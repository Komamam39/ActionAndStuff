# Motion & Bits

Неофициальный клиентский мод для Fabric: перерисовывает ванильных мобов, игрока
и предметы в руках анимированными моделями GeckoLib из Bedrock-ресурспака
«Actions & Stuff» от Oreville Studios.

Мод сделан фанатами и **не связан с Oreville Studios и не одобрен ими**.

## Требования

- Minecraft 26.3, Fabric Loader ≥ 0.19.5
- Fabric API, GeckoLib ≥ 5.5.5
- Java 25
- Mod Menu (необязательно): даёт экран настроек

## Ассеты пака

Модели, анимации, текстуры, звуки и частицы конвертированы из пака Oreville
Studios и принадлежат им, поэтому в репозитории их нет. Сборка ищет их в
папке `pack-assets/` (она в `.gitignore`). Без неё jar соберётся, но мобы
отображаться не будут.

Заполнить `pack-assets/` из уже собранного jar мода:

```bash
scripts/extract-pack-assets.sh path/to/motionandbits.jar
```

## Сборка

```bash
./gradlew build
```

Gradle запускается на Java 25: если её нет среди установленных JDK, он скачает
её сам (`gradle/gradle-daemon-jvm.properties`). JDK в нестандартном месте можно
указать в `~/.gradle/gradle.properties` через `org.gradle.java.installations.paths`.

Готовый jar лежит в `build/libs/`. Запуск клиента для разработки:

```bash
./gradlew runClient
```

## Структура

| Пакет | Что внутри |
|---|---|
| `com.ans.client` | Основная часть: Molang, рантайм Bedrock-сущностей, рендер, частицы, звуки, настройки |
| `com.ans.perf`, `com.ans.perfutil` | Миксины с исправлениями и оптимизациями, экран-хаб настроек |
| `com.ansviewmodel` | Настройка положения рук и предметов от первого лица |

Исходники восстановлены декомпиляцией версии 0.7.0. В некоторых классах
(`FirstPerson`, `AnsHubScreen`, `ShoulderParrotLayer` и др.) локальные переменные
названы `var1`, `var2`…, потому что в байткоде не было их имён. Переименовывать
их можно по ходу работы.

## Лицензия

Код распространяется по лицензии MIT, см. [LICENSE](LICENSE). На ассеты пака
лицензия не распространяется.
