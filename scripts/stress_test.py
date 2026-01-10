#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
import concurrent.futures
import json
import os
import random
import statistics
import string
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from datetime import datetime
from typing import Any, Dict, List, Optional, Tuple


@dataclass
class RequestResult:
    ok: bool
    http_status: int
    latency_ms: float
    business_code: Optional[int] = None
    business_message: Optional[str] = None
    error: Optional[str] = None


def _now_ts() -> str:
    return datetime.now().strftime("%Y%m%d-%H%M%S")


def _percentile(sorted_values: List[float], p: float) -> float:
    if not sorted_values:
        return 0.0
    if p <= 0:
        return sorted_values[0]
    if p >= 100:
        return sorted_values[-1]
    k = (len(sorted_values) - 1) * (p / 100.0)
    f = int(k)
    c = min(f + 1, len(sorted_values) - 1)
    if f == c:
        return sorted_values[f]
    d0 = sorted_values[f] * (c - k)
    d1 = sorted_values[c] * (k - f)
    return d0 + d1


def _http_json(
    method: str,
    url: str,
    headers: Dict[str, str],
    body: Optional[Dict[str, Any]] = None,
    timeout_s: float = 10.0,
) -> Tuple[int, str, Optional[Dict[str, Any]]]:
    data = None
    if body is not None:
        encoded = json.dumps(body, ensure_ascii=False).encode("utf-8")
        data = encoded
        headers = {**headers, "Content-Type": "application/json"}

    req = urllib.request.Request(url=url, data=data, method=method.upper())
    for k, v in headers.items():
        req.add_header(k, v)

    try:
        with urllib.request.urlopen(req, timeout=timeout_s) as resp:
            raw = resp.read().decode("utf-8", errors="replace")
            try:
                parsed = json.loads(raw)
            except Exception:
                parsed = None
            return resp.getcode(), raw, parsed
    except urllib.error.HTTPError as e:
        raw = ""
        try:
            raw = e.read().decode("utf-8", errors="replace")
        except Exception:
            raw = str(e)
        try:
            parsed = json.loads(raw)
        except Exception:
            parsed = None
        return e.code, raw, parsed


def _rand_username(prefix: str = "lt") -> str:
    suffix = "".join(random.choices(string.ascii_lowercase + string.digits, k=8))
    return f"{prefix}_{int(time.time())}_{suffix}"


def register_and_login(gateway_base: str, username: str, password: str) -> Tuple[str, int]:
    register_url = gateway_base.rstrip("/") + "/user/register"
    login_url = gateway_base.rstrip("/") + "/user/login"

    _http_json(
        "POST",
        register_url,
        headers={},
        body={
            "username": username,
            "password": password,
            "phone": "13800138000",
            "email": f"{username}@example.com",
        },
        timeout_s=10.0,
    )

    status, raw, parsed = _http_json(
        "POST",
        login_url,
        headers={},
        body={"username": username, "password": password},
        timeout_s=10.0,
    )
    if status != 200 or not isinstance(parsed, dict):
        raise RuntimeError(f"login failed: http={status}, body={raw[:300]}")

    code = parsed.get("code")
    if code != 200:
        raise RuntimeError(f"login failed: business code={code}, body={raw[:300]}")

    data = parsed.get("data") or {}
    token = data.get("token")
    user_id = data.get("userId") or data.get("id") or 0
    if not token:
        raise RuntimeError(f"login response missing token: {raw[:300]}")

    return token, int(user_id)


def build_request(
    gateway_base: str,
    api: str,
    schedule_id: int,
    mode: str,
    idx: int,
) -> Tuple[str, str, Optional[Dict[str, Any]]]:
    base = gateway_base.rstrip("/")

    if api == "movie_list":
        url = base + "/movie/list?current=1&size=10"
        return "GET", url, None

    if api == "seat_lock":
        url = base + "/seat/lock"
        seat = "A1" if mode == "conflict" else f"T{idx}"
        body = {"scheduleId": schedule_id, "seatNumbers": [seat]}
        return "POST", url, body

    if api == "order_create":
        url = base + "/order/create"
        seat = "A1" if mode == "conflict" else f"O{idx}"
        body = {"scheduleId": schedule_id, "seatNumbers": [seat]}
        return "POST", url, body

    raise ValueError(f"unknown api: {api}")


def run_one(
    method: str,
    url: str,
    headers: Dict[str, str],
    body: Optional[Dict[str, Any]],
    timeout_s: float,
) -> RequestResult:
    t0 = time.perf_counter()
    try:
        status, raw, parsed = _http_json(method, url, headers=headers, body=body, timeout_s=timeout_s)
        latency_ms = (time.perf_counter() - t0) * 1000.0

        business_code = None
        business_message = None
        ok = False

        if isinstance(parsed, dict):
            business_code = parsed.get("code")
            business_message = parsed.get("message")
            ok = (status == 200) and (business_code == 200)
        else:
            ok = status == 200

        return RequestResult(
            ok=ok,
            http_status=status,
            latency_ms=latency_ms,
            business_code=business_code,
            business_message=business_message,
            error=None if ok else (raw[:300] if raw else None),
        )
    except Exception as e:
        latency_ms = (time.perf_counter() - t0) * 1000.0
        return RequestResult(ok=False, http_status=0, latency_ms=latency_ms, error=str(e))


