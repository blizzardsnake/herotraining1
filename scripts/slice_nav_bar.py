"""
Slice the standalone nav-bar mockup (mockups/nav_bar.png) into 5 clean tab cells.

This mockup is JUST the bottom nav, isolated on a black canvas, all tabs in idle
state (no active highlight). We:
  1. Auto-detect the nav bar rectangle by finding the extents of red-glow pixels
  2. Crop that rectangle out
  3. Divide horizontally into 5 equal cells
  4. Overwrite nav_tab_home/workouts/quests/progress/profile.webp in drawable-nodpi

Output is clean, uniform idle tabs — no KBECTbI-active bias like the previous
quest-mockup-derived set.
"""
from PIL import Image, ImageDraw
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
SRC = os.path.join(ROOT, 'mockups', 'nav_bar.png')
OUT = os.path.join(ROOT, 'app', 'src', 'main', 'res', 'drawable-nodpi')

img = Image.open(SRC).convert('RGB')
W, H = img.size
px = img.load()
print(f'Source: {W}x{H}')

def is_red(r, g, b):
    return r > 120 and r > g * 1.7 and r > b * 1.7

# --------------------------------------------------------------------------
# 1. Auto-detect nav bar bounding box by scanning for red-glow pixels
# --------------------------------------------------------------------------
top_red = None
bottom_red = None
for y in range(H):
    for x in range(W):
        if is_red(*px[x, y]):
            if top_red is None:
                top_red = y
            bottom_red = y
            break

left_red = None
right_red = None
for x in range(W):
    for y in range(H):
        if is_red(*px[x, y]):
            if left_red is None:
                left_red = x
            right_red = x
            break

PAD = 8
nav_box = (
    max(0, left_red - PAD),
    max(0, top_red - PAD),
    min(W, right_red + PAD),
    min(H, bottom_red + PAD)
)
NW = nav_box[2] - nav_box[0]
NH = nav_box[3] - nav_box[1]
print(f'Nav bar: {nav_box}  size={NW}x{NH}  aspect={NW/NH:.3f}')

# --------------------------------------------------------------------------
# 2. Crop the nav bar, then slice into 5 equal cells
# --------------------------------------------------------------------------
nav = img.crop(nav_box)

TAB_NAMES = ['home', 'workouts', 'quests', 'progress', 'profile']
cell_w = NW / 5
tab_boxes_rel = {}
for i, name in enumerate(TAB_NAMES):
    x1 = int(i * cell_w)
    x2 = int((i + 1) * cell_w)
    tab_boxes_rel[name] = (x1, 0, x2, NH)

for name, box in tab_boxes_rel.items():
    out_name = f'nav_tab_{name}.webp'
    cell = nav.crop(box)
    cell.save(os.path.join(OUT, out_name), 'WEBP', quality=92, method=6)
    kb = os.path.getsize(os.path.join(OUT, out_name)) / 1024
    print(f'  {out_name}: {cell.size[0]}x{cell.size[1]}  ({kb:.1f} KB)')

# --------------------------------------------------------------------------
# 3. Debug overlay
# --------------------------------------------------------------------------
dbg = img.copy()
d = ImageDraw.Draw(dbg)
d.rectangle(nav_box, outline=(255, 255, 0), width=4)
for i, (name, rel) in enumerate(tab_boxes_rel.items()):
    abs_box = (
        nav_box[0] + rel[0], nav_box[1] + rel[1],
        nav_box[0] + rel[2], nav_box[1] + rel[3]
    )
    d.rectangle(abs_box, outline=(0, 255, 0), width=3)
    d.text((abs_box[0] + 6, abs_box[1] + 6), name.upper(), fill=(0, 255, 0))
dbg.save(os.path.join(ROOT, 'mockups', '_debug_nav_bar.png'))
print(f'Debug overlay: mockups/_debug_nav_bar.png')

# Aspect ratio to paste into MainTabsHost
print(f'\nFor MainTabsHost: .aspectRatio({cell_w:.0f}f / {NH}f) ~ {cell_w/NH:.3f}')
