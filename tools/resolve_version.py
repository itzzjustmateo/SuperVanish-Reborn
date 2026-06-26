#!/usr/bin/env -S uv run
"""Resolve the next release version.

Reads the current version from build.gradle.kts, checks existing Git tags,
and auto-increments the patch version if the current version already exists
as a tag.

Usage:
    uv run python3 tools/resolve_version.py
"""

import re
import subprocess
import sys


def get_current_version() -> str:
    with open("build.gradle.kts") as f:
        content = f.read()
    m = re.search(r'version\s*=\s*"([^"]+)"', content)
    if not m:
        print("ERROR: could not find version in build.gradle.kts", file=sys.stderr)
        sys.exit(1)
    return m.group(1)


def get_existing_tags() -> set[str]:
    result = subprocess.run(
        ["git", "tag", "--list", "v*"],
        capture_output=True, text=True, check=False,
    )
    return set(result.stdout.strip().splitlines())


def bump_version(version: str) -> str:
    parts = version.split(".")
    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
    return f"{major}.{minor}.{patch + 1}"


def main():
    version = get_current_version()
    tags = get_existing_tags()

    while f"v{version}" in tags:
        version = bump_version(version)

    print(version)


if __name__ == "__main__":
    main()
