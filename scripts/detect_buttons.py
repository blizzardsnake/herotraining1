"""Finds the red-glow button regions in the signin mockup automatically.

Scans each row of pixels, measures how much "pure red glow" is present, then groups
the rows into clusters. The two biggest clusters are GOOGLE (wider, brighter) and
EMAIL (narrower, dimmer). Prints the bounding boxes so slice_signin.py can use them.
"""
from PIL import Image
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
SRC = os.path.join(ROOT, 'mockups', 'signin.png')
img = Image.open(SRC).convert('RGB')
W, H = img.size
print(f'Image: {W}x{H}')

# --- Row-wise redness score ---
# For each row, count pixels where R >> G, B (pure red/crimson — button neon glow).
px = img.load()
row_score = [0] * H
for y in range(H):
    s = 0
    for x in range(W):
        r, g, b = px[x, y]
        if r > 140 and r > g * 2 and r > b * 2:
            s += 1
    row_score[y] = s

# --- Find clusters of bright-red rows ---
threshold = 30   # min red pixel count per row to consider "in a button"
clusters = []
in_cluster = False
start = 0
for y in range(H):
    if row_score[y] >= threshold:
        if not in_cluster:
            start = y
            in_cluster = True
    else:
        if in_cluster:
            clusters.append((start, y - 1))
            in_cluster = False
if in_cluster:
    clusters.append((start, H - 1))

# Merge clusters separated by < 30 rows (glow extends past hard border)
merged = []
for c in clusters:
    if merged and c[0] - merged[-1][1] < 30:
        merged[-1] = (merged[-1][0], c[1])
    else:
        merged.append(list(c))

# Filter clusters by height — real buttons are > 80 rows tall
real = [c for c in merged if c[1] - c[0] > 50]
print(f'Clusters: {real}')

# --- For each cluster, find X bounds by scanning columns within Y range ---
def find_x_bounds(y1, y2):
    col_score = [0] * W
    for y in range(y1, y2 + 1):
        for x in range(W):
            r, g, b = px[x, y]
            if r > 140 and r > g * 2 and r > b * 2:
                col_score[x] += 1
    thresh = 2
    x1 = next((x for x in range(W) if col_score[x] >= thresh), 0)
    x2 = next((x for x in range(W - 1, -1, -1) if col_score[x] >= thresh), W - 1)
    return x1, x2

# Pad boxes a bit so we include the full glow halo
PAD = 10

for i, (y1, y2) in enumerate(real):
    x1, x2 = find_x_bounds(y1, y2)
    y1p = max(0, y1 - PAD)
    y2p = min(H, y2 + PAD)
    x1p = max(0, x1 - PAD)
    x2p = min(W, x2 + PAD)
    name = ['GOOGLE', 'EMAIL', 'EXTRA'][min(i, 2)]
    pct = (y1p / H * 100, x1p / W * 100, (x2p - x1p) / W * 100, (y2p - y1p) / H * 100)
    print(f'{name}: box=({x1p},{y1p},{x2p},{y2p})   '
          f'size={x2p - x1p}x{y2p - y1p}   '
          f'percent: top={pct[0]:.2f}  left={pct[1]:.2f}  w={pct[2]:.2f}  h={pct[3]:.2f}')
