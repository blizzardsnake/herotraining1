"""
Slice the QUESTS mockup into button pieces.

For now we only extract:
  - 5 bottom-nav tab cells (nav_tab_home/workouts/quests/progress/profile)
  - "ПРОДОЛЖИТЬ ПРОТОКОЛ" CTA button

The list items with progress bars (daily missions, personal quests) stay as part
of the bg — they need animated/dynamic bars which we'll render in Compose later.

Tab strip Y bounds are auto-detected: we scan for the topmost horizontal line
where text/icons start AFTER the last divider, then take everything below to
the bottom edge. Width is divided into 5 equal cells.

Debug overlay: mockups/_debug_quests_boxes.png
"""
from PIL import Image, ImageDraw
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
SRC = os.path.join(ROOT, 'mockups', 'quests.png')
OUT = os.path.join(ROOT, 'app', 'src', 'main', 'res', 'drawable-nodpi')
os.makedirs(OUT, exist_ok=True)

img = Image.open(SRC).convert('RGB')
W, H = img.size
px = img.load()
print(f'Source: {W}x{H}')

# --------------------------------------------------------------------------
# 1. Detect bottom nav strip Y range
#    Strategy: scan rows from the bottom up. Rows with icons/text have many
#    non-black pixels. Above the nav strip there's a short black gap
#    (maybe 10-20 rows). Find the gap -> top of the strip is just below it.
# --------------------------------------------------------------------------
# Auto-detect was too strict (it found only the label row, not the icons above them
# separated by a ~30px gap). Just hardcode bottom 10% — the strip is always there,
# and this matches the design across all screens (signin used the same proportions).
NAV_STRIP_PCT = 0.10   # bottom 10% of the image
nav_top = int(H * (1 - NAV_STRIP_PCT))
nav_bottom = H

print(f'Bottom nav strip detected: y {nav_top}..{nav_bottom} ({nav_bottom-nav_top}px tall)')
print(f'  percent: top={nav_top/H*100:.2f}%  height={(nav_bottom-nav_top)/H*100:.2f}%')

# --------------------------------------------------------------------------
# 2. Divide nav strip into 5 equal tab cells
# --------------------------------------------------------------------------
TAB_NAMES = ['home', 'workouts', 'quests', 'progress', 'profile']
tab_w = W / 5
tab_boxes = {}
for i, name in enumerate(TAB_NAMES):
    x1 = int(i * tab_w)
    x2 = int((i + 1) * tab_w)
    tab_boxes[name] = (x1, nav_top, x2, nav_bottom)

# --------------------------------------------------------------------------
# 3. "ПРОДОЛЖИТЬ ПРОТОКОЛ" CTA — hardcoded because auto-detect picks the red
#    progress bar just above it (both have strong red glow and are too close
#    to disambiguate reliably). Coords below are measured off the 941x1672
#    mockup; re-measure if the CTA position shifts in a future mockup.
# --------------------------------------------------------------------------
continue_box = (40, 1425, 900, 1540)
print(f'Continue button (hardcoded): {continue_box}')

# --------------------------------------------------------------------------
# 4. Save crops
# --------------------------------------------------------------------------
for name, box in tab_boxes.items():
    out_name = f'nav_tab_{name}.webp'
    img.crop(box).save(os.path.join(OUT, out_name), 'WEBP', quality=92, method=6)
    x1, y1, x2, y2 = box
    kb = os.path.getsize(os.path.join(OUT, out_name)) / 1024
    print(f'  {out_name}: {x2-x1}x{y2-y1}  ({kb:.1f} KB)')

if continue_box is not None:
    img.crop(continue_box).save(
        os.path.join(OUT, 'btn_quests_continue.webp'), 'WEBP', quality=92, method=6
    )
    x1, y1, x2, y2 = continue_box
    kb = os.path.getsize(os.path.join(OUT, 'btn_quests_continue.webp')) / 1024
    print(f'  btn_quests_continue.webp: {x2-x1}x{y2-y1}  ({kb:.1f} KB)')
    print(f'    percent: top={y1/H*100:.2f}%  left={x1/W*100:.2f}%  '
          f'w={(x2-x1)/W*100:.2f}%  h={(y2-y1)/H*100:.2f}%')

# --------------------------------------------------------------------------
# 5. Debug overlay
# --------------------------------------------------------------------------
dbg = img.copy()
d = ImageDraw.Draw(dbg)
for i, (name, box) in enumerate(tab_boxes.items()):
    color = (0, 255, 0) if name != 'quests' else (255, 100, 100)
    d.rectangle(box, outline=color, width=5)
    d.text((box[0] + 8, box[1] + 8), name.upper(), fill=color)
if continue_box:
    d.rectangle(continue_box, outline=(255, 255, 0), width=5)
    d.text((continue_box[0] + 8, continue_box[1] + 8), 'CONTINUE', fill=(255, 255, 0))
dbg_path = os.path.join(ROOT, 'mockups', '_debug_quests_boxes.png')
dbg.save(dbg_path)
print(f'\nDebug overlay: {dbg_path}')
