#!/usr/bin/env bash
#
# macOS ncnn zip often has NO models/ — copy from the Ubuntu release zip.
#
# Usage:
#   ./scripts/download_realesrgan_models.sh [/path/to/realesrgan-ncnn-vulkan-dir]
#
# Default dir: tools/realesrgan-ncnn-vulkan (next to the binary)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
TARGET_DIR="${1:-$ROOT_DIR/tools/realesrgan-ncnn-vulkan}"
ZIP_URL="https://github.com/xinntao/Real-ESRGAN/releases/download/v0.2.5.0/realesrgan-ncnn-vulkan-20220424-ubuntu.zip"
TMP_ZIP="$(mktemp -t realesrgan-ubuntu-models.XXXXXX.zip)"
TMP_DIR="$(mktemp -d -t realesrgan-models.XXXXXX)"

cleanup() {
  rm -f "$TMP_ZIP"
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT

if [[ ! -x "$TARGET_DIR/realesrgan-ncnn-vulkan" ]]; then
  echo "Binary not found: $TARGET_DIR/realesrgan-ncnn-vulkan" >&2
  echo "Put your macOS realesrgan-ncnn-vulkan there first, then re-run." >&2
  exit 1
fi

echo "Target:   $TARGET_DIR"
echo "Download: $ZIP_URL"
curl -fL --progress-bar "$ZIP_URL" -o "$TMP_ZIP"
unzip -q "$TMP_ZIP" -d "$TMP_DIR"

SRC_MODELS="$(find "$TMP_DIR" -type d -name models | head -n 1)"
if [[ -z "$SRC_MODELS" || ! -f "$SRC_MODELS/realesrgan-x4plus.param" ]]; then
  echo "models/ not found inside ubuntu zip" >&2
  exit 1
fi

rm -rf "$TARGET_DIR/models"
cp -R "$SRC_MODELS" "$TARGET_DIR/models"
echo
echo "Installed: $TARGET_DIR/models"
ls "$TARGET_DIR/models" | head -20
echo
echo "Test:"
echo "  cd \"$TARGET_DIR\""
echo "  ./realesrgan-ncnn-vulkan -m \"\$(pwd)/models\" -i input.png -o output.png -n realesrgan-x4plus -s 4"
