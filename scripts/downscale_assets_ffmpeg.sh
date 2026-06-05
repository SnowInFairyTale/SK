#!/usr/bin/env bash
#
# Downscale PNG assets with ffmpeg (Lanczos — sharp when shrinking).
# Default pipeline: assets_4x (4x) -> assets at 1/2 size (2x vs originals — sharper than x1).
#
# Usage:
#   ./scripts/downscale_assets_ffmpeg.sh              # all PNGs under assets_4x -> assets
#   ./scripts/downscale_assets_ffmpeg.sh --dry-run
#   ./scripts/downscale_assets_ffmpeg.sh --resume     # skip outputs that already exist
#   ./scripts/downscale_assets_ffmpeg.sh --scale 4    # down to 1x (original game resolution)
#   ./scripts/downscale_assets_ffmpeg.sh path/to/a.png
#
# Requires: ffmpeg on PATH

set -euo pipefail

SCALE_NUM=1
SCALE_DEN=2
SRC_REL="app/src/main/assets_4x"
OUTPUT_REL="app/src/main/assets"
DRY_RUN=false
IN_PLACE=false
RESUME=false
BACKUP_SUFFIX=".bak"
EXTRA_FILES=()

usage() {
  cat <<'EOF'
Downscale PNGs with ffmpeg (Lanczos scaler).

Default: every PNG under app/src/main/assets_4x -> app/src/main/assets at 1/2
dimensions (4x -> 2x, keeps higher clarity than shrinking to 1x). assets_4x is not modified.

Usage:
  ./scripts/downscale_assets_ffmpeg.sh [options] [FILE ...]

Options:
  --dry-run       List files and planned output sizes only
  --resume        Skip PNGs that already exist in output
  --scale N       Divide width/height by N (default: 2, i.e. 4x -> 2x)
  --input DIR     Source folder (default: app/src/main/assets_4x)
  --output DIR    Output folder (default: app/src/main/assets)
  --in-place      Replace files under --input (creates FILE.bak backups first)
  --no-backup     With --in-place, skip .bak backups (destructive)
  -h, --help      Show this help

Aliases: --assets DIR is the same as --input DIR.

Examples:
  ./scripts/downscale_assets_ffmpeg.sh
  ./scripts/downscale_assets_ffmpeg.sh --dry-run
  ./scripts/downscale_assets_ffmpeg.sh --scale 4   # 4x -> 1x if you need original resolution
EOF
  exit "${1:-0}"
}

NO_BACKUP=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage 0 ;;
    --dry-run) DRY_RUN=true; shift ;;
    --resume) RESUME=true; shift ;;
    --in-place) IN_PLACE=true; shift ;;
    --no-backup) NO_BACKUP=true; shift ;;
    --scale)
      shift
      [[ $# -gt 0 ]] || { echo "--scale requires a positive integer" >&2; exit 1; }
      SCALE_DEN="$1"
      shift
      ;;
    --scale=*) SCALE_DEN="${1#--scale=}"; shift ;;
    --output)
      shift
      [[ $# -gt 0 ]] || { echo "--output requires a directory" >&2; exit 1; }
      OUTPUT_REL="$1"
      shift
      ;;
    --output=*) OUTPUT_REL="${1#--output=}"; shift ;;
    --input|--assets)
      shift
      [[ $# -gt 0 ]] || { echo "--input requires a directory" >&2; exit 1; }
      SRC_REL="$1"
      shift
      ;;
    --input=*|--assets=*) SRC_REL="${1#*=}"; shift ;;
    -*) echo "Unknown option: $1" >&2; usage 1 ;;
    *) EXTRA_FILES+=("$1"); shift ;;
  esac
done

if ! [[ "$SCALE_DEN" =~ ^[1-9][0-9]*$ ]]; then
  echo "--scale must be a positive integer (got: $SCALE_DEN)" >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
