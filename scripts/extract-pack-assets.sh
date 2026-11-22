#!/usr/bin/env bash
# Fills pack-assets/ from an existing Motion & Bits jar.
# Usage: scripts/extract-pack-assets.sh path/to/motionandbits.jar
set -euo pipefail

jar="${1:?usage: $0 path/to/motionandbits.jar}"
root="$(cd "$(dirname "$0")/.." && pwd)"
out="$root/pack-assets"

rm -rf "$out"
mkdir -p "$out"
unzip -q "$jar" 'assets/*' 'ans_overrides/*' 'resourcepacks/*' -d "$out"

# Files that belong to the mod itself already live in src/main/resources.
rm -rf "$out/assets/ans/lang" "$out/assets/ans/shaders" "$out/assets/ans_ui" "$out/assets/ans_viewmodel"

echo "pack-assets: $(find "$out" -type f | wc -l) files"
