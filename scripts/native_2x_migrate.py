#!/usr/bin/env python3
"""Replace Constants.f/i/v/font() literal args with native 2x pixel values."""
import re
import os

ROOT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "java", "org", "test")

LIT = r"(?:0x[0-9a-fA-F]+|\d+(?:\.\d+)?f?)"

def parse_num(s):
    s = s.strip()
    if s.lower().startswith("0x"):
        return int(s, 16)
    if "." in s or s.endswith("f") or s.endswith("F"):
        return float(s.rstrip("fF"))
    return int(s)

def fmt_num(n, original):
    orig = original.strip()
    if isinstance(n, float) or "." in orig or orig.endswith("f") or orig.endswith("F"):
        v = float(n)
        if v == int(v):
            return f"{int(v)}f"
        return f"{v}g".replace("g", "") + "f" if False else (f"{v:.1f}f" if v != int(v) else f"{int(v)}f")
    if "0x" in orig.lower():
        return hex(int(n))
    return str(int(n))

def double_lit(expr):
    n = parse_num(expr)
    return fmt_num(n * 2, expr)

def process_content(text):
    text = text.replace("/ Constants.f(2f)", "/ 2f")

    text = re.sub(
        rf"Constants\.font\(\s*({LIT})\s*\)",
        lambda m: f"Constants.font({double_lit(m.group(1))})",
        text,
    )
    text = re.sub(
        rf"Constants\.v\(\s*({LIT})\s*,\s*({LIT})\s*\)",
        lambda m: f"new Vector2f({double_lit(m.group(1))}, {double_lit(m.group(2))})",
        text,
    )
    text = re.sub(
        rf"Constants\.i\(\s*({LIT})\s*\)",
        lambda m: double_lit(m.group(1)),
        text,
    )
    text = re.sub(
        rf"Constants\.f\(\s*({LIT})\s*\)",
        lambda m: double_lit(m.group(1)),
        text,
    )
    return text

SKIP = {"Constants.java"}

def main():
    for name in sorted(os.listdir(ROOT)):
        if not name.endswith(".java") or name in SKIP:
            continue
        path = os.path.join(ROOT, name)
        with open(path, "r", encoding="utf-8") as f:
            original = f.read()
        updated = process_content(original)
        if updated != original:
            with open(path, "w", encoding="utf-8") as f:
                f.write(updated)
            print("updated", name)

if __name__ == "__main__":
    main()
