#!/usr/bin/env bash
#
# Downscale PNG assets to 50% with ffmpeg (Lanczos — sharp when shrinking).
# Default targets: 16 PNGs listed in DEFAULT_REL_PATHS below.
#
# Usage:
#   ./scripts/downscale_assets_ffmpeg.sh              # write to assets_half/, keep originals
#   ./scripts/downscale_assets_ffmpeg.sh --in-place   # backup *.bak then replace originals
#   ./scripts/downscale_assets_ffmpeg.sh --dry-run
#   ./scripts/downscale_assets_ffmpeg.sh path/to/a.png path/to/b.png
#
# Requires: ffmpeg on PATH

set -euo pipefail

SCALE_NUM=1
SCALE_DEN=2
ASSETS_REL="app/src/main/assets"
OUTPUT_REL="app/src/main/assets_half"
DRY_RUN=false
IN_PLACE=false
BACKUP_SUFFIX=".bak"
EXTRA_FILES=()

usage() {
  cat <<'EOF'
Downscale PNGs to 50% with ffmpeg (Lanczos scaler).

Usage:
  ./scripts/downscale_assets_ffmpeg.sh [options] [FILE ...]

Options:
  --dry-run       List files and planned output sizes only
  --in-place      Replace originals (creates FILE.bak backups first)
  --output DIR    Output root (default: app/src/main/assets_half)
  --no-backup     With --in-place, skip .bak backups (destructive)
  --assets DIR    Assets root (default: app/src/main/assets)
  -h, --help      Show this help

Default files (16, when no FILE args):
  png/monsterinfo1-6.png, png/towerinfo1-4.png,
  monsterinfo1-6.png (assets root)

After reviewing assets_half/, copy into assets/ or run with --in-place.
EOF
  exit "${1:-0}"
}

NO_BACKUP=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage 0 ;;
    --dry-run) DRY_RUN=true; shift ;;
    --in-place) IN_PLACE=true; shift ;;
    --no-backup) NO_BACKUP=true; shift ;;
    --output)
      shift
      [[ $# -gt 0 ]] || { echo "--output requires a directory" >&2; exit 1; }
      OUTPUT_REL="$1"
      shift
      ;;
    --output=*) OUTPUT_REL="${1#--output=}"; shift ;;
    --assets)
      shift
      [[ $# -gt 0 ]] || { echo "--assets requires a directory" >&2; exit 1; }
      ASSETS_REL="$1"
      shift
      ;;
    --assets=*) ASSETS_REL="${1#--assets=}"; shift ;;
    -*) echo "Unknown option: $1" >&2; usage 1 ;;
    *) EXTRA_FILES+=("$1"); shift ;;
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

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "ffmpeg not found on PATH. Install ffmpeg and retry." >&2
  exit 1
fi

if [[ ! -d "$ASSETS_DIR" ]]; then
  echo "Assets directory not found: $ASSETS_DIR" >&2
  exit 1
fi

# 16 files to downscale (paths relative to app/src/main/assets)
DEFAULT_REL_PATHS=(
  png/monsterinfo1.png
  png/monsterinfo2.png
  png/monsterinfo3.png
  png/monsterinfo4.png
  png/monsterinfo5.png
  png/monsterinfo6.png
  png/towerinfo1.png
  png/towerinfo2.png
  png/towerinfo3.png
  png/towerinfo4.png
  monsterinfo1.png
  monsterinfo2.png
  monsterinfo3.png
  monsterinfo4.png
  monsterinfo5.png
  monsterinfo6.png
)

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
  for rel in "${DEFAULT_REL_PATHS[@]}"; do
    f="$ASSETS_DIR/$rel"
    if [[ ! -f "$f" ]]; then
      echo "Missing default file: $rel (expected at $f)" >&2
      exit 1
    fi
    FILES+=("$f")
  done
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
  echo "No PNG files to process." >&2
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
    local rel="${src#"$ASSETS_DIR"/}"
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
    "${src#"$ASSETS_DIR"/}" "$w" "$h" "$sw" "$sh" "$kb_before" "$kb_after"
}

echo "Assets:  $ASSETS_DIR"
if $IN_PLACE; then
  if $NO_BACKUP; then
    echo "Mode:    in-place (50% scale, Lanczos, no backup)"
  else
    echo "Mode:    in-place (50% scale, Lanczos, .bak backup)"
  fi
else
  echo "Output:  $OUTPUT_DIR"
  echo "Mode:    copy tree (50% scale, Lanczos)"
fi
echo "Files:   ${#FILES[@]}"
echo

if $DRY_RUN; then
  echo "Dry run:"
fi

for src in "${FILES[@]}"; do
  rel="${src#"$ASSETS_DIR"/}"
  if $IN_PLACE; then
    tmp="${src}.downscale_tmp.png"
    downscale_one "$src" "$tmp"
    if ! $DRY_RUN; then
      if ! $NO_BACKUP && [[ ! -f "${src}${BACKUP_SUFFIX}" ]]; then
        cp -p "$src" "${src}${BACKUP_SUFFIX}"
      fi
      mv -f "$tmp" "$src"
    fi
  else
    dst="$OUTPUT_DIR/$rel"
    downscale_one "$src" "$dst"
  fi
done

echo
if $DRY_RUN; then
  echo "Dry run complete. Re-run without --dry-run to process."
elif $IN_PLACE; then
  echo "Done. Originals backed up as *${BACKUP_SUFFIX} unless --no-backup was set."
else
  echo "Done. Review $OUTPUT_DIR then merge into assets/ if satisfied."
fi
