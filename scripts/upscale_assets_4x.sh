#!/usr/bin/env bash
#
# Batch upscale app/src/main/assets PNGs to 4x with Real-ESRGAN.
# Original assets are never modified; output goes to app/src/main/assets_4x/
#
# Setup (your layout):
#   SK/Tools/realesrgan-ncnn-vulkan/realesrgan-ncnn-vulkan
#   SK/Tools/models/
#
# Usage:
#   ./scripts/upscale_assets_4x.sh
#   ./scripts/upscale_assets_4x.sh --resume    # only missing outputs (e.g. subdirs)
#   ./scripts/upscale_assets_4x.sh --dry-run
#   ./scripts/upscale_assets_4x.sh --output app/src/main/assets_hd
#
# Optional env:
#   REALESRGAN_BIN=.../realesrgan-ncnn-vulkan
#   REALESRGAN_MODELS=.../models
#   REALESRGAN_MODEL=realesrgan-x4plus
#
# After success: point the game at assets_4x (or swap folders manually).

set -euo pipefail

SCALE=4
MODEL="${REALESRGAN_MODEL:-realesrgan-x4plus}"
ASSETS_REL="app/src/main/assets"
OUTPUT_REL="app/src/main/assets_4x"
DRY_RUN=false
RESUME=false
FRESH=false

usage() {
  cat <<'EOF'
Batch 4x upscale: assets -> assets_4x (original assets unchanged).

Usage:
  ./scripts/upscale_assets_4x.sh [--dry-run] [--output DIR]

Options:
  --dry-run       List PNG files only
  --resume        Skip PNGs that already exist in output (fill gaps only)
  --fresh         Delete output folder before run (default on first full run)
  --output DIR    Output folder (default: app/src/main/assets_4x)
  -h, --help      Show this help

Environment:
  REALESRGAN_BIN      Path to realesrgan-ncnn-vulkan binary
  REALESRGAN_MODELS   Path to models/ (.param + .bin)
  REALESRGAN_MODEL    Model name (default: realesrgan-x4plus)
EOF
  exit "${1:-0}"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage 0 ;;
    --dry-run) DRY_RUN=true; shift ;;
    --resume) RESUME=true; shift ;;
    --fresh) FRESH=true; shift ;;
    --keep|--no-replace) shift ;; # no-op; originals are always kept
    --output)
      shift
      [[ $# -gt 0 ]] || { echo "--output requires a directory" >&2; exit 1; }
      OUTPUT_REL="$1"
      shift
      ;;
    --output=*) OUTPUT_REL="${1#--output=}"; shift ;;
    *) echo "Unknown option: $1" >&2; usage 1 ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ASSETS_DIR="$ROOT_DIR/$ASSETS_REL"