def summarize(results: List[RequestResult]) -> Dict[str, Any]:
    total = len(results)
    ok_count = sum(1 for r in results if r.ok)
    fail_count = total - ok_count

    latencies = [r.latency_ms for r in results]
    latencies_sorted = sorted(latencies)

    http_dist: Dict[str, int] = {}
    biz_dist: Dict[str, int] = {}
    for r in results:
        http_dist[str(r.http_status)] = http_dist.get(str(r.http_status), 0) + 1
        if r.business_code is not None:
            biz_dist[str(r.business_code)] = biz_dist.get(str(r.business_code), 0) + 1

    avg = statistics.mean(latencies) if latencies else 0.0
    p50 = _percentile(latencies_sorted, 50)
    p95 = _percentile(latencies_sorted, 95)
    p99 = _percentile(latencies_sorted, 99)
    mx = max(latencies) if latencies else 0.0

    return {
        "total": total,
        "ok": ok_count,
        "fail": fail_count,
        "success_rate": (ok_count / total * 100.0) if total else 0.0,
        "latency_ms": {
            "avg": avg,
            "p50": p50,
            "p95": p95,
            "p99": p99,
            "max": mx,
        },
        "http_status_dist": dict(sorted(http_dist.items(), key=lambda x: int(x[0]) if x[0].isdigit() else 9999)),
        "business_code_dist": dict(sorted(biz_dist.items(), key=lambda x: int(x[0]) if x[0].isdigit() else 9999)),
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Movie Ticket System stress test (no external deps).")
    parser.add_argument("--gateway", default="http://localhost:9999/api", help="Gateway base url, e.g. http://localhost:9999/api")
    parser.add_argument("--api", choices=["movie_list", "seat_lock", "order_create"], required=True)
    parser.add_argument("--schedule-id", type=int, default=1)
    parser.add_argument("--mode", choices=["unique", "conflict"], default="unique", help="unique seats or conflict on same seat")
    parser.add_argument("--concurrency", type=int, default=20)
    parser.add_argument("--requests", type=int, default=200)
    parser.add_argument("--timeout", type=float, default=10.0)
    parser.add_argument("--out", default="stress-results", help="Output dir")
    parser.add_argument("--no-auth", action="store_true", help="Do not login / do not attach Authorization header")

    args = parser.parse_args()

    random.seed(42)

    headers: Dict[str, str] = {}
    auth_info: Dict[str, Any] = {}

    if not args.no_auth and args.api in ("seat_lock", "order_create"):
        username = _rand_username("load")
        password = "123456"
        token, user_id = register_and_login(args.gateway, username, password)
        headers["Authorization"] = f"Bearer {token}"
        auth_info = {"username": username, "userId": user_id}

    req_defs = []
    for i in range(args.requests):
        method, url, body = build_request(args.gateway, args.api, args.schedule_id, args.mode, i)
        req_defs.append((method, url, body))

    results: List[RequestResult] = []
    start = time.perf_counter()

    with concurrent.futures.ThreadPoolExecutor(max_workers=args.concurrency) as ex:
        futs = [ex.submit(run_one, m, u, headers, b, args.timeout) for (m, u, b) in req_defs]
        for fut in concurrent.futures.as_completed(futs):
            results.append(fut.result())

    duration_s = time.perf_counter() - start
    summary = summarize(results)
    summary["duration_s"] = duration_s
    summary["rps"] = (summary["total"] / duration_s) if duration_s > 0 else 0.0

    meta = {
        "timestamp": _now_ts(),
        "gateway": args.gateway,
        "api": args.api,
        "mode": args.mode,
        "scheduleId": args.schedule_id,
        "concurrency": args.concurrency,
        "requests": args.requests,
        "timeout": args.timeout,
        "auth": auth_info,
        "summary": summary,
    }

    os.makedirs(args.out, exist_ok=True)
    out_file = os.path.join(args.out, f"{meta['timestamp']}_{args.api}_{args.mode}_c{args.concurrency}_n{args.requests}.json")
    with open(out_file, "w", encoding="utf-8") as f:
        json.dump(meta, f, ensure_ascii=False, indent=2)

    print(f"api={args.api} mode={args.mode} concurrency={args.concurrency} requests={args.requests}")
    print(f"duration_s={duration_s:.3f} rps={summary['rps']:.2f}")
    print(f"success={summary['ok']}/{summary['total']} ({summary['success_rate']:.2f}%)")
    lm = summary["latency_ms"]
    print(f"latency_ms avg={lm['avg']:.2f} p50={lm['p50']:.2f} p95={lm['p95']:.2f} p99={lm['p99']:.2f} max={lm['max']:.2f}")
    print(f"http_status_dist={summary['http_status_dist']}")
    if summary["business_code_dist"]:
        print(f"business_code_dist={summary['business_code_dist']}")
    print(f"output={out_file}")

    return 0 if summary["fail"] == 0 else 2


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except KeyboardInterrupt:
        raise SystemExit(130)
