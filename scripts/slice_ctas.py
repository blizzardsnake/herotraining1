"""
Slice static CTA buttons from multiple mockups.

Uses the same red-glow auto-detector as slice_signin.py — each mockup is scanned
below its top 50% (to avoid hero art / title glows), red clusters are grouped,
and the biggest cluster near the bottom is treated as the CTA.

Outputs into app/src/main/res/drawable-nodpi/:
  btn_anketa_continue.webp   — "ПРОДОЛЖИТЬ →" (anketa)
  btn_home_start.webp        — "НАЧАТЬ" on workout card (home)
  btn_progress_add.webp      — "ДОБАВИТЬ ЗАМЕРЫ +" (progress)

If auto-detect finds the wrong thing (happens when mockup has multiple red
glows — e.g. progress has a red weight chart and a red button), fall back to
hardcoded coords from visual inspection.
"""
from PIL import Image, ImageDraw
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
MOCKUPS = os.path.join(ROOT, 'mockups')
OUT = os.path.join(ROOT, 'app', 'src', 'main', 'res', 'drawable-nodpi')


def is_red(r, g, b):
    return r > 130 and r > g * 1.8 and r > b * 1.8


def detect_biggest_red_cluster(img, scan_y_from_pct=0.60, scan_y_to_pct=0.95):
    """Find the tallest contiguous band of red-glow rows between the given Y %s.
    Returns (x1, y1, x2, y2) bounding box including ~18px padding, or None."""
    W, H = img.size
    px = img.load()
    y_from = int(H * scan_y_from_pct)
    y_to = int(H * scan_y_to_pct)

    row_score = [0] * H
    for y in range(y_from, y_to):
        s = 0
        for x in range(W):
            r, g, b = px[x, y]
            if is_red(r, g, b):
                s += 1
        row_score[y] = s

    clusters = []
    start = None
    for y in range(y_from, y_to):
        if row_score[y] >= 20:
            if start is None:
                start = y
        else:
            if start is not None:
                clusters.append([start, y - 1])
                start = None
    if start is not None:
        clusters.append([start, y_to - 1])

    # Merge nearby (<30 rows apart)
    merged = []
    for c in clusters:
        if merged and c[0] - merged[-1][1] < 30:
            merged[-1][1] = c[1]
        else:
            merged.append(list(c))

    big = [c for c in merged if c[1] - c[0] > 40]
    if not big:
        return None

    # Take the TALLEST cluster (bigger area = CTA, not a thin progress bar line)
    y1, y2 = max(big, key=lambda c: c[1] - c[0])

    col_score = [0] * W
    for y in range(y1, y2 + 1):
        for x in range(W):
            r, g, b = px[x, y]
            if is_red(r, g, b):
                col_score[x] += 1
    x1 = next((x for x in range(W) if col_score[x] >= 2), 0)
    x2 = next((x for x in range(W - 1, -1, -1) if col_score[x] >= 2), W - 1)

    PAD = 18
    return (max(0, x1 - PAD), max(0, y1 - PAD),
            min(W, x2 + PAD), min(H, y2 + PAD))


# --------------------------------------------------------------------------
# Per-mockup configuration
# --------------------------------------------------------------------------
JOBS = [
    # (src filename, output drawable name, scan range, fallback box)
    # anketa: auto-detect works perfectly ("ПРОДОЛЖИТЬ →" is a bright filled neon).
    ('anketa.png',   'btn_anketa_continue.webp',   (0.85, 0.99), None),

    # home: "НАЧАТЬ" is a small pill INSIDE the workout card, autodetect kept grabbing
    # the "СЕГОДНЯ" badge above it. Hardcoded bounds measured off home.png.
    ('home.png',     'btn_home_start.webp',         (0.99, 0.99), (668, 1045, 900, 1110)),

    # progress: "ДОБАВИТЬ ЗАМЕРЫ +" is a thin-outlined pill with no filled interior,
    # so its per-row red-pixel count is too low for the detector. Hardcoded.
    ('progress.png', 'btn_progress_add.webp',       (0.99, 0.99), (40, 1395, 900, 1490)),
]

for src_name, out_name, scan_range, fallback in JOBS:
    src_path = os.path.join(MOCKUPS, src_name)
    if not os.path.exists(src_path):
        print(f'SKIP {src_name} — not found')
        continue

    img = Image.open(src_path).convert('RGB')
    W, H = img.size
    box = detect_biggest_red_cluster(img, *scan_range)
    source = 'auto'
    if box is None and fallback is not None:
        box = fallback
        source = 'fallback'
    if box is None:
        print(f'FAIL {src_name} — no cluster + no fallback')
        continue

    img.crop(box).save(os.path.join(OUT, out_name), 'WEBP', quality=92, method=6)
    x1, y1, x2, y2 = box
    kb = os.path.getsize(os.path.join(OUT, out_name)) / 1024
    pct = (y1 / H * 100, x1 / W * 100, (x2 - x1) / W * 100, (y2 - y1) / H * 100)
    print(f'{src_name:20s}-> {out_name:30s} {x2-x1}x{y2-y1} ({kb:.1f} KB) [{source}]')
    print(f'  percent: top={pct[0]:.2f} left={pct[1]:.2f} w={pct[2]:.2f} h={pct[3]:.2f}')

    # Debug overlay per source
    dbg = img.copy()
    d = ImageDraw.Draw(dbg)
    d.rectangle(box, outline=(255, 255, 0), width=6)
    d.text((box[0] + 8, box[1] + 8), out_name, fill=(255, 255, 0))
    dbg_path = os.path.join(MOCKUPS, f'_debug_{src_name}')
    dbg.save(dbg_path)
