#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VERSION="${VERSION:?JitPack VERSION is required}"
[ "${GROUP:-com.github.woncan}" = "com.github.woncan" ] || { echo "unexpected GROUP: ${GROUP:-}" >&2; exit 1; }
[ "${ARTIFACT:-device}" = "device" ] || { echo "unexpected ARTIFACT: ${ARTIFACT:-}" >&2; exit 1; }

POM_VERSION="$(sed -n 's:.*<version>\([^<]*\)</version>.*:\1:p' "$ROOT/prebuilt/device.pom" | head -1)"
[ "$POM_VERSION" = "$VERSION" ] || { echo "tag VERSION $VERSION != POM $POM_VERSION" >&2; exit 1; }

EXPECTED="$(awk '{print $1}' "$ROOT/prebuilt/device.aar.sha256")"
ACTUAL="$(sha256sum "$ROOT/prebuilt/device.aar" | awk '{print $1}')"
[ "$ACTUAL" = "$EXPECTED" ] || { echo "prebuilt AAR SHA-256 mismatch" >&2; exit 1; }

M2_ROOT="${MAVEN_REPO_LOCAL:-$HOME/.m2/repository}"
if command -v cygpath >/dev/null 2>&1; then
  M2_ROOT="$(cygpath -u "$M2_ROOT")"
fi
DEST="$M2_ROOT/com/github/woncan/device/$VERSION"
mkdir -p "$DEST"
cp "$ROOT/prebuilt/device.aar" "$DEST/device-$VERSION.aar"
cp "$ROOT/prebuilt/device.pom" "$DEST/device-$VERSION.pom"
echo "installed exact device:$VERSION AAR ($ACTUAL) into $DEST"
