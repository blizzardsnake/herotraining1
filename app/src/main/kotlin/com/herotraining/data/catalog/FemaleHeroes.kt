package com.herotraining.data.catalog

import androidx.compose.ui.graphics.Color
import com.herotraining.data.model.*

internal object FemaleHeroes {

    val ADA = Hero(
        id = "ada",
        name = "ADA WONG",
        tagline = "Шпионаж. Точность. Грация.",
        color = Color(0xFFDC143C),
        bgColor = Color(0xFF1A0510),
        iconKey = "cat",
        gender = Gender.FEMALE,
        personality = "Femme fatale с кодексом.",
        description = "Грация шпионки. Точность агента.",
        vibe = "spy & grace",
        comboName = "SHADOW",
        comboStages = listOf("DORMANT", "COVER", "PROWL", "STRIKE", "PHANTOM"),
        macroRatio = MacroRatio(0.30, 0.30, 0.40),
        signaturePerk = SignaturePerk("Silent Hour", "20 мин сон = +3% combo", "🌙"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Смузи боул + ягоды", 380),
                FoodItem("Авокадо тост + яйцо", 420),
                FoodItem("Йогурт + гранола", 340)
            ),
            lunch = listOf(
                FoodItem("Салат с курицей", 480),
                FoodItem("Суши сет", 520),
                FoodItem("Боул с тунцом", 460)
            ),
            dinner = listOf(
                FoodItem("Лосось + овощи", 420),
                FoodItem("Курица + спаржа", 380),
                FoodItem("Паста с креветками", 540)
            ),
            snack = listOf(
                FoodItem("Тёмный шоколад 20г", 110),
                FoodItem("Яблоко + миндаль", 180),
                FoodItem("Шейк", 150)
            ),
            treat = FoodItem("🍫 Dark chocolate", 200)
        ),
        rankSystem = RankSystem(
            name = "Agent Class",
            ranks = listOf("FIELD", "AGENT", "GHOST", "PHANTOM", "WONG"),
            thresholds = listOf(0, 5, 15, 35, 75)
        ),
        bonusQuest = BonusQuest("activity", "Grace Hour", "15 мин растяжка/йога"),
        builds = listOf(
            HeroBuild("rookie_spy", "Rookie Spy", "Средняя", 2,
                "Основы шпионажа.", "4 раза/нед", "Лёгкая.",
                "«Каждое движение считается.»",
                frequency = 4, intensityMultiplier = 0.7, calorieAdjust = -0.1,
                perks = listOf("Гибкость")),
            HeroBuild("field_spy", "Field Spy", "Высокая", 3,
                "Оперативная форма.", "5 раз/нед", "Белок.",
                "«Тени — моя стихия.»",
                frequency = 5, intensityMultiplier = 0.85, calorieAdjust = -0.15,
                perks = listOf("Паркур")),
            HeroBuild("phantom_agent", "Phantom Agent", "Экстрим", 4,
                "Элитная форма.", "6 раз/нед", "Контроль.",
                "«Я исчезаю когда хочу.»",
                frequency = 6, intensityMultiplier = 1.0, calorieAdjust = -0.2,
                perks = listOf("Элитная грация")),
            HeroBuild("veteran_handler", "Veteran Handler", "Для 45+", 3,
                "Грация не стареет.", "4 раза/нед", "Коллаген.",
                "«Я играла в эту игру дольше всех.»",
                frequency = 4, intensityMultiplier = 0.75, calorieAdjust = -0.05,
                perks = listOf("Йога-основа"), hiddenFor45 = true)
        )
    )

    val LARA_CROFT = Hero(
        id = "lara_croft",
        name = "LARA CROFT",
        tagline = "Приключения. Сила. Ум.",
        color = Color(0xFF10B981),
        bgColor = Color(0xFF052016),
        iconKey = "compass",
        gender = Gender.FEMALE,
        personality = "Археолог-авантюристка.",
        description = "Универсальная подготовка.",
        vibe = "adventure ready",
        comboName = "ADVENTURE",
        comboStages = listOf("BASE CAMP", "TRAIL", "ASCENT", "SUMMIT", "LEGEND"),
        macroRatio = MacroRatio(0.30, 0.30, 0.40),
        signaturePerk = SignaturePerk("Trail Mix", "Орехи+сухофрукты как ритуал", "🥜"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Овсянка + ягоды + орехи", 450),
                FoodItem("Омлет + тост", 480),
                FoodItem("Мюсли + йогурт", 400)
            ),
            lunch = listOf(
                FoodItem("Курица + рис + овощи", 580),
                FoodItem("Паста болоньезе", 620),
                FoodItem("Сэндвич с тунцом", 520)
            ),
            dinner = listOf(
                FoodItem("Стейк + картофель", 640),
                FoodItem("Рыба + киноа", 480),
                FoodItem("Курица карри", 560)
            ),
            snack = listOf(
                FoodItem("Trail mix", 250),
                FoodItem("Банан + паста", 260),
                FoodItem("Энерджи бар", 220)
            ),
            treat = FoodItem("🍫 Экспедиционная шоколадка", 250)
        ),
        rankSystem = RankSystem(
            name = "Explorer Rank",
            ranks = listOf("ROOKIE", "SCOUT", "EXPLORER", "VETERAN", "TOMB RAIDER"),
            thresholds = listOf(0, 7, 20, 45, 90)
        ),
        bonusQuest = BonusQuest("footprints", "Field Training", "30 мин на улице или 10+ этажей"),
        builds = listOf(
            HeroBuild("scout", "Scout", "Средняя", 2,
                "Базовая подготовка.", "4 раза/нед", "Сбалансированно.",
                "«С первого шага.»",
                frequency = 4, intensityMultiplier = 0.7, calorieAdjust = 0.0,
                perks = listOf("Универсальная база")),
            HeroBuild("explorer", "Explorer", "Высокая", 3,
                "Серьёзный уровень.", "5 раз/нед", "Белок.",
                "«Ищу правильный путь.»",
                frequency = 5, intensityMultiplier = 0.85, calorieAdjust = -0.1,
                perks = listOf("Скалолазание")),
            HeroBuild("tomb_raider", "Tomb Raider", "Экстрим", 4,
                "Элитная форма.", "6 раз/нед", "Контроль.",
                "«Легенды создают те, кто не останавливается.»",
                frequency = 6, intensityMultiplier = 1.0, calorieAdjust = -0.15,
                perks = listOf("Элитная выносливость")),
            HeroBuild("seasoned_explorer", "Seasoned Explorer", "Для 45+", 3,
                "Опыт важнее скорости.", "4 раза/нед", "Белок.",
                "«Опыт важнее скорости.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = 0.0,
                perks = listOf("Ходьба с весом"), hiddenFor45 = true)
        )
    )

    val TWO_B = Hero(
        id = "twob",
        name = "2B",
        tagline = "Protocol. Execute. Repeat.",
        color = Color(0xFF94A3B8),
        bgColor = Color(0xFF0A0A14),
        iconKey = "cpu",
        gender = Gender.FEMALE,
        personality = "Боевой андроид YoRHa.",
        description = "Максимальная эффективность.",
        vibe = "protocol execute",
        comboName = "PROTOCOL",
        comboStages = listOf("STANDBY", "ACTIVE", "ENGAGED", "OVERRIDE", "BERSERK"),
        macroRatio = MacroRatio(0.35, 0.25, 0.40),
        signaturePerk = SignaturePerk("Pod Recharge", "Зелёный чай + 10 мин тишины", "🫖"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Омлет + рис + мисо", 420),
                FoodItem("Овсянка + матча", 360),
                FoodItem("Тофу + рис", 380)
            ),
            lunch = listOf(
                FoodItem("Суши сет", 560),
                FoodItem("Рамен", 620),
                FoodItem("Тэйшёку", 540)
            ),
            dinner = listOf(
                FoodItem("Терияки лосось + рис", 580),
                FoodItem("Курица темпура", 520),
                FoodItem("Шабу-шабу", 460)
            ),
            snack = listOf(
                FoodItem("Онигири", 200),
                FoodItem("Эдамаме", 150),
                FoodItem("Чай + моти", 160)
            ),
            treat = FoodItem("🫖 Matcha + Mochi", 280)
        ),
        rankSystem = RankSystem(
            name = "YoRHa Class",
            ranks = listOf("UNIT-E", "UNIT-D", "UNIT-C", "UNIT-B", "UNIT-A", "TYPE-S", "EXECUTIONER"),
            thresholds = listOf(0, 5, 12, 25, 45, 75, 120)
        ),
        bonusQuest = BonusQuest("cpu", "System Check", "10 мин медитации"),
        builds = listOf(
            HeroBuild("unit_standard", "Standard Unit", "Базовая", 2,
                "Базовый протокол.", "4 раза/нед", "По макросам.",
                "«Эмоции запрещены.»",
                frequency = 4, intensityMultiplier = 0.75, calorieAdjust = -0.1,
                perks = listOf("Чёткий протокол")),
            HeroBuild("combat_model", "Combat Model", "Высокая", 3,
                "Боевая модель.", "5 раз/нед", "Белок.",
                "«Я — 2B.»",
                frequency = 5, intensityMultiplier = 0.9, calorieAdjust = -0.15,
                perks = listOf("CQC")),
            HeroBuild("executioner", "Executioner", "Экстрим", 4,
                "Высший протокол.", "6 раз/нед", "Максимум.",
                "«Всё живое должно быть казнено.»",
                frequency = 6, intensityMultiplier = 1.05, calorieAdjust = -0.2,
                perks = listOf("Элитный протокол")),
            HeroBuild("legacy_unit", "Legacy Unit", "Для 45+", 3,
                "Старая модель.", "4 раза/нед", "Коллаген.",
                "«Протокол не стареет.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = -0.05,
                perks = listOf("Low-impact"), hiddenFor45 = true)
        )
    )

    val CIRI = Hero(
        id = "ciri",
        name = "CIRI",
        tagline = "Элдер-Блад. Клинок. Воля.",
        color = Color(0xFFD4D4D4),
        bgColor = Color(0xFF0A1014),
        iconKey = "crosshair",
        gender = Gender.FEMALE,
        personality = "Ведьмачка школы Кота.",
        description = "Тренировка ведьмака + элдер-блад.",
        vibe = "witcher discipline",
        comboName = "ELDER BLOOD",
        comboStages = listOf("DORMANT", "FOCUS", "CHANNELING", "BLINK", "ELDER"),
        macroRatio = MacroRatio(0.38, 0.28, 0.34),
        signaturePerk = SignaturePerk("Zireael Brew", "Зелёный чай + куркума", "🗡️"),
        foodLibrary = FoodLibrary(
            breakfast = listOf(
                FoodItem("Яичница + овощи + кофе", 420),
                FoodItem("Овсянка + протеин", 380),
                FoodItem("Йогурт + орехи", 360)
            ),
            lunch = listOf(
                FoodItem("Курица + рис + овощи", 560),
                FoodItem("Говядина + картофель", 620),
                FoodItem("Тунец + киноа", 480)
            ),
            dinner = listOf(
                FoodItem("Стейк + овощи", 580),
                FoodItem("Грудка + брокколи", 480),
                FoodItem("Рыба + спаржа", 520)
            ),
            snack = listOf(
                FoodItem("Шейк", 150),
                FoodItem("Яблоко + паста", 220),
                FoodItem("Вяленое мясо", 180)
            ),
            treat = FoodItem("🌿 Чай + тёмный шоколад", 180)
        ),
        rankSystem = RankSystem(
            name = "Witcher Rank",
            ranks = listOf("SWALLOW", "TRAINEE", "WITCHERESS", "ZIREAEL", "ELDER BLOOD"),
            thresholds = listOf(0, 7, 20, 45, 90)
        ),
        bonusQuest = BonusQuest("target", "Blink Drill", "5 мин дыхательные/прицеливание"),
        builds = listOf(
            HeroBuild("trainee", "Kaer Morhen Trainee", "Средняя", 2,
                "Базовая подготовка ведьмака.", "4 раза/нед", "Белок.",
                "«Я готова учиться.»",
                frequency = 4, intensityMultiplier = 0.75, calorieAdjust = -0.1,
                perks = listOf("Функциональная сила")),
            HeroBuild("hunted", "Wild Hunt Hunted", "Высокая", 3,
                "Тебя преследуют.", "5 раз/нед", "Белок.",
                "«Я не дам себя поймать.»",
                frequency = 5, intensityMultiplier = 0.9, calorieAdjust = -0.15,
                perks = listOf("Тактический кроссфит")),
            HeroBuild("zireael", "Zireael", "Экстрим", 4,
                "Элдер-блад раскрыт.", "6 раз/нед", "Контроль.",
                "«Я — ласточка.»",
                frequency = 6, intensityMultiplier = 1.0, calorieAdjust = -0.2,
                perks = listOf("Элитный уровень")),
            HeroBuild("veteran_witcheress", "Veteran Witcheress", "Для 45+", 3,
                "Ветеран Школы Кота.", "4 раза/нед", "Белок.",
                "«Я пережила всё.»",
                frequency = 4, intensityMultiplier = 0.8, calorieAdjust = -0.05,
                perks = listOf("Low-impact"), hiddenFor45 = true)
        )
    )

    val ALL = listOf(ADA, LARA_CROFT, TWO_B, CIRI)
}
