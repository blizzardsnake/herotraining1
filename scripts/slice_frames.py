"""
Extract every static FRAME / CARD / PANEL from the mockups.

Output lands in mockups/frames/{mockup}_{name}.webp plus a debug overlay
mockups/_debug_frames_{mockup}.png so you can see where each frame was cut.

Workflow:
  1. Run this script
  2. Inspect _debug_frames_*.png — if boxes are misaligned, edit coords below
  3. User opens each frame in Photoshop, erases dynamic text, saves clean version
  4. Clean frames get copied (or the script outputs directly) to app/src/main/res/drawable-nodpi/
     where they'll be referenced as R.drawable.frame_<name>
  5. Text is rendered on top via Compose Text composables with matching positioning

All Y/X coordinates are measured against the 941x1672 source — if you regen a
mockup at different dimensions, redo the math or let me know.
"""
from PIL import Image, ImageDraw, ImageFont
import os

ROOT = os.path.join(os.path.dirname(__file__), '..')
MOCKUPS = os.path.join(ROOT, 'mockups')
OUT = os.path.join(MOCKUPS, 'frames')
os.makedirs(OUT, exist_ok=True)

# Per-mockup frame definitions. Each entry: (output_name, x1, y1, x2, y2, description)
JOBS = {
    # =====================================================================
    # QUESTS — 941x1672
    # =====================================================================
    'quests.png': [
        ('quests_title_bar',      0,    0,  941,  135, 'КВЕСТЫ · АКТИВНЫЙ ПРОТОКОЛ — top title + Dante badge'),
        ('quests_dante_badge',    590, 10,  925, 125,  'Active hero badge (top-right) — reusable'),
        ('quests_section_daily',  25,  145, 921, 185,  'Section header: ЕЖЕДНЕВНЫЕ МИССИИ · 1/3'),
        ('quests_mission_card',   25,  195, 921, 305,  'Daily mission card (3 of these stack) — with progress bar'),
        ('quests_mission_card_2', 25,  315, 921, 425,  'Daily mission card #2'),
        ('quests_mission_card_3', 25,  435, 921, 545,  'Daily mission card #3'),
        ('quests_section_perso',  25,  570, 921, 615,  'Section header: ПЕРСОНАЛЬНЫЕ КВЕСТЫ'),
        ('quests_personal_card',  25,  625, 921, 810,  'Personal quest card (3 stack) — bigger, hero-themed'),
        ('quests_personal_card2', 25,  820, 921, 1000, 'Personal quest card #2'),
        ('quests_personal_card3', 25,  1010,921, 1190, 'Personal quest card #3'),
        ('quests_big_progress',   25,  1265,921, 1405, 'ПРОГРЕСС ПРОТОКОЛА big bar with % and XP counter'),
    ],

    # =====================================================================
    # HOME — 941x1672
    # =====================================================================
    'home.png': [
        ('home_greeting',         0,    0,  941,  100, 'ПРИВЕТ, ВОИН + subtitle'),
        ('home_xp_card',          25,  110, 921,  300, 'Level / XP / class card with hero silhouette bg'),
        ('home_section_weekly',   25,  310, 921,  350, 'Section header: ПРОГРЕСС ЗА НЕДЕЛЮ + delta %'),
        ('home_weekly_chart',     25,  360, 921,  715, 'Weekly chart panel (68% + goal + 7-day graph)'),
        ('home_stats_row',        25,  725, 921,  860, 'Row of 3 stats: training minutes / calories / water'),
        ('home_section_next',     25,  870, 921,  915, 'Section header: СЛЕДУЮЩАЯ ТРЕНИРОВКА + link'),
        ('home_next_workout',     25,  925, 921, 1195, 'Next workout card (hero + name + НАЧАТЬ button)'),
        ('home_section_dynamics', 25,  1210,921, 1255, 'Section header: ДИНАМИКА'),
        ('home_dynamics_card',    25,  1265,460, 1445, 'Dynamic stat card (left) — вес -3.2 кг'),
        ('home_dynamics_card2',   485, 1265,921, 1445, 'Dynamic stat card (right) — талия -4.1 см'),
    ],

    # =====================================================================
    # PROGRESS — 941x1672
    # =====================================================================
    'progress.png': [
        ('progress_title_bar',    0,    0,  941, 110,  'ПРОГРЕСС title + notification icon'),
        ('progress_tabs',         25,  120, 921, 180,  'Period tabs: НЕДЕЛЯ / МЕСЯЦ / ВСЁ ВРЕМЯ'),
        ('progress_weight_card',  25,  200, 921, 605,  'Weight card with big number + chart'),
        ('progress_section_body', 25,  620, 921, 665,  'Section header: ПАРАМЕТРЫ ТЕЛА + compare link'),
        ('progress_measure_card', 25,  680, 310, 885,  'Measurement card (3 across) — грудь'),
        ('progress_measure_card2',315, 680, 620, 885,  'Measurement card #2 — талия'),
        ('progress_measure_card3',625, 680, 921, 885,  'Measurement card #3 — бёдра'),
        ('progress_section_photo',25,  905, 921, 950,  'Section header: ПРОГРЕСС ФОТО + all link'),
        ('progress_photo_pair',   25,  960, 921, 1380, 'Before/after photo pair with dates'),
    ],

    # =====================================================================
    # ANKETA — 941x1672
    # =====================================================================
    'anketa.png': [
        ('anketa_step_indicator', 0,    0,  941, 120,  'Step indicator: 01 / 05 + ПРОТОКОЛ ИНИЦИАЛИЗАЦИИ'),
        ('anketa_section_primary',25,  130, 921, 190,  'Section header: БАЗОВЫЕ ДАННЫЕ / PRIMARY PARAMETERS'),
        ('anketa_gender_toggle',  25,  205, 921, 380,  'Gender toggle pair — МУЖСКОЙ / ЖЕНСКИЙ'),
        ('anketa_input_age',      25,  395, 921, 520,  'Number input card: ВОЗРАСТ / 32 / ЛЕТ'),
        ('anketa_input_height',   25,  535, 921, 660,  'Number input card: РОСТ / 172 / СМ'),
        ('anketa_input_weight',   25,  675, 921, 800,  'Number input card: ВЕС / 95 / КГ'),
        ('anketa_bmi_analysis',   25,  815, 921, 1120, 'АНАЛИЗ СИСТЕМЫ / BMI / category / recommendation'),
    ],

    # =====================================================================
    # HERO SELECT (original: 1 featured + 3 mini) — 941x1672
    # =====================================================================
    'hero_select.png': [
        ('hero_select_title',     0,    0,  941, 190,  'Title: ВЫБЕРИ СВОЕГО ГЕРОЯ + subtitle'),
        ('hero_select_featured',  25,  205, 921, 1040, 'Featured hero card (Dante) — big with stats + ВЫБРАН chip'),
        ('hero_select_mini_1',    25,  1060,310, 1385, 'Mini hero card (Leon)'),
        ('hero_select_mini_2',    315, 1060,620, 1385, 'Mini hero card (Jin-Woo)'),
        ('hero_select_mini_3',    625, 1060,921, 1385, 'Mini hero card (Kratos)'),
    ],

    # =====================================================================
    # HERO SELECT V2 (4 vertical cards) — 941x1672
    # =====================================================================
    'hero_select_v2.png': [
        ('heroes_v2_title',       0,    0,  941, 140,  'Title: ВЫБЕРИ ПУТЬ + МУЖСКИЕ ГЕРОИ'),
        ('heroes_v2_card_leon',   25,  155, 921, 410,  'Hero card: LEON КЕННЕДИ with portrait + desc + arrow'),
        ('heroes_v2_card_dante',  25,  420, 921, 680,  'Hero card: DANTE with portrait + desc + arrow'),
        ('heroes_v2_card_kratos', 25,  690, 921, 950,  'Hero card: КРАТОС with portrait + desc + arrow'),
        ('heroes_v2_card_jinwoo', 25,  960, 921, 1220, 'Hero card: СОН ДЖИН-У with portrait + desc + arrow'),
        ('heroes_v2_bottom_cta',  25,  1440,921, 1560, 'ПРОДОЛЖИТЬ → CTA'),
    ],
}


