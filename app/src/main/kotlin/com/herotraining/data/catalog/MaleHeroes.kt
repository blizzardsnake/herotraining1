package com.herotraining.data.catalog

import androidx.compose.ui.graphics.Color
import com.herotraining.data.model.*

internal object MaleHeroes {

    val LEON = Hero(
        id = "leon",
        name = "ЛЕОН КЕННЕДИ",
        tagline = "Выживший. Спецагент. Хладнокровие.",
        color = Color(0xFF2E5A88),
        bgColor = Color(0xFF0A0F1A),
        iconKey = "shield",
        gender = Gender.MALE,
        personality = "Пережил Ракун-Сити. Ноль страха, ноль паники.",
        description = "Агент D.S.O. Тренировка — это выживание.",
        vibe = "выживший Ракун-Сити",
        comboName = "ТАКТИКА",
        comboStages = listOf("НОВИЧОК", "ТРЕВОГА", "ОПЕРАТИВНИК", "ПРИЗРАК", "АГЕНТ"),
        macroRatio = MacroRatio(0.35, 0.30, 0.35),
        signaturePerk = SignaturePerk("Зелёная Трава", "Зелёный чай + витамины = +5 RP", "🌿"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Омлет 3 яйца + овощи", 380),
                FoodItem("Овсянка + ягоды + миндаль", 420),
                FoodItem("Йогурт + протеин", 320)
            ),
            lunch = listOf(
                FoodItem("Куриная грудка + рис + брокколи", 520),
                FoodItem("Лосось + киноа", 560),
                FoodItem("Говядина + картофель", 620)
            ),
            dinner = listOf(
                FoodItem("Индейка + овощи", 420),
                FoodItem("Рыба + спаржа", 380),
                FoodItem("Творог + орехи", 340)
            ),
            snack = listOf(
                FoodItem("Протеиновый шейк", 150),
                FoodItem("Орехи 30г", 180),
                FoodItem("Энергетик без сахара", 50)
            ),
            treat = FoodItem("Чай из Зелёной Травы", 5)
        ),
        rankSystem = RankSystem(
            name = "Уровень Агента",
            ranks = listOf("ГРАЖДАНИН", "НОВИЧОК", "R.P.D.", "D.S.O.", "ЛЕГЕНДА"),
            thresholds = listOf(0, 5, 15, 35, 75)
        ),
        bonusQuest = BonusQuest("brain", "Тактическая подготовка", "20 мин чтения или медитации"),
        builds = listOf(
            HeroBuild("rookie_cop", "Новобранец R.P.D.", "Средняя", 2,
                "Базовая подготовка полицейского.", "4 раза в неделю", "1 послабление в неделю.",
                "«Первый день на службе.»",
                frequency = 4, intensityMultiplier = 0.7, calorieAdjust = -0.1,
                perks = listOf("Плановое послабление")),
            HeroBuild("raccoon_survivor", "Выживший Ракун-Сити", "Высокая", 3,
                "Пережил ад.", "6 раз в неделю", "Белок, ноль сахара.",
                "«Город превратился в ад.»",
                frequency = 6, intensityMultiplier = 0.85, calorieAdjust = -0.15,
                perks = listOf("Выживание")),
            HeroBuild("dso_agent", "Агент D.S.O.", "Экстрим", 4,
                "Элитный оперативник.", "Ежедневно", "Интервальное голодание 16/8.",
                "«Миссия прежде всего.»",
                frequency = 7, intensityMultiplier = 1.0, calorieAdjust = -0.2,
                perks = listOf("Тотальный контроль")),
            HeroBuild("veteran_agent", "Ветеран-Агент", "Для 45+", 3,
                "Ветеран последнего задания.", "4 раза в неделю", "Белок 1.8 г/кг.",
                "«Я видел слишком много.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = -0.1,
                perks = listOf("Бережём суставы"), hiddenFor45 = true)
        )
    )

    val DANTE = Hero(
        id = "dante",
        name = "ДАНТЕ",
        tagline = "Пицца. Стиль. Пот.",
        color = Color(0xFFE63946),
        bgColor = Color(0xFF1A0505),
        iconKey = "swords",
        gender = Gender.MALE,
        personality = "Ест что хочет. Пашет как проклятый.",
        description = "Ешь пиццу — но отработай её.",
        vibe = "стиль важнее пота",
        comboName = "СТИЛЬ",
        comboStages = listOf("D", "C", "B", "A", "S"),
        macroRatio = MacroRatio(0.30, 0.30, 0.40),
        signaturePerk = SignaturePerk("Стильный Читмил", "Пицца + интенсив = +5 RP", "🍕"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Панкейки + яичница", 580),
                FoodItem("Йогурт + гранола", 450),
                FoodItem("Тост + авокадо + яйцо", 420)
            ),
            lunch = listOf(
                FoodItem("Паста с курицей", 680),
                FoodItem("Бургер из индейки", 750),
                FoodItem("Рис + говядина", 620)
            ),
            dinner = listOf(
                FoodItem("Пицца 2 куска", 700),
                FoodItem("Стейк + картофель фри", 780),
                FoodItem("Курица + рис", 580)
            ),
            snack = listOf(
                FoodItem("Батончик", 200),
                FoodItem("Банан + арахисовая паста", 280),
                FoodItem("Смузи", 300)
            ),
            treat = FoodItem("🍕 Вечер Пиццы", 900)
        ),
        rankSystem = RankSystem(
            name = "Стилевой Ранг",
            ranks = listOf("D", "C", "B", "A", "S", "SS", "SSS"),
            labels = listOf("УЖАСНО", "БЕЗУМНО", "БАХ!", "НИЧЕГО!", "КЛАСС!", "СЕНСАЦИЯ!!", "СТИЛЯГА!!!"),
            thresholds = listOf(0, 3, 8, 18, 35, 60, 100)
        ),
        bonusQuest = BonusQuest("target", "Стилевой бонус", "Новое упражнение"),
        builds = listOf(
            HeroBuild("devil_may_cry", "Охотник на Демонов", "Интенсивная", 2,
                "Пицца можно. Но отработай.", "4 раза в неделю", "Контроль калорий.",
                "«Джекпот!»",
                frequency = 4, intensityMultiplier = 0.75, calorieAdjust = -0.1,
                perks = listOf("🍕 Пицца ок", "💪 Пашешь")),
            HeroBuild("rebellion", "Бунтарь", "Высокая", 3,
                "Серьёзнее.", "5 раз в неделю", "Белок.",
                "«Залей тёмную душу светом!»",
                frequency = 5, intensityMultiplier = 0.9, calorieAdjust = -0.15,
                perks = listOf("Еженедельная пицца")),
            HeroBuild("sin_devil_trigger", "Дьявольский Триггер", "Экстрим", 4,
                "Полная мощь.", "6 раз в неделю", "Контроль.",
                "«Ни человек, ни демон не будет править нами!»",
                frequency = 6, intensityMultiplier = 1.0, calorieAdjust = -0.2,
                perks = listOf("Максимум")),
            HeroBuild("veteran_hunter", "Ветеран-Охотник", "Для 45+", 3,
                "Старый Данте. Техника.", "4 раза в неделю", "Пицца раз в неделю.",
                "«Я пережил демонов.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = -0.05,
                perks = listOf("Без прыжков"), hiddenFor45 = true)
        )
    )

    val KRATOS = Hero(
        id = "kratos",
        name = "КРАТОС",
        tagline = "Призрак Спарты. Ярость. Месть.",
        color = Color(0xFFC9302C),
        bgColor = Color(0xFF0F0605),
        iconKey = "sword",
        gender = Gender.MALE,
        personality = "Спартанский воин. Ярость — топливо.",
        description = "Призрак Спарты. Тренировка — битва с богами.",
        vibe = "спартанская ярость",
        comboName = "ЯРОСТЬ",
        comboStages = listOf("СПЯЩАЯ", "РАСТЁТ", "ПЫЛАЕТ", "АДСКАЯ", "БОГОУБИЙЦА"),
        macroRatio = MacroRatio(0.40, 0.30, 0.30),
        signaturePerk = SignaturePerk("Чаша Кратоса", "Мясо + мёд после тренировки = +5 RP", "🍖"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Яичница 4 яйца + бекон", 520),
                FoodItem("Мясная нарезка + хлеб", 480),
                FoodItem("Творог + мёд + орехи", 440)
            ),
            lunch = listOf(
                FoodItem("Говядина 250г + картофель", 780),
                FoodItem("Баранина + гречка", 720),
                FoodItem("Курица + рис", 620)
            ),
            dinner = listOf(
                FoodItem("Стейк на углях", 700),
                FoodItem("Рыба + чёрный хлеб", 560),
                FoodItem("Свинина + корнеплоды", 680)
            ),
            snack = listOf(
                FoodItem("Вяленое мясо", 180),
                FoodItem("Мёд + орехи", 250),
                FoodItem("Варёные яйца", 150)
            ),
            treat = FoodItem("🍖 Пир после битвы", 800)
        ),
        rankSystem = RankSystem(
            name = "Ярость Спартанца",
            ranks = listOf("СМЕРТНЫЙ", "ВОИН", "СПАРТАНЕЦ", "ПОЛУБОГ", "ПРИЗРАК СПАРТЫ"),
            thresholds = listOf(0, 7, 20, 45, 90)
        ),
        bonusQuest = BonusQuest("flame", "Огонь Олимпа", "Холодный душ 2+ мин или баня"),
        builds = listOf(
            HeroBuild("warrior", "Путь Воина", "Интенсивная", 2,
                "Спартанская база.", "4 раза в неделю", "Мясо. Мёд.",
                "«Спартанец не знает поражения.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = -0.1,
                perks = listOf("Чистая сила")),
            HeroBuild("ghost_of_sparta", "Призрак Спарты", "Высокая", 3,
                "Ты — оружие.", "5 раз в неделю", "Высокий белок.",
                "«Мои руки утоплены в крови богов.»",
                frequency = 5, intensityMultiplier = 0.95, calorieAdjust = -0.15,
                perks = listOf("Ярость")),
            HeroBuild("god_of_war", "Бог Войны", "Экстрим", 4,
                "Убийца богов.", "6 раз в неделю", "Хищник.",
                "«Я — война.»",
                frequency = 6, intensityMultiplier = 1.1, calorieAdjust = -0.2,
                perks = listOf("Убийца богов")),
            HeroBuild("veteran_spartan", "Ветеран Спарты", "Для 45+", 3,
                "Пережил войну.", "4 раза в неделю", "Белок 1.8 г/кг.",
                "«Сила — наследие.»",
                frequency = 4, intensityMultiplier = 0.85, calorieAdjust = -0.05,
                perks = listOf("Функциональная сила"), hiddenFor45 = true)
        )
    )

    val SUNG_JINWOO = Hero(
        id = "sung_jinwoo",
        name = "СОН ДЖИН-У",
        tagline = "Квест получен. Выполнить.",
        color = Color(0xFF8B5CF6),
        bgColor = Color(0xFF0A0514),
        iconKey = "sparkles",
        gender = Gender.MALE,
        personality = "Корейский геймер-Монарх.",
        description = "Задания с дедлайном.",
        vibe = "игровая система",
        comboName = "СИСТЕМА",
        comboStages = listOf("ОФФЛАЙН", "ЗАПУСК", "АКТИВНА", "ВСПЛЕСК", "ПЕРЕГРУЗ"),
        macroRatio = MacroRatio(0.30, 0.25, 0.45),
        signaturePerk = SignaturePerk("Жетон Системы", "7 дней серии: рамен без штрафа", "🍜"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Рис + яйцо + кимчи", 420),
                FoodItem("Омлет + тост", 380),
                FoodItem("Овсянка + банан", 350)
            ),
            lunch = listOf(
                FoodItem("Бибимбап", 620),
                FoodItem("Курица терияки + рис", 580),
                FoodItem("Гречка + тофу", 480)
            ),
            dinner = listOf(
                FoodItem("Корейское барбекю", 680),
                FoodItem("Курица + овощи", 420),
                FoodItem("Рыба гриль + рис", 520)
            ),
            snack = listOf(
                FoodItem("Батончик", 200),
                FoodItem("Рисовые шарики", 250),
                FoodItem("Фрукты", 120)
            ),
            treat = FoodItem("🍜 Вечер Рамена", 650)
        ),
        rankSystem = RankSystem(
            name = "Ранг Охотника",
            ranks = listOf("E", "D", "C", "B", "A", "S", "НАЦИОНАЛЬНЫЙ"),
            thresholds = listOf(0, 5, 12, 25, 45, 75, 120)
        ),
        bonusQuest = BonusQuest("zap", "Бонус Системы", "+10 отжим, +10 присед, +10 пресс"),
        builds = listOf(
            HeroBuild("e_rank", "Охотник E-Ранга", "Базовая", 1,
                "Старт Системы.", "「Задание」: 50/50/50 + 5 км", "Рамен ок.",
                "「Дедлайн: 23:00.」",
                frequency = 7, intensityMultiplier = 0.5, calorieAdjust = 0.0,
                perks = listOf("Ежедневные задания")),
            HeroBuild("double_dungeon", "Двойное Подземелье", "Повышенная", 2,
                "Система расширилась.", "「Задание」: 100/100/100 + 10 км", "Белок.",
                "「Штраф: −5 HP.」",
                frequency = 7, intensityMultiplier = 0.8, calorieAdjust = -0.1,
                perks = listOf("Штрафы")),
            HeroBuild("shadow_monarch", "Монарх Теней", "Экстрим", 4,
                "Вершина.", "Двухразовые тренировки", "Строгий контроль",
                "「Восстань.」",
                frequency = 7, intensityMultiplier = 1.1, calorieAdjust = -0.15,
                perks = listOf("Абсолютная форма")),
            HeroBuild("retired_monarch", "Монарх на Отдыхе", "Для 45+", 3,
                "Монарх отошёл от дел.", "「Адаптивный」", "Белок.",
                "「Сила не уходит.」",
                frequency = 5, intensityMultiplier = 0.85, calorieAdjust = -0.05,
                perks = listOf("Адаптивные задания"), hiddenFor45 = true)
        )
    )

    val ALL = listOf(LEON, DANTE, KRATOS, SUNG_JINWOO)
}
