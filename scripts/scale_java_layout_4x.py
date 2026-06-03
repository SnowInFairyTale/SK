#!/usr/bin/env python3
"""Scale 1x layout numbers to 4x in org.test (safe token replacements only)."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1] / "app/src/main/java/org/test"
SKIP = {"Constants.java"}

# Word-boundary float replacements (won't touch Vector2f or 0.2f)
FLOAT_PATTERNS = [
    (r"\b160f\b", "Constants.ScreenCenterX"),
    (r"\b164f\b", "656f"),
    (r"\b-300f\b", "-1200f"),
    (r"\b420f\b", "1680f"),
    (r"\b425f\b", "1700f"),
    (r"\b444f\b", "1776f"),
    (r"\b405f\b", "1620f"),
    (r"\b402f\b", "1608f"),
    (r"\b380f\b", "1520f"),
    (r"\b435f\b", "1740f"),
    (r"\b400f\b", "1600f"),
    (r"\b339f\b", "1356f"),
    (r"\b320f\b", "1280f"),
    (r"\b279f\b", "1116f"),
    (r"\b260f\b", "1040f"),
    (r"\b240f\b", "960f"),
    (r"\b230f\b", "920f"),
    (r"\b219f\b", "876f"),
    (r"\b220f\b", "880f"),
    (r"\b206f\b", "824f"),
    (r"\b200f\b", "800f"),
    (r"\b274f\b", "1096f"),
    (r"\b287f\b", "1148f"),
    (r"\b150f\b", "600f"),
    (r"\b140f\b", "560f"),
    (r"\b120f\b", "480f"),
    (r"\b118f\b", "472f"),
    (r"\b100f\b", "400f"),
    (r"\b-40f\b", "-160f"),
    (r"\b-34f\b", "-136f"),
    (r"\b-4f\b", "-16f"),
    (r"\b-2f\b", "-8f"),
]

LITERAL_REPLACEMENTS = [
    ("LFont.getFont(", "Constants.uiFont("),
    ("textureOffsetX = 180", "textureOffsetX = 720"),
    ("textureOffsetX = 120", "textureOffsetX = 480"),
    ("textureOffsetX = 60", "textureOffsetX = 240"),
    ("setBounds(this.drawPosition.x, this.drawPosition.y, 60, 60)",
     "setBounds(this.drawPosition.x, this.drawPosition.y, 240, 240)"),
    (", 60, 60,", ", 240, 240,"),
    ("setHeight(8)", "setHeight(32)"),
    ("setHeight(4)", "setHeight(16)"),
    ("spriteWidth = 80", "spriteWidth = 320"),
    ("spriteHeight = 80", "spriteHeight = 320"),
    (", 80, 80,", ", 320, 320,"),
    ("int num = 8", "int num = 32"),
    ("int num = 10", "int num = 40"),
    ("int num2 = 0xe8", "int num2 = 0x3a0"),
    ("8, 8, 0x20, 0x20", "32, 32, 0x80, 0x80"),
    ("8, 8, 0x18, 0x18", "32, 32, 0x60, 0x60"),
    ("8, 8, 40, 40", "32, 32, 160, 160"),
    ("8, 13, 0x27, 0x27", "32, 52, 0x9c, 0x9c"),
    ("40, 40, 0x10, 0x10", "160, 160, 0x40, 0x40"),
    ("40, 40, 8, 0x10", "160, 160, 32, 0x40"),
    ("0x20, 0x20", "0x80, 0x80"),
    ("2, 2, 0x18, 0x18", "8, 8, 0x60, 0x60"),
    ("8, 0x10, 0x10", "32, 0x40, 0x40"),
    ("setRadius(5f)", "setRadius(20f)"),
    ("setRadius(6f)", "setRadius(24f)"),
    ("setRadius(8f)", "setRadius(32f)"),
    ("new Vector2f(120f, 38f)", "new Vector2f(480f, 152f)"),
    ("new Vector2f(140f, 50f)", "new Vector2f(560f, 200f)"),
    ("new Vector2f(140f, 42f)", "new Vector2f(560f, 168f)"),
    ("new Vector2f(48f, 220f)", "new Vector2f(192f, 880f)"),
    ("new Vector2f(206f, 220f)", "new Vector2f(824f, 880f)"),
    ("new Vector2f(10f, 420f)", "new Vector2f(40f, 1680f)"),
    ("new Vector2f(100f, 2f)", "new Vector2f(400f, 8f)"),
    ("new Vector2f(240f, -300f)", "new Vector2f(960f, -1200f)"),
    ("new Vector2f(240f, 425f)", "new Vector2f(960f, 1700f)"),
    ("drawPosition.add(40f, 20f)", "drawPosition.add(160f, 80f)"),
    ("drawPosition.add(150f, 0f)", "drawPosition.add(600f, 0f)"),
    ("drawPosition.add(120f, 33f)", "drawPosition.add(480f, 132f)"),
    ("drawPosition.add(120f, 15f)", "drawPosition.add(480f, 60f)"),
    ("drawPosition.add(64f, 15f)", "drawPosition.add(256f, 60f)"),
    ("drawPosition.add(64f, 33f)", "drawPosition.add(256f, 132f)"),
    ("drawPosition.add(274f, 15f)", "drawPosition.add(1096f, 60f)"),
    ("drawPosition.add(287f, 33f)", "drawPosition.add(1148f, 132f)"),
    ("drawPosition.add(23f, 15f)", "drawPosition.add(92f, 60f)"),
    ("drawPosition.add(77f, 15f)", "drawPosition.add(308f, 60f)"),
    ("drawPosition.add(44f, 33f)", "drawPosition.add(176f, 132f)"),
    ("drawPosition.add(74f, 33f)", "drawPosition.add(296f, 132f)"),
    ("towerToolbarDrawPosition.add(-2f, -34f)", "towerToolbarDrawPosition.add(-8f, -136f)"),
    ("monsterToolbarDrawPosition.add(-2f, -34f)", "monsterToolbarDrawPosition.add(-8f, -136f)"),
    ("new Vector2f(200f, -4f)", "new Vector2f(800f, -16f)"),
    ("new Vector2f(25f, -40f)", "new Vector2f(100f, -160f)"),
    (").add(20f,\n\t\t\t\t\t20f)", ").add(80f,\n\t\t\t\t\t80f)"),
    ("this.getPosition().x - 20f, this.getPosition().y - 20f",
     "this.getPosition().x - 80f, this.getPosition().y - 80f"),
    ("((int) this.drawPosition.x) + 0x109", "((int) this.drawPosition.x) + 0x424"),
    ("this.textureOffsetY = 0x41", "this.textureOffsetY = 0x104"),
    ("drawPosition.x + 64f", "drawPosition.x + 256f"),
    ("drawPosition.y + 33f", "drawPosition.y + 132f"),
    ("drawPosition.y + 15f", "drawPosition.y + 60f"),
    ("drawPosition.x + 50f", "drawPosition.x + 200f"),
    ("drawPosition.y + 13f", "drawPosition.y + 52f"),
    ("position.add(0f, 14f)", "position.add(0f, 56f)"),
    ("new Vector2f(230f, 20f)", "new Vector2f(920f, 80f)"),
    ("new Vector2f(164f, 20f)", "new Vector2f(656f, 80f)"),
    ("Utils.DrawStringAlignCenter(batch, this.font, text, 160f, 400f", "Utils.DrawStringAlignCenter(batch, this.font, text, Constants.ScreenCenterX, 1600f"),
    ("num += 20", "num += 80"),
    ("num2 += 20", "num2 += 80"),
    ("int num = 8", "int num = 32"),
    ("0, 5, 0x2d, 0x2d", "0, 20, 0xb4, 0xb4"),
]

# Sheet atlas Y offsets in AnimatedSpriteMonster / Tower
ATLAS_REPLACEMENTS = [
    ("new Vector2f(\n\t\t\t\t(float) num, 8f)", "new Vector2f(\n\t\t\t\t(float) num, 32f)"),
    ("new Vector2f((float) num2,\n\t\t\t\t72f)", "new Vector2f((float) num2,\n\t\t\t\t288f)"),
    ("(float) num, 132f)", "(float) num, 528f)"),
    ("(float) num2, 200f)", "(float) num2, 800f)"),
    ("(float) num, 258f)", "(float) num, 1032f)"),
    ("(float) num2, 322f)", "(float) num2, 1288f)"),
    ("new Vector2f((float) num, 18f)", "new Vector2f((float) num, 72f)"),
    ("new Vector2f((float) num, 118f)", "new Vector2f((float) num, 472f)"),
    ("new Vector2f((float) num,\n\t\t\t\t218f)", "new Vector2f((float) num,\n\t\t\t\t872f)"),
    ("new Vector2f((float) num, 318f)", "new Vector2f((float) num, 1272f)"),
    ("sellButtonArea = new RectBox(((int) this.drawPosition.x) + 0x424,\n\t\t\t\t((int) this.drawPosition.y) + 5, 0x2d, 0x2d)",
     "sellButtonArea = new RectBox(((int) this.drawPosition.x) + 0x424,\n\t\t\t\t((int) this.drawPosition.y) + 20, 0xb4, 0xb4)"),
]


def scale_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    orig = text
    for old, new in LITERAL_REPLACEMENTS:
        if old != new:
            text = text.replace(old, new)
    for old, new in ATLAS_REPLACEMENTS:
        text = text.replace(old, new)
    for pattern, repl in FLOAT_PATTERNS:
        text = re.sub(pattern, repl, text)
    if text != orig:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main():
    changed = []
    for path in sorted(ROOT.rglob("*.java")):
        if path.name in SKIP:
            continue
        if scale_file(path):
            changed.append(path)
    print(f"Updated {len(changed)} files")

if __name__ == "__main__":
    main()