# ---------------------------------------------------------------
# Execute: slice and generate debug overlays
# ---------------------------------------------------------------
summary = []  # rows for the index.md

for src_name, frames in JOBS.items():
    src_path = os.path.join(MOCKUPS, src_name)
    if not os.path.exists(src_path):
        print(f'SKIP {src_name} — not in mockups/')
        continue

    img = Image.open(src_path).convert('RGB')
    W, H = img.size
    print(f'\n== {src_name} ({W}x{H}) ==')

    dbg = img.copy()
    d = ImageDraw.Draw(dbg)

    for (name, x1, y1, x2, y2, desc) in frames:
        out_name = f'{name}.webp'
        out_path = os.path.join(OUT, out_name)
        x1c = max(0, min(W, x1))
        y1c = max(0, min(H, y1))
        x2c = max(0, min(W, x2))
        y2c = max(0, min(H, y2))
        crop = img.crop((x1c, y1c, x2c, y2c))
        crop.save(out_path, 'WEBP', quality=92, method=6)
        kb = os.path.getsize(out_path) / 1024
        print(f'  {out_name:35s} {x2c-x1c:4d}x{y2c-y1c:4d}  {kb:6.1f} KB')

        d.rectangle((x1c, y1c, x2c, y2c), outline=(0, 255, 0), width=4)
        d.text((x1c + 6, y1c + 4), name, fill=(0, 255, 0))

        summary.append((src_name, out_name, x2c - x1c, y2c - y1c, round(kb, 1), desc))

    dbg_path = os.path.join(MOCKUPS, f'_debug_frames_{src_name}')
    dbg.save(dbg_path)
    print(f'  debug overlay: {os.path.basename(dbg_path)}')

# ---------------------------------------------------------------
# index.md listing what each file is
# ---------------------------------------------------------------
index_path = os.path.join(OUT, 'INDEX.md')
with open(index_path, 'w', encoding='utf-8') as f:
    f.write('# Frame index\n\n')
    f.write('Auto-generated by scripts/slice_frames.py. Open each .webp in Photoshop,\n')
    f.write('erase the text/numbers inside, save as a clean frame. Then copy cleaned\n')
    f.write('versions to `app/src/main/res/drawable-nodpi/` as `frame_<name>.webp`.\n\n')
    current = None
    for src, name, w, h, kb, desc in summary:
        if src != current:
            f.write(f'\n## {src}\n\n')
            f.write('| file | size | KB | what it is |\n')
            f.write('|---|---|---|---|\n')
            current = src
        f.write(f'| `{name}` | {w}x{h} | {kb} | {desc} |\n')
print(f'\nIndex: {index_path}')
print(f'Total frames: {len(summary)}')
