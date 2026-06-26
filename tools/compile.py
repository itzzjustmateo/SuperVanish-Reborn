#!/usr/bin/env -S uv run
"""Build the SuperVanish Reborn plugin using the Gradle wrapper.

Usage:
    uv run tools/compile.py              # Build the shadow jar (default)
    uv run tools/compile.py --clean      # Clean then build
    uv run tools/compile.py --test       # Run tests
    uv run tools/compile.py --jar        # Just produce the jar
"""

import argparse
import subprocess
import sys
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parent.parent
GRADLEW = REPO_ROOT / "gradlew"


def run_gradle(*args: str) -> int:
    cmd = [str(GRADLEW), *args]
    print(f"> {' '.join(cmd)}", file=sys.stderr)
    result = subprocess.run(cmd, cwd=REPO_ROOT)
    return result.returncode


def main() -> int:
    parser = argparse.ArgumentParser(description="Build SuperVanish Reborn")
    parser.add_argument("--clean", action="store_true", help="Clean before building")
    parser.add_argument("--test", action="store_true", help="Run tests")
    parser.add_argument("--jar", action="store_true",
                        help="Only produce the shadow jar (no tests)")
    args = parser.parse_args()

    tasks = []

    if args.clean:
        tasks.append("clean")

    if args.test:
        tasks.append("test")
    elif args.jar:
        tasks.append("shadowJar")
    else:
        tasks.append("build")

    return run_gradle(*tasks)


if __name__ == "__main__":
    sys.exit(main())
