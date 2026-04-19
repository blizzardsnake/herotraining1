package com.herotraining.data.catalog.exercises

import com.herotraining.data.model.Injury

/**
 * Курированный каталог упражнений — v0.9.0 = ~45 шт.
 *
 * Отбор: базовые паттерны движения (push/pull/legs/hinge/squat/carry) + популярные
 * вариации + минимум core и cardio. Без эзотерики и гимнастики которые новичок не
 * освоит сам без тренера.
 *
 * Переводы: ru-имя — то как гуглится в русских роликах; en-имя — международное
 * название для возможного поиска ютуб-туториала в будущем.
 *
 * Фото: пока НЕ прикладываем (v0.9.1 — возьмём из yuhonas/free-exercise-db MIT).
 */
object ExerciseLibrary {

    val ALL: List<Exercise> = listOf(
        // =========================================================
        // PUSH — chest / shoulders / triceps
        // =========================================================
        Exercise(
            id = "pushup_standard", nameRu = "Отжимания классические", nameEn = "Push-up",
            primary = BodyPart.PUSH_CHEST, secondary = listOf(BodyPart.PUSH_SHOULDERS, BodyPart.PUSH_TRICEPS, BodyPart.CORE_ABS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Руки на ширине плеч, корпус прямой от пяток до макушки. Опускайся грудью к полу, локти уходят назад под ~45°.",
            baselineId = "pushups",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "pushup_knee", nameRu = "Отжимания с колен", nameEn = "Knee Push-up",
            primary = BodyPart.PUSH_CHEST, secondary = listOf(BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Упор на колени вместо носков. Всё остальное как в классических — корпус прямой, локти под 45°."
        ),
        Exercise(
            id = "pushup_incline", nameRu = "Отжимания с возвышения", nameEn = "Incline Push-up",
            primary = BodyPart.PUSH_CHEST,
            gearAny = setOf(GearKind.BODYWEIGHT, GearKind.BENCH), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Руки на скамью/диван/стол. Чем выше опора — тем легче. Идеальный шаг перед классикой."
        ),
        Exercise(
            id = "pushup_diamond", nameRu = "Отжимания алмазом", nameEn = "Diamond Push-up",
            primary = BodyPart.PUSH_TRICEPS, secondary = listOf(BodyPart.PUSH_CHEST),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Ладони под грудью — большие и указательные пальцы образуют ромб. Локти прижимай к корпусу.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "pushup_pike", nameRu = "Отжимания уголком", nameEn = "Pike Push-up",
            primary = BodyPart.PUSH_SHOULDERS, secondary = listOf(BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Таз высоко, голова между рук. Отжимайся макушкой к полу — работает дельта.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "dips_bench", nameRu = "Отжимания от скамьи спиной", nameEn = "Bench Dips",
            primary = BodyPart.PUSH_TRICEPS, secondary = listOf(BodyPart.PUSH_SHOULDERS),
            gearAny = setOf(GearKind.BENCH), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Спиной к скамье, руки на край. Опускайся сгибая локти до 90°, поднимайся трицепсом.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "bench_dumbbell", nameRu = "Жим гантелей лёжа", nameEn = "Dumbbell Bench Press",
            primary = BodyPart.PUSH_CHEST, secondary = listOf(BodyPart.PUSH_SHOULDERS, BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Лёжа на скамье (или на полу). Гантели опускай до уровня груди, локти ~45°, без рывков."
        ),
        Exercise(
            id = "shoulder_press_dumbbell", nameRu = "Жим гантелей стоя", nameEn = "Standing Shoulder Press",
            primary = BodyPart.PUSH_SHOULDERS, secondary = listOf(BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Стоя или сидя. Гантели на уровне ушей — выжимай вверх, не разгибай локти полностью.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "lateral_raise", nameRu = "Махи гантелями в стороны", nameEn = "Lateral Raise",
            primary = BodyPart.PUSH_SHOULDERS,
            gearAny = setOf(GearKind.DUMBBELLS, GearKind.BAND), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Маленький вес. Поднимай гантели через стороны до уровня плеч — локти чуть согнуты."
        ),
        Exercise(
            id = "bench_barbell", nameRu = "Жим штанги лёжа", nameEn = "Barbell Bench Press",
            primary = BodyPart.PUSH_CHEST, secondary = listOf(BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.BARBELL), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Лопатки сведены. Опускай штангу на грудь, без отбива, выжимай прямо над грудью."
        ),
        Exercise(
            id = "overhead_press_barbell", nameRu = "Армейский жим", nameEn = "Overhead Press",
            primary = BodyPart.PUSH_SHOULDERS, secondary = listOf(BodyPart.PUSH_TRICEPS),
            gearAny = setOf(GearKind.BARBELL), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Стоя. Штанга с груди вверх строго вертикально. Корпус жёсткий — без раскачки.",
            excludeIf = setOf(Injury.SHOULDERS, Injury.BACK)
        ),

        // =========================================================
        // PULL — back / biceps
        // =========================================================
        Exercise(
            id = "pullup_classic", nameRu = "Подтягивания прямым хватом", nameEn = "Pull-up",
            primary = BodyPart.PULL_BACK, secondary = listOf(BodyPart.PULL_BICEPS),
            gearAny = setOf(GearKind.PULL_BAR), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Хват чуть шире плеч. Подбородок выше перекладины, опускайся до полного повисания.",
            baselineId = "pullups",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "pullup_chinup", nameRu = "Подтягивания обратным хватом", nameEn = "Chin-up",
            primary = BodyPart.PULL_BICEPS, secondary = listOf(BodyPart.PULL_BACK),
            gearAny = setOf(GearKind.PULL_BAR), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Ладонями к себе. Проще классики — бицепс включается сильнее.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "pullup_negative", nameRu = "Негативные подтягивания", nameEn = "Negative Pull-up",
            primary = BodyPart.PULL_BACK,
            gearAny = setOf(GearKind.PULL_BAR), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Подпрыгиваешь в верхнюю точку — медленно опускаешься 3-5 секунд. Подготовка к полноценным.",
            excludeIf = setOf(Injury.SHOULDERS)
        ),
        Exercise(
            id = "row_inverted", nameRu = "Австралийские подтягивания", nameEn = "Inverted Row",
            primary = BodyPart.PULL_BACK, secondary = listOf(BodyPart.PULL_BICEPS),
            gearAny = setOf(GearKind.PULL_BAR, GearKind.BAND), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Низкая перекладина или петли. Тело прямое, тянись грудью к грифу."
        ),
        Exercise(
            id = "row_dumbbell_single", nameRu = "Тяга гантели одной рукой", nameEn = "Single-arm DB Row",
            primary = BodyPart.PULL_BACK, secondary = listOf(BodyPart.PULL_BICEPS),
            gearAny = setOf(GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Упор коленом на скамью. Тяни гантель к поясу, локоть вдоль корпуса.",
            excludeIf = setOf(Injury.BACK)
        ),
        Exercise(
            id = "deadlift_romanian", nameRu = "Румынская тяга", nameEn = "Romanian Deadlift",
            primary = BodyPart.LEGS_HAMSTRINGS, secondary = listOf(BodyPart.LEGS_GLUTES, BodyPart.PULL_BACK),
            gearAny = setOf(GearKind.DUMBBELLS, GearKind.BARBELL), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Колени чуть согнуты. Опускай вес, наклоняясь в бёдрах — спина прямая, живот в тонусе.",
            excludeIf = setOf(Injury.BACK)
        ),
        Exercise(
            id = "curl_biceps_dumbbell", nameRu = "Сгибания на бицепс", nameEn = "Dumbbell Curl",
            primary = BodyPart.PULL_BICEPS,
            gearAny = setOf(GearKind.DUMBBELLS, GearKind.BAND), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Локти прижаты к корпусу. Поднимай к плечу, медленно опускай."
        ),
        Exercise(
            id = "curl_hammer", nameRu = "Молотковые сгибания", nameEn = "Hammer Curl",
            primary = BodyPart.PULL_BICEPS,
            gearAny = setOf(GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Ладони смотрят друг на друга. Прокачка плечевой и бицепса — меньше напряжения на запястья."
        ),

        // =========================================================
        // LEGS
        // =========================================================
        Exercise(
            id = "squat_air", nameRu = "Приседания без веса", nameEn = "Air Squat",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Ноги на ширине плеч. Таз назад, колени не заваливаются внутрь, глубина — до параллели.",
            baselineId = "squats"
        ),
        Exercise(
            id = "squat_goblet", nameRu = "Приседания с гантелью у груди", nameEn = "Goblet Squat",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES, BodyPart.CORE_ABS),
            gearAny = setOf(GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Гантель у груди, локти вниз. Приседай глубже чем в воздушных — вес балансирует корпус.",
            excludeIf = setOf(Injury.KNEES)
        ),
        Exercise(
            id = "squat_barbell", nameRu = "Приседания со штангой на спине", nameEn = "Back Squat",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES, BodyPart.PULL_BACK),
            gearAny = setOf(GearKind.BARBELL), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Штанга на трапециях. Глубина — до параллели или ниже. Колени направлены туда же куда носки.",
            excludeIf = setOf(Injury.KNEES, Injury.BACK)
        ),
        Exercise(
            id = "squat_jump", nameRu = "Прыжки из приседа", nameEn = "Jump Squat",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.CARDIO),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Приседаешь в параллель, взрывной прыжок вверх. Мягкое приземление на всю стопу.",
            excludeIf = setOf(Injury.KNEES, Injury.CARDIO)
        ),
        Exercise(
            id = "lunge_walking", nameRu = "Выпады в движении", nameEn = "Walking Lunge",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES),
            gearAny = setOf(GearKind.BODYWEIGHT, GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Шаг вперёд, колено передней ноги над стопой, заднее — не касается пола. Смена ноги каждый шаг.",
            excludeIf = setOf(Injury.KNEES)
        ),
        Exercise(
            id = "lunge_reverse", nameRu = "Обратные выпады", nameEn = "Reverse Lunge",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES),
            gearAny = setOf(GearKind.BODYWEIGHT, GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Шагаешь НАЗАД — опускайся прямо вниз. Менее травматично для коленей чем классические.",
            excludeIf = setOf(Injury.KNEES)
        ),
        Exercise(
            id = "lunge_bulgarian", nameRu = "Болгарские сплит-приседания", nameEn = "Bulgarian Split Squat",
            primary = BodyPart.LEGS_QUADS, secondary = listOf(BodyPart.LEGS_GLUTES),
            gearAny = setOf(GearKind.BENCH, GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Задняя нога на скамье. Глубокий присед на передней — самое жёсткое упражнение на ноги.",
            excludeIf = setOf(Injury.KNEES)
        ),
        Exercise(
            id = "deadlift_classic", nameRu = "Классическая становая тяга", nameEn = "Deadlift",
            primary = BodyPart.LEGS_HAMSTRINGS, secondary = listOf(BodyPart.LEGS_GLUTES, BodyPart.PULL_BACK),
            gearAny = setOf(GearKind.BARBELL), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Гриф над серединой стопы. Подъём ногами, спина прямая. Королева упражнений.",
            excludeIf = setOf(Injury.BACK)
        ),
        Exercise(
            id = "glute_bridge", nameRu = "Ягодичный мост", nameEn = "Glute Bridge",
            primary = BodyPart.LEGS_GLUTES, secondary = listOf(BodyPart.LEGS_HAMSTRINGS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Лёжа на спине, стопы близко к ягодицам. Поднимай таз до прямой линии, сжимая ягодицы."
        ),
        Exercise(
            id = "hip_thrust", nameRu = "Ягодичный мост с опорой", nameEn = "Hip Thrust",
            primary = BodyPart.LEGS_GLUTES, secondary = listOf(BodyPart.LEGS_HAMSTRINGS),
            gearAny = setOf(GearKind.BENCH), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Верх спины на скамье, вес на тазе. Толкай таз вверх — делит всё на до и после для ягодиц."
        ),
        Exercise(
            id = "calf_raise", nameRu = "Подъёмы на носки стоя", nameEn = "Standing Calf Raise",
            primary = BodyPart.LEGS_CALVES,
            gearAny = setOf(GearKind.BODYWEIGHT, GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "На ровной или на ступеньке (для большей амплитуды). Высокое число повторов."
        ),
        Exercise(
            id = "wall_sit", nameRu = "Присед у стены", nameEn = "Wall Sit",
            primary = BodyPart.LEGS_QUADS,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Спиной к стене, колени под 90°. Просто держи время — квадрицепс горит.",
            excludeIf = setOf(Injury.KNEES)
        ),

        // =========================================================
        // CORE
        // =========================================================
        Exercise(
            id = "plank", nameRu = "Планка на локтях", nameEn = "Plank",
            primary = BodyPart.CORE_ABS, secondary = listOf(BodyPart.PUSH_SHOULDERS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Локти под плечами. Тело прямое — не проваливай таз, не поднимай. Лопатки к позвоночнику.",
            baselineId = "plank"
        ),
        Exercise(
            id = "plank_side", nameRu = "Боковая планка", nameEn = "Side Plank",
            primary = BodyPart.CORE_OBLIQUES, secondary = listOf(BodyPart.CORE_ABS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.INTERMEDIATE,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "На одном локте. Бёдра не провисают. Держи время, потом другая сторона."
        ),
        Exercise(
            id = "crunch", nameRu = "Скручивания", nameEn = "Crunch",
            primary = BodyPart.CORE_ABS,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Лёжа, колени согнуты. Подними только верх корпуса — поясница на полу.",
            excludeIf = setOf(Injury.BACK)
        ),
        Exercise(
            id = "leg_raise_lying", nameRu = "Подъём ног лёжа", nameEn = "Lying Leg Raise",
            primary = BodyPart.CORE_ABS,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Лёжа, ноги прямые. Подними до 90°, медленно опусти — не касайся пола между подъёмами.",
            excludeIf = setOf(Injury.BACK)
        ),
        Exercise(
            id = "leg_raise_hanging", nameRu = "Подъём ног в висе", nameEn = "Hanging Leg Raise",
            primary = BodyPart.CORE_ABS,
            gearAny = setOf(GearKind.PULL_BAR), difficulty = ExerciseDifficulty.ADVANCED,
            instructionRu = "Вис на перекладине. Подними ноги до параллели с полом (или выше если сможешь).",
            excludeIf = setOf(Injury.SHOULDERS, Injury.BACK)
        ),
        Exercise(
            id = "bicycle", nameRu = "Велосипед", nameEn = "Bicycle Crunch",
            primary = BodyPart.CORE_OBLIQUES, secondary = listOf(BodyPart.CORE_ABS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Лёжа. Касайся локтем противоположного колена, попеременно. Темп средний."
        ),
        Exercise(
            id = "russian_twist", nameRu = "Русские скручивания", nameEn = "Russian Twist",
            primary = BodyPart.CORE_OBLIQUES,
            gearAny = setOf(GearKind.BODYWEIGHT, GearKind.DUMBBELLS), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Сядь, корпус под 45°, ноги навесу. Поворачивай корпус поочерёдно влево-вправо."
        ),
        Exercise(
            id = "mountain_climber", nameRu = "Скалолаз", nameEn = "Mountain Climber",
            primary = BodyPart.CORE_ABS, secondary = listOf(BodyPart.CARDIO),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Упор лёжа. Поочерёдно подтягивай колени к груди в быстром темпе."
        ),

        // =========================================================
        // CARDIO
        // =========================================================
        Exercise(
            id = "burpee", nameRu = "Бёрпи", nameEn = "Burpee",
            primary = BodyPart.CARDIO, secondary = listOf(BodyPart.PUSH_CHEST, BodyPart.LEGS_QUADS),
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.INTERMEDIATE,
            instructionRu = "Присед → упор лёжа → отжимание → прыжок вверх с хлопком. Одна повторушка.",
            baselineId = "burpees",
            excludeIf = setOf(Injury.KNEES, Injury.CARDIO)
        ),
        Exercise(
            id = "jumping_jack", nameRu = "Прыжки-звёздочка", nameEn = "Jumping Jack",
            primary = BodyPart.CARDIO,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Прыжок с разведением рук и ног — и обратно. Простой способ поднять пульс."
        ),
        Exercise(
            id = "high_knees", nameRu = "Бег на месте с высоким коленом", nameEn = "High Knees",
            primary = BodyPart.CARDIO,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Колени до пояса. Руки работают как в беге. Кор включён."
        ),
        Exercise(
            id = "jump_rope", nameRu = "Скакалка", nameEn = "Jump Rope",
            primary = BodyPart.CARDIO,
            gearAny = setOf(GearKind.ROPE), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Ритм ровный. Приземляйся на переднюю часть стопы, не на пятки.",
            excludeIf = setOf(Injury.CARDIO)
        ),
        Exercise(
            id = "run_outdoor", nameRu = "Бег", nameEn = "Run",
            primary = BodyPart.CARDIO,
            gearAny = setOf(GearKind.OUTDOOR), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_MIN,
            instructionRu = "Легкий темп — можешь говорить не задыхаясь. Стартуем с 10-15 минут.",
            baselineId = "cardio",
            excludeIf = setOf(Injury.CARDIO)
        ),

        // =========================================================
        // MOBILITY
        // =========================================================
        Exercise(
            id = "cat_cow", nameRu = "Кошка-корова", nameEn = "Cat-Cow",
            primary = BodyPart.MOBILITY,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "На четвереньках. Прогибайся и округляйся поочерёдно — разминает позвоночник."
        ),
        Exercise(
            id = "downward_dog", nameRu = "Собака мордой вниз", nameEn = "Downward Dog",
            primary = BodyPart.MOBILITY,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Таз к потолку, пятки тянутся к полу. Растягивает заднюю поверхность и плечи."
        ),
        Exercise(
            id = "hip_flexor_stretch", nameRu = "Растяжка сгибателей бедра", nameEn = "Hip Flexor Stretch",
            primary = BodyPart.MOBILITY,
            gearAny = setOf(GearKind.BODYWEIGHT), difficulty = ExerciseDifficulty.BEGINNER,
            unit = ExerciseUnit.TIME_SEC,
            instructionRu = "Выпад с опущенным коленом. Таз ВПЕРЁД чтоб почувствовать переднюю часть бедра."
        ),
        Exercise(
            id = "shoulder_dislocate", nameRu = "Выкруты плеч с резинкой", nameEn = "Shoulder Dislocates",
            primary = BodyPart.MOBILITY,
            gearAny = setOf(GearKind.BAND), difficulty = ExerciseDifficulty.BEGINNER,
            instructionRu = "Резинка широким хватом. Круг руками через голову вперёд-назад. Мобильность плеч."
        )
    )

    fun byId(id: String): Exercise? = ALL.firstOrNull { it.id == id }

    /** Все упражнения которые подходят под инвентарь и НЕ заблокированы травмами. */
    fun available(
        equipment: com.herotraining.data.model.EquipmentKind,
        injuries: Set<Injury>
    ): List<Exercise> = ALL.filter {
        it.fitsEquipment(equipment) && !it.blockedByInjuries(injuries)
    }

    /** Упражнения конкретной группы мышц — с учётом инвентаря и травм. */
    fun forBodyPart(
        part: BodyPart,
        equipment: com.herotraining.data.model.EquipmentKind,
        injuries: Set<Injury>
    ): List<Exercise> = available(equipment, injuries).filter {
        it.primary == part || part in it.secondary
    }
}