if [[ "$OUTPUT_REL" == /* ]]; then
  OUTPUT_DIR="$OUTPUT_REL"
else
  OUTPUT_DIR="$ROOT_DIR/$OUTPUT_REL"
fi

find_realesrgan_bin() {
  if [[ -n "${REALESRGAN_BIN:-}" && -x "${REALESRGAN_BIN}" ]]; then
    echo "${REALESRGAN_BIN}"
    return 0
  fi
  local candidate
  for candidate in \
    "$ROOT_DIR/Tools/realesrgan-ncnn-vulkan/realesrgan-ncnn-vulkan" \
    "$ROOT_DIR/tools/realesrgan-ncnn-vulkan/realesrgan-ncnn-vulkan"; do
    if [[ -x "$candidate" ]]; then
      echo "$candidate"
      return 0
    fi
  done
  candidate="$(find "$ROOT_DIR/Tools" "$ROOT_DIR/tools" -type f -name 'realesrgan-ncnn-vulkan' 2>/dev/null | head -n 1)"
  if [[ -n "$candidate" && -x "$candidate" ]]; then
    echo "$candidate"
    return 0
  fi
  local name
  for name in realesrgan-ncnn-vulkan; do
    if command -v "$name" >/dev/null 2>&1; then
      command -v "$name"
      return 0
    fi
  done
  return 1
}

find_models_dir() {
  local bin="$1"
  local bin_dir parent_dir
  bin_dir="$(cd "$(dirname "$bin")" && pwd)"
  parent_dir="$(cd "$bin_dir/.." && pwd)"

  if [[ -n "${REALESRGAN_MODELS:-}" && -d "${REALESRGAN_MODELS}" ]]; then
    echo "${REALESRGAN_MODELS}"
    return 0
  fi
  if [[ -f "$bin_dir/models/realesrgan-x4plus.param" ]]; then
    echo "$bin_dir/models"
    return 0
  fi
  if [[ -f "$parent_dir/models/realesrgan-x4plus.param" ]]; then
    echo "$parent_dir/models"
    return 0
  fi
  if [[ -f "$ROOT_DIR/Tools/models/realesrgan-x4plus.param" ]]; then
    echo "$ROOT_DIR/Tools/models"
    return 0
  fi
  return 1
}

run_file_upscale() {
  local bin="$1"
  local models="$2"
  local input_file="$3"
  local output_file="$4"
  mkdir -p "$(dirname "$output_file")"
  if "$bin" -m "$models" -i "$input_file" -o "$output_file" -n "$MODEL" -s "$SCALE" 2>/dev/null; then
    return 0
  fi
  if "$bin" -m "$models" -i "$input_file" -o "$output_file" -n "$MODEL" 2>/dev/null; then
    return 0
  fi
  return 1
}

if [[ ! -d "$ASSETS_DIR" ]]; then
  echo "Assets directory not found: $ASSETS_DIR" >&2
  exit 1
fi

PNG_COUNT="$(find "$ASSETS_DIR" -type f -name '*.png' | wc -l | tr -d ' ')"
if [[ "$PNG_COUNT" -eq 0 ]]; then
  echo "No PNG files under $ASSETS_DIR" >&2
  exit 1
fi

echo "Project:  $ROOT_DIR"
echo "Input:    $ASSETS_DIR ($PNG_COUNT PNG files)"
echo "Output:   $OUTPUT_DIR"
echo "Scale:    ${SCALE}x"
echo "Model:    $MODEL"
echo "Note:     original assets will NOT be modified"
echo "Mode:     per-file (includes subfolders)"
[[ "$RESUME" == true ]] && echo "          --resume: skip existing outputs"
echo

if [[ "$DRY_RUN" == true ]]; then
  find "$ASSETS_DIR" -type f -name '*.png' | sort | sed "s|^$ASSETS_DIR/||"
  exit 0
fi

if ! REALESRGAN_BIN_PATH="$(find_realesrgan_bin)"; then
  cat >&2 <<EOF
Real-ESRGAN CLI not found.

Set REALESRGAN_BIN, e.g.:
  export REALESRGAN_BIN="$ROOT_DIR/Tools/realesrgan-ncnn-vulkan/realesrgan-ncnn-vulkan"
EOF
  exit 1
fi

if ! MODELS_DIR="$(find_models_dir "$REALESRGAN_BIN_PATH")"; then
  cat >&2 <<EOF
models/ not found (need realesrgan-x4plus.param).

Set REALESRGAN_MODELS, e.g.:
  export REALESRGAN_MODELS="$ROOT_DIR/Tools/models"
EOF
  exit 1
fi

echo "Binary:   $REALESRGAN_BIN_PATH"
echo "Models:   $MODELS_DIR"
echo
if [[ "$FRESH" == true ]]; then
  rm -rf "$OUTPUT_DIR"
fi
mkdir -p "$OUTPUT_DIR"

OK=0
SKIP=0
FAIL=0
INDEX=0

while IFS= read -r src; do
  INDEX=$((INDEX + 1))
  rel="${src#"$ASSETS_DIR"/}"
  dest="$OUTPUT_DIR/$rel"
  if [[ "$RESUME" == true && -f "$dest" ]]; then
    SKIP=$((SKIP + 1))
    continue
  fi
  echo "[$INDEX/$PNG_COUNT] $rel"
  if run_file_upscale "$REALESRGAN_BIN_PATH" "$MODELS_DIR" "$src" "$dest"; then
    OK=$((OK + 1))
  else
    echo "  failed: $rel" >&2
    FAIL=$((FAIL + 1))
  fi
done < <(find "$ASSETS_DIR" -type f -name '*.png' | sort)

OUT_COUNT="$(find "$OUTPUT_DIR" -type f -name '*.png' | wc -l | tr -d ' ')"
echo
echo "Done."
echo "  Input PNGs:   $PNG_COUNT"
echo "  Output PNGs:  $OUT_COUNT"
echo "  Processed:    $OK  skipped: $SKIP  failed: $FAIL"
echo "  Folder:       $OUTPUT_DIR"
echo
if [[ "$OUT_COUNT" -lt "$PNG_COUNT" ]]; then
  echo "Warning: output count < input count. Re-run with --resume after fixing errors." >&2
  exit 1
fi

cat <<EOF
Next steps:
  1. Review images under: $OUTPUT_REL
  2. To use in the game, either:
       - Replace assets manually after backup, or
       - Change Gradle/assets path to point at assets_4x
  3. MainActivity: setting.width = 1280; setting.height = 1920;

EOF
