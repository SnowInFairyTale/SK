#!/usr/bin/env python3
"""Apply Constants.f/v/font wrappers to spatial literals in Java sources (1x design values)."""
import re
import os
import sys

ROOT = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'java')

SKIP_LINE = re.compile(
    r'Constants\.(f|v|i|font)|new Wave\(|TowerLevel\(|setDrawOrder|getDrawOrder|'
    r'animationSpeedRatio|columnCount|spriteCount|SecondsPerFrame|0\.0333|elapsedTime|'
    r'reloadTime|releaseTime|upgradeTime|spread|timeUntil|Delay|showMilliseconds|'
    r'timeLeft|transitionOnTime|transitionOffTime|setScale\(|getScale\(|privateScale|'
    r'0\.55f|1\.25f|0\.4f|0\.5f|lowColorLimit|LColor|PoolColor|TransitionAlpha|'
    r'RemainingLives\(game|Cash\(game|setTowerPrice|startHitPoints|'
    r'setCurrentPercent|Math\.|spriteIndex|spriteCount\.argvalue|'
    r'setStartPoint|setEndPoint|TowerBlockingGridCells|LevelSpecificOccupied|'
    r'PathNode\(|gridX|gridY|GridX|GridY|IsOccupied|MikkelsPathFinding|'
    r'tempDirs|0x12\]|0x13\]|< 0x12|>= 0x12|>= 15|< 2\)|> 0x10|'
    r'GetMonsterSpawnOffsetPositions|new Vector2f\([01],|new Vector2f\(-1,|'
    r'new Vector2f\(0,|case [0-9]+:|PiOver8|wrapAngle|'
    r'OccupiedGridCells\(\)\.add\(new Vector2f\([0-9]+, [0-9]+\)\)|'
    r'addReservedHitPoints|removeReservedHitPoints|getHitPoints|setHitPoints|'
    r'import |package |//|"\d|getLevel\(\)|setLevel\(|versionCode'
)

FLOAT_LIT = re.compile(r'(?<!Constants\.)(?<![\w.])(\d+(?:\.\d+)?)f')

VECTOR2F_FF = re.compile(
    r'new Vector2f\((\d+(?:\.\d+)?)f,\s*(\d+(?:\.\d+)?)f\)'
)

def should_skip(line: str) -> bool:
    return bool(SKIP_LINE.search(line))


def wrap_floats(line: str) -> str:
    if should_skip(line):
        return line
    if 'new Vector2f(' in line and 'f' in line:
        return VECTOR2F_FF.sub(r'Constants.v(\1f, \2f)', line)
    if FLOAT_LIT.search(line):
        return FLOAT_LIT.sub(r'Constants.f(\1f)', line)
    return line


def wrap_fonts(content: str) -> str:
    return re.sub(r'LFont\.getFont\((\d+)\)', r'Constants.font(\1)', content)


def process_file(path: str) -> bool:
    if os.path.basename(path) == 'Constants.java':
        return False
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = wrap_fonts(original)
    lines = content.splitlines(keepends=True)
    new_lines = [wrap_floats(line) for line in lines]
    updated = ''.join(new_lines)

    if updated != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(updated)
        return True
    return False


def main():
    changed = []
    for dirpath, _, files in os.walk(ROOT):
        for name in files:
            if not name.endswith('.java'):
                continue
            path = os.path.join(dirpath, name)
            if process_file(path):
                changed.append(path)
    print(f'Updated {len(changed)} files')
    for p in sorted(changed):
        print(' ', os.path.relpath(p, ROOT))


if __name__ == '__main__':
    main()
