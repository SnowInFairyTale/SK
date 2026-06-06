#!/usr/bin/env bash
#
# Sharpen gems: upscale to 4x (Real-ESRGAN) -> downscale to 2x (ffmpeg Lanczos) -> overwrite originals.
#
# Pipeline (per PNG under app/src/main/gems/):
#   jewel_*.png  --4x ESRGAN-->  .build/gems_pipeline/4x/
#                --1/2 ffmpeg-->  .build/gems_pipeline/2x/
#                --copy-------->  app/src/main/gems/  (in-place replace)
#
# Usage:
#   ./scripts/process_gems.sh
#   ./scripts/process_gems.sh --dry-run
#
# Requires: Tools/realesrgan-ncnn-vulkan, ffmpeg (same as upscale/downscale scripts)

set -euo pipefail

GEMS_REL="app/src/main/gems"
WORK_REL=".build/gems_pipeline"
STAGE_4X_REL="$WORK_REL/4x"
STAGE_2X_REL="$WORK_REL/2x"
SCALE=4
MODEL="${REALESRGAN_MODEL:-realesrgan-x4plus}"
DRY_RUN=false

usage() {
  cat <<'EOF'
Upscale gems to 4x, ffmpeg downscale to 2x, overwrite app/src/main/gems/*.png.

Usage:
  ./scripts/process_gems.sh [--dry-run] [-h|--help]

Options:
  --dry-run   Show planned sizes only; do not modify files
  -h, --help  Show this help

Environment (optional):
  REALESRGAN_BIN, REALESRGAN_MODELS, REALESRGAN_MODEL
EOF
  exit "${1:-0}"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage 0 ;;
    --dry-run) DRY_RUN=true; shift ;;
    *) echo "Unknown option: $1" >&2; usage 1 ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
GEMS_DIR="$ROOT_DIR/$GEMS_REL"
STAGE_4X_DIR="$ROOT_DIR/$STAGE_4X_REL"
STAGE_2X_DIR="$ROOT_DIR/$STAGE_2X_REL"
DOWNSCALE_SCRIPT="$SCRIPT_DIR/downscale_assets_ffmpeg.sh"

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
  if command -v realesrgan-ncnn-vulkan >/dev/null 2>&1; then
    command -v realesrgan-ncnn-vulkan
    return 0
  fi
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

image_size() {
  ffprobe -v error -select_streams v:0 -show_entries stream=width,height \
    -of csv=p=0:s=x "$1"
}

if [[ ! -d "$GEMS_DIR" ]]; then
  echo "Gems directory not found: $GEMS_DIR" >&2
  exit 1
fi

FILES=()
while IFS= read -r f; do
  FILES+=("$f")
done < <(find "$GEMS_DIR" -maxdepth 1 -type f -name '*.png' | sort)
if [[ ${#FILES[@]} -eq 0 ]]; then
  echo "No PNG files under $GEMS_DIR" >&2
  exit 1
fi

if [[ ! -x "$DOWNSCALE_SCRIPT" ]]; then
  echo "Downscale script not found: $DOWNSCALE_SCRIPT" >&2
  exit 1
fi

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "ffmpeg not found on PATH." >&2
  exit 1
fi

echo "Project:  $ROOT_DIR"
echo "Gems:     $GEMS_DIR (${#FILES[@]} PNGs)"
echo "Pipeline: 4x Real-ESRGAN -> 1/2 ffmpeg Lanczos -> overwrite gems"
echo

if $DRY_RUN; then
  for src in "${FILES[@]}"; do
    w=$(ffprobe -v error -select_streams v:0 -show_entries stream=width -of csv=p=0 "$src")
    h=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of csv=p=0 "$src")
    nw4=$(( w * 4 ))
    nh4=$(( h * 4 ))
    nw2=$(( (nw4 / 2 / 2) * 2 ))
    nh2=$(( (nh4 / 2 / 2) * 2 ))
    printf '  %s  %sx%s -> 4x %sx%s -> 2x %sx%s\n' \
      "$(basename "$src")" "$w" "$h" "$nw4" "$nh4" "$nw2" "$nh2"
  done
  echo
  echo "Dry run complete. Re-run without --dry-run to process."
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

echo "ESRGAN:   $REALESRGAN_BIN_PATH"
echo "Models:   $MODELS_DIR"
echo "Model:    $MODEL"
echo

rm -rf "$STAGE_4X_DIR" "$STAGE_2X_DIR"
mkdir -p "$STAGE_4X_DIR" "$STAGE_2X_DIR"

INDEX=0
for src in "${FILES[@]}"; do
  INDEX=$((INDEX + 1))
  name="$(basename "$src")"
  dest_4x="$STAGE_4X_DIR/$name"
  echo "[$INDEX/${#FILES[@]}] 4x upscale: $name ($(image_size "$src"))"
  if ! run_file_upscale "$REALESRGAN_BIN_PATH" "$MODELS_DIR" "$src" "$dest_4x"; then
    echo "  failed: $name" >&2
    exit 1
  fi
  echo "  -> $(image_size "$dest_4x")"
done

echo
echo "Downscale 4x -> 2x (ffmpeg Lanczos):"
"$DOWNSCALE_SCRIPT" --input "$STAGE_4X_REL" --output "$STAGE_2X_REL" --scale 2 --no-backup

echo
echo "Overwrite gems:"
for src in "${FILES[@]}"; do
  name="$(basename "$src")"
  processed="$STAGE_2X_DIR/$name"
  if [[ ! -f "$processed" ]]; then
    echo "Missing output: $processed" >&2
    exit 1
  fi
  cp -f "$processed" "$src"
  printf '  %s  %s -> %s\n' "$name" "$(image_size "$processed")" "$(image_size "$src")"
done

echo
echo "Done. Updated ${#FILES[@]} file(s) under $GEMS_REL"
echo "Work copies kept at: $STAGE_4X_REL, $STAGE_2X_REL"
