#!/usr/bin/env -S uv run
"""Generate a changelog between the last release tag and HEAD.

Outputs Markdown suitable for a GitHub release body.

Usage:
    uv run python3 tools/generate_changelog.py [version]
"""

import subprocess
import sys
from collections.abc import Sequence
from collections import defaultdict


def run(cmd: Sequence[str]) -> str:
    result = subprocess.run(cmd, capture_output=True, text=True, check=True)
    return result.stdout.strip()


def get_last_tag() -> str | None:
    result = subprocess.run(
        ["git", "tag", "--list", "v*", "--sort=-version:refname"],
        capture_output=True, text=True, check=False,
    )
    tags = result.stdout.strip().splitlines()
    if tags:
        head_ref = run(["git", "rev-parse", "HEAD"])
        for t in tags:
            tag_ref = run(["git", "rev-list", "-n", "1", t])
            if tag_ref != head_ref:
                return t
    return None


def get_commits_since(tag: str | None) -> list[dict]:
    if tag:
        rev_range = f"{tag}..HEAD"
    else:
        rev_range = "--all"

    raw = run(["git", "log", rev_range, "--pretty=format:%H||%an||%s"])
    commits = []
    for line in raw.splitlines():
        parts = line.split("||", 2)
        if len(parts) == 3:
            commits.append({
                "hash": parts[0][:7],
                "author": parts[1],
                "message": parts[2],
            })
    return commits


def get_new_contributors(commits: list[dict], last_tag: str | None) -> set[str]:
    all_authors = set()
    if last_tag:
        result = subprocess.run(
            ["git", "log", last_tag, "--pretty=format:%an"],
            capture_output=True, text=True, check=False,
        )
        for line in result.stdout.strip().splitlines():
            all_authors.add(line)
    current_authors = {c["author"] for c in commits}
    return current_authors - all_authors if all_authors else set()


def categorize(message: str) -> str:
    msg_lower = message.lower()
    if msg_lower.startswith(("feat", "feature")):
        return "Features"
    if msg_lower.startswith("fix"):
        return "Bug Fixes"
    if msg_lower.startswith(("chore", "build")):
        return "Chores"
    if msg_lower.startswith(("docs", "doc")):
        return "Documentation"
    if msg_lower.startswith("refactor"):
        return "Refactoring"
    if msg_lower.startswith("test"):
        return "Tests"
    if msg_lower.startswith("perf"):
        return "Performance"
    if msg_lower.startswith("ci"):
        return "CI/CD"
    return "Other"


def main():
    version = sys.argv[1] if len(sys.argv) > 1 else None
    last_tag = get_last_tag()
    if last_tag:
        print(f"## Changes since {last_tag}\n")
    else:
        print("## Initial Release\n")

    commits = get_commits_since(last_tag)

    if not commits:
        print("No new commits.")
        return

    new_contributors = get_new_contributors(commits, last_tag)

    categories: dict[str, list[str]] = defaultdict(list)
    for c in commits:
        cat = categorize(c["message"])
        categories[cat].append(f"- {c['message']} ({c['author']}, {c['hash']})")

    for cat in ["Features", "Bug Fixes", "Refactoring", "Performance", "CI/CD",
                 "Documentation", "Tests", "Chores", "Other"]:
        items = categories.get(cat)
        if items:
            print(f"### {cat}\n")
            for item in items:
                print(item)
            print()

    if new_contributors:
        names = ", ".join(sorted(new_contributors))
        print(f"### New Contributors\n")
        print(f"Welcome to {names}!")
        print()

    if version and last_tag:
        print(f"**Full Changelog**: https://github.com/itzzjustmateo/SuperVanish-Reborn/compare/"
              f"{last_tag}...v{version}")
    elif last_tag:
        print(f"**Full Changelog**: https://github.com/itzzjustmateo/SuperVanish-Reborn/compare/"
              f"{last_tag}...HEAD")


if __name__ == "__main__":
    main()