SRC_DIR="$ROOT_DIR/$SRC_REL"
if [[ "$OUTPUT_REL" == /* ]]; then
  OUTPUT_DIR="$OUTPUT_REL"
else
  OUTPUT_DIR="$ROOT_DIR/$OUTPUT_REL"
fi

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "ffmpeg not found on PATH. Install ffmpeg and retry." >&2
  exit 1
fi

if [[ ! -d "$SRC_DIR" ]]; then
  echo "Source directory not found: $SRC_DIR" >&2
  exit 1
fi

declare -a FILES=()
if [[ ${#EXTRA_FILES[@]} -gt 0 ]]; then
  for f in "${EXTRA_FILES[@]}"; do
    if [[ ! -f "$f" ]]; then
      echo "Not a file: $f" >&2
      exit 1
    fi
    FILES+=("$(cd "$(dirname "$f")" && pwd)/$(basename "$f")")
  done
else
  while IFS= read -r f; do
    FILES+=("$f")
  done < <(find "$SRC_DIR" -type f -name '*.png' | sort)
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
  echo "No PNG files to process under $SRC_DIR" >&2
  exit 1
fi

downscale_one() {
  local src="$1"
  local dst="$2"
  local w h nw nh

  w=$(ffprobe -v error -select_streams v:0 -show_entries stream=width -of csv=p=0 "$src")
  h=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of csv=p=0 "$src")
  nw=$(( (w * SCALE_NUM / SCALE_DEN / 2) * 2 ))
  nh=$(( (h * SCALE_NUM / SCALE_DEN / 2) * 2 ))
  [[ "$nw" -lt 1 ]] && nw=1
  [[ "$nh" -lt 1 ]] && nh=1

  if $DRY_RUN; then
    local rel="${src#"$SRC_DIR"/}"
    printf '  %s  %sx%s -> %sx%s\n' "$rel" "$w" "$h" "$nw" "$nh"
    return 0
  fi

  mkdir -p "$(dirname "$dst")"
  ffmpeg -y -hide_banner -loglevel error -i "$src" \
    -vf "scale=${nw}:${nh}:flags=lanczos+accurate_rnd+full_chroma_int,format=rgba" \
    -frames:v 1 -update 1 \
    -compression_level 6 \
    "$dst"

  local sw sh
  sw=$(ffprobe -v error -select_streams v:0 -show_entries stream=width -of csv=p=0 "$dst")
  sh=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of csv=p=0 "$dst")
  local kb_before kb_after
  kb_before=$(( $(stat -f%z "$src" 2>/dev/null || stat -c%s "$src") / 1024 ))
  kb_after=$(( $(stat -f%z "$dst" 2>/dev/null || stat -c%s "$dst") / 1024 ))
  printf '  OK %s  %dx%d -> %dx%d  %dKB -> %dKB\n' \
    "${src#"$SRC_DIR"/}" "$w" "$h" "$sw" "$sh" "$kb_before" "$kb_after"
}

echo "Project: $ROOT_DIR"
echo "Input:   $SRC_DIR"
echo "Scale:   1/${SCALE_DEN} (${SCALE_NUM}/${SCALE_DEN})"
if $IN_PLACE; then
  if $NO_BACKUP; then
    echo "Mode:    in-place (Lanczos, no backup)"
  else
    echo "Mode:    in-place (Lanczos, .bak backup)"
  fi
else
  echo "Output:  $OUTPUT_DIR"
  echo "Mode:    mirror tree (Lanczos)"
fi
[[ "$RESUME" == true ]] && echo "         --resume: skip existing outputs"
echo "Files:   ${#FILES[@]}"
echo

if $DRY_RUN; then
  echo "Dry run:"
fi

OK=0
SKIP=0
INDEX=0

for src in "${FILES[@]}"; do
  INDEX=$((INDEX + 1))
  rel="${src#"$SRC_DIR"/}"
  if $IN_PLACE; then
    tmp="${src}.downscale_tmp.png"
    downscale_one "$src" "$tmp"
    if ! $DRY_RUN; then
      if ! $NO_BACKUP && [[ ! -f "${src}${BACKUP_SUFFIX}" ]]; then
        cp -p "$src" "${src}${BACKUP_SUFFIX}"
      fi
      mv -f "$tmp" "$src"
      OK=$((OK + 1))
    fi
  else
    dst="$OUTPUT_DIR/$rel"
    if [[ "$RESUME" == true && -f "$dst" ]]; then
      SKIP=$((SKIP + 1))
      continue
    fi
    if ! $DRY_RUN; then
      echo "[$INDEX/${#FILES[@]}] $rel"
    fi
    downscale_one "$src" "$dst"
    if ! $DRY_RUN; then
      OK=$((OK + 1))
    fi
  fi
done

echo
if $DRY_RUN; then
  echo "Dry run complete. Re-run without --dry-run to process."
elif $IN_PLACE; then
  echo "Done. Processed in-place under $SRC_REL (*${BACKUP_SUFFIX} backups unless --no-backup)."
else
  OUT_COUNT="$(find "$OUTPUT_DIR" -type f -name '*.png' 2>/dev/null | wc -l | tr -d ' ')"
  echo "Done."
  echo "  Input PNGs:   ${#FILES[@]}"
  echo "  Output PNGs:  $OUT_COUNT"
  echo "  Processed:    $OK  skipped: $SKIP"
  echo "  Folder:       $OUTPUT_REL"
  if [[ "$OUT_COUNT" -lt "${#FILES[@]}" ]]; then
    echo "Warning: output count < input count. Re-run with --resume after fixing errors." >&2
    exit 1
  fi
fi
