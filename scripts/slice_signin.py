"""
Slices signin.png mockup into layered pieces + saves a debug overlay so you can visually
verify the button boxes are correct.

Outputs to app/src/main/res/drawable-nodpi/:
  bg_signin_full.webp       — entire background
  btn_signin_google.webp    — crop of the Google button
  btn_signin_email.webp     — crop of the Email button

Debug file (not shipped):
  mockups/_debug_boxes.png  — source image with red/cyan rectangles over the button regions
"""
from PIL import Image, ImageDraw
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
SRC = os.path.join(ROOT, 'mockups', 'signin.png')
OUT = os.path.join(ROOT, 'app', 'src', 'main', 'res', 'drawable-nodpi')
os.makedirs(OUT, exist_ok=True)

img = Image.open(SRC).convert('RGB')
W, H = img.size
print(f'Source: {W}x{H}')

# -------------------------------------------------------------------------
# Auto-detect button Y ranges by scanning the LOWER half of the image only
# (so the red rune on the hero's back doesn't register as a button)
# -------------------------------------------------------------------------
px = img.load()

SCAN_FROM = int(H * 0.50)   # skip hero art

row_score = [0] * H
for y in range(SCAN_FROM, H):
    s = 0
    for x in range(W):
        r, g, b = px[x, y]
        # Strong red pixel (button neon), not just any reddish
        if r > 130 and r > g * 1.8 and r > b * 1.8:
            s += 1
    row_score[y] = s

# Cluster bright rows
threshold = 18
clusters = []
start = None
for y in range(SCAN_FROM, H):
    if row_score[y] >= threshold:
        if start is None:
            start = y
    else:
        if start is not None:
            clusters.append([start, y - 1])
            start = None
if start is not None:
    clusters.append([start, H - 1])

# Merge clusters separated by <40 rows (glow + button interior pairs)
merged = []
for c in clusters:
    if merged and c[0] - merged[-1][1] < 40:
        merged[-1][1] = c[1]
    else:
        merged.append(c)

# Keep only clusters taller than 40 rows
clusters = [c for c in merged if c[1] - c[0] > 40]
print(f'Detected clusters: {clusters}')

# -------------------------------------------------------------------------
# For each cluster, scan X columns to get horizontal bounds
# -------------------------------------------------------------------------
def find_x_bounds(y1, y2, thresh=2):
    col_score = [0] * W
    for y in range(y1, y2 + 1):
        for x in range(W):
            r, g, b = px[x, y]
            if r > 130 and r > g * 1.8 and r > b * 1.8:
                col_score[x] += 1
    x1 = next((x for x in range(W) if col_score[x] >= thresh), 0)
    x2 = next((x for x in range(W - 1, -1, -1) if col_score[x] >= thresh), W - 1)
    return x1, x2

PAD = 18   # extra padding around the detected glow so we don't cut the halo

boxes = {}
labels = ['google', 'email']
for i, (y1, y2) in enumerate(clusters[:2]):
    x1, x2 = find_x_bounds(y1, y2)
    x1p = max(0, x1 - PAD)
    y1p = max(0, y1 - PAD)
    x2p = min(W, x2 + PAD)
    y2p = min(H, y2 + PAD)
    boxes[labels[i]] = (x1p, y1p, x2p, y2p)

# If auto-detect failed for either, fall back to manual estimates
if 'google' not in boxes:
    boxes['google'] = (50, 895, 890, 1100)
if 'email' not in boxes:
    boxes['email'] = (210, 1150, 730, 1300)

# -------------------------------------------------------------------------
# Save crops + full bg + debug overlay
# -------------------------------------------------------------------------
img.save(os.path.join(OUT, 'bg_signin_full.webp'), 'WEBP', quality=85, method=6)

def percent(box):
    x1, y1, x2, y2 = box
    return (y1 / H * 100, x1 / W * 100, (x2 - x1) / W * 100, (y2 - y1) / H * 100)

for name, box in boxes.items():
    out_name = f'btn_signin_{name}.webp'
    img.crop(box).save(os.path.join(OUT, out_name), 'WEBP', quality=92, method=6)
    x1, y1, x2, y2 = box
    pct = percent(box)
    print(f'{name.upper()}: box={box}  size={x2-x1}x{y2-y1}')
    print(f'  percent: top={pct[0]:.2f}  left={pct[1]:.2f}  w={pct[2]:.2f}  h={pct[3]:.2f}')
    kb = os.path.getsize(os.path.join(OUT, out_name)) / 1024
    print(f'  saved: {out_name} ({kb:.1f} KB)')

# Debug overlay — draw rects on a copy so we can see if they line up
debug = img.copy()
d = ImageDraw.Draw(debug)
colors = {'google': (0, 255, 0), 'email': (0, 200, 255)}
for name, box in boxes.items():
    d.rectangle(box, outline=colors.get(name, (255, 255, 0)), width=6)
debug.save(os.path.join(ROOT, 'mockups', '_debug_boxes.png'))
print('Debug overlay saved: mockups/_debug_boxes.png')
