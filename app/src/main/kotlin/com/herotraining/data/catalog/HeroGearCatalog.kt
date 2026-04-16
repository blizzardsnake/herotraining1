package com.herotraining.data.catalog

import com.herotraining.data.model.HeroGear

/**
 * Per-hero signature training/gear activities. Mirrors `HERO_GEAR` object from the prototype,
 * with Batman→Leon / Catwoman→Ada / Jill→Ciri migration applied.
 */
object HeroGearCatalog {

    private val LEON = listOf(
        HeroGear("boxing",   "Бокс/MMA или груша", "CQC Training",        "Ближний бой агента", "🥊"),
        HeroGear("shooting", "Тир / стрельба",     "Marksman Drill",      "Точность агента",    "🎯"),
        HeroGear("airsoft",  "Страйкбол/лазертаг", "Tactical Ops",        "Тактика зачистки",   "🔫", featured = true),
        HeroGear("parkour",  "Паркур / урбан-фит", "Urban Evac",          "Эвакуация из города","🏢")
    )

    private val DANTE = listOf(
        HeroGear("airsoft",  "Страйкбол/лазертаг", "Devil Trigger Drills","Ebony & Ivory",      "🔫"),
        HeroGear("boxing",   "Бокс / Муай-тай",    "Demon Fists",         "Кавальер/Беовулф",   "🥊"),
        HeroGear("dancing",  "Танцы / брейк",      "Style Training",      "Стиль — оружие",     "💃"),
        HeroGear("kendo",    "Кендо / фехтование", "Rebellion Strike",    "Меч Rebellion",      "⚔️")
    )

    private val KRATOS = listOf(
        HeroGear("heavy_tools","Топор/колка/молот", "Leviathan Training", "Рубка дров",         "🪓"),
        HeroGear("ropes",      "Battle ropes / канат","Chains of Chaos",   "Цепи Хаоса",         "⛓️", featured = true),
        HeroGear("climbing",   "Скалолазание",     "Climbing Olympus",   "Восхождение воина",  "⛰️"),
        HeroGear("wrestling",  "Борьба / BJJ / дзюдо","Spartan Grip",     "Боевой захват",      "🤼")
    )

    private val JINWOO = listOf(
        HeroGear("airsoft",  "Страйкбол/лазертаг", "Dungeon Raid",        "Штурм подземелья",   "🎯"),
        HeroGear("kendo",    "Кендо/катана",       "Shadow Blade",        "Чёрный клинок",      "🗡️"),
        HeroGear("running",  "Бег / интервалы",    "Dungeon Escape",      "Скоростное преодоление","🏃"),
        HeroGear("hiking",   "Длинные пешие",      "Monarch's March",     "Марш Монарха",       "🥾")
    )

    private val ADA = listOf(
        HeroGear("pole",     "Pole dance / пилон", "Stiletto Grace",      "Грация + сила",      "🎭", featured = true),
        HeroGear("aerial",   "Воздушная гимнастика","Aerial Infiltration","Грация шпионки",     "🎪"),
        HeroGear("yoga",     "Йога / стретчинг",   "Silent Flow",         "Гибкость агента",    "🧘"),
        HeroGear("parkour",  "Паркур / акробатика","Rooftop Runner",      "Крыши Шанхая",       "🏢"),
        HeroGear("dancing",  "Танцы (любые)",      "Night Dance",         "Ритм в движении",    "💃")
    )

    private val LARA = listOf(
        HeroGear("climbing", "Скалолазание",       "Ascent Training",     "Главный навык Лары", "🧗‍♀️", featured = true),
        HeroGear("archery",  "Стрельба из лука",   "Bow Mastery",         "Фирменное оружие",   "🏹"),
        HeroGear("swimming", "Плавание / бассейн", "River Passage",       "Выносливость пловца","🏊‍♀️"),
        HeroGear("hiking",   "Hiking / туризм",    "Expedition Prep",     "Переходы с рюкзаком","🥾"),
        HeroGear("knife",    "Ножевой бой / survival","Survival Skills",  "Выживание",          "🔪")
    )

    private val TWOB = listOf(
        HeroGear("kendo",      "Кендо / катана",      "YoRHa Blade",      "Virtuous Contract",  "🗡️", featured = true),
        HeroGear("martial",    "Айкидо/дзюдо/карате", "Close Combat Protocol","Рукопашная",       "🥋"),
        HeroGear("dancing",    "Танцы / хореография", "Battle Ballet",    "Боевая хореография", "💃"),
        HeroGear("ballet",     "Балет / контемпорари","Elegant Destruction","Изящество боя",     "🩰"),
        HeroGear("gymnastics", "Гимнастика",          "Precise Motion",   "Точность движений",  "🤸")
    )

    private val CIRI = listOf(
        HeroGear("kendo",    "Кендо / фехтование", "Zireael Strike",      "Серебряный клинок",  "🗡️", featured = true),
        HeroGear("shooting", "Стрельба / тир",     "Aim Drill",           "Точность ведьмака",  "🎯"),
        HeroGear("running",  "Бег / интервалы",    "Wild Hunt Evade",     "Уход от преследования","🏃"),
        HeroGear("martial",  "Муай-тай / BJJ / бокс","Witcher CQC",       "Ближний бой",        "🥊"),
        HeroGear("parkour",  "Паркур / urban",     "Blink Mobility",      "Мобильность Ciri",   "🏙️")
    )

    private val byHeroId: Map<String, List<HeroGear>> = mapOf(
        "leon" to LEON,
        "dante" to DANTE,
        "kratos" to KRATOS,
        "sung_jinwoo" to JINWOO,
        "ada" to ADA,
        "lara_croft" to LARA,
        "twob" to TWOB,
        "ciri" to CIRI
    )

    fun forHero(heroId: String): List<HeroGear> = byHeroId[heroId].orEmpty()
}
