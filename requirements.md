# Pokemon TCG Collector — Описание задачи

## Обзор проекта

Android-приложение для коллекционеров карт Pokemon TCG. Позволяет сканировать физические карты камерой телефона, сохранять коллекцию, размечать карты тегами, отслеживать прогресс по наборам и вести список желаемых карт.

---

## Источник данных

Основная база карт: **https://www.pokemon.com/us/pokemon-tcg/pokemon-cards**

Для получения структурированных данных используется **Pokemon TCG API** (https://pokemontcg.io) — неофициальное зеркало данных с pokemon.com, содержащее ~20 000 карт со всеми метаданными и изображениями. Бесплатный доступ без ключа для базовых запросов.

---

## Экраны приложения

### 1. Сканер карт
- Открытие камеры с превью в реальном времени (CameraX)
- OCR-распознавание текста на карте (ML Kit Text Recognition):
  - Название покемона
  - Номер карты в наборе (например: `4/102`)
  - Название набора
- Поиск карты в Pokemon TCG API по распознанным данным
- Отображение найденных вариантов для подтверждения пользователем
- Ручной поиск карты по названию / номеру (если OCR не справился)
- Кнопка «Добавить в коллекцию» после подтверждения

### 2. Коллекция
- Сетка или список всех добавленных карт
- Изображения карт (загружаются с pokemon.com через Coil)
- Фильтрация по:
  - Тегам
  - Набору (сету)
  - Типу покемона
  - Редкости
  - Wishlist
- Поиск по названию карты
- Сортировка: по дате добавления, по имени, по номеру

### 3. Наборы (Sets)
- Список всех наборов Pokemon TCG из API
- Для каждого набора:
  - Логотип и название
  - Прогресс: `X / Y карт собрано` + прогресс-бар
  - Количество пропущенных карт
- Переход в детали набора

### 4. Детали набора
- Полный список карт набора (сетка)
- Визуальная метка: ✅ есть в коллекции / 🔲 отсутствует
- Быстрое добавление карты прямо с этого экрана
- Кнопка «Добавить все пропущенные в Wishlist»
- Прогресс-бар заполнения набора

### 5. Детали карты
- Фото карты в высоком разрешении
- Метаданные:
  - Название, номер, набор
  - Тип покемона, редкость, HP
  - Атаки, слабости, сопротивления
- Управление тегами: добавить / удалить теги
- Поле для личных заметок
- Чекбокс «Wishlist»
- Чекбокс «На обмен»

### 6. Теги
- Создание пользовательских тегов с цветовой маркировкой
- Встроенные теги-примеры: `Любимая`, `На обмен`, `Mint`, `Топ-колода`, `Holographic`
- Фильтрация коллекции по тегам
- Редактирование и удаление тегов

### 7. Статистика (дашборд)
- Общее количество карт в коллекции
- Распределение по типам покемонов (график)
- Распределение по редкости
- Количество завершённых наборов
- Размер Wishlist

---

## База данных (Room)

```
cards
  id             INTEGER PRIMARY KEY
  tcg_api_id     TEXT UNIQUE       -- ID из pokemontcg.io
  name           TEXT
  set_id         TEXT              -- FK → sets.tcg_api_id
  number         TEXT              -- номер в наборе (напр. "4/102")
  image_url      TEXT
  rarity         TEXT
  pokemon_type   TEXT
  hp             INTEGER
  notes          TEXT              -- личные заметки
  in_wishlist    INTEGER DEFAULT 0
  for_trade      INTEGER DEFAULT 0
  added_at       INTEGER           -- unix timestamp

sets
  id             INTEGER PRIMARY KEY
  tcg_api_id     TEXT UNIQUE
  name           TEXT
  series         TEXT
  total_cards    INTEGER
  logo_url       TEXT
  release_date   TEXT

tags
  id             INTEGER PRIMARY KEY
  name           TEXT
  color          TEXT              -- hex-цвет (#FF5733)

card_tags                          -- связь many-to-many
  card_id        INTEGER FK → cards.id
  tag_id         INTEGER FK → tags.id
  PRIMARY KEY (card_id, tag_id)
```

---

## Технический стек

| Слой | Технология |
|---|---|
| Язык | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Архитектура | MVVM + Repository pattern |
| Async | Coroutines + StateFlow |
| Камера | CameraX |
| OCR | ML Kit Text Recognition |
| БД | Room (SQLite) |
| Сеть | Retrofit + OkHttp |
| Изображения | Coil |
| DI | Hilt |
| Навигация | Navigation Compose |

---

## Этапы разработки

### MVP (Этап 1)
- [ ] Настройка проекта, Hilt, Room, Retrofit
- [ ] Интеграция Pokemon TCG API
- [ ] Экран сканера (камера + OCR + поиск)
- [ ] Экран коллекции (список карт)
- [ ] Экран деталей карты
- [ ] Локальное хранение в Room

### Этап 2
- [ ] Экран наборов и деталей набора
- [ ] Прогресс заполнения набора
- [ ] Пустые карты / Wishlist

### Этап 3
- [ ] Система тегов
- [ ] Фильтры и сортировка
- [ ] Статистика / дашборд
- [ ] Экспорт коллекции (JSON / CSV)

---

## Структура проекта

```
app/
├── data/
│   ├── local/         # Room DAO, entities, database
│   ├── remote/        # Retrofit API, DTOs
│   └── repository/    # CardRepository, SetRepository
├── domain/
│   └── model/         # Card, Set, Tag (domain models)
├── ui/
│   ├── scanner/       # ScannerScreen, ScannerViewModel
│   ├── collection/    # CollectionScreen, CollectionViewModel
│   ├── sets/          # SetsScreen, SetDetailScreen
│   ├── carddetail/    # CardDetailScreen
│   ├── tags/          # TagsScreen
│   └── stats/         # StatsScreen
└── di/                # Hilt modules
```
