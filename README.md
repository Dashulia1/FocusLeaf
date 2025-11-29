# FocusLeaf 2.0

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Min SDK](https://img.shields.io/badge/min%20sdk-21-green.svg)
![Target SDK](https://img.shields.io/badge/target%20sdk-34-orange.svg)
![Kotlin](https://img.shields.io/badge/kotlin-1.9.20-purple.svg)

**Приложение для повышения продуктивности на основе техники Pomodoro**

[Описание](#-описание) • [Установка](#-установка) • [Структура](#-структура-проекта) • [Архитектура](#-архитектура) • [База данных](#-схема-базы-данных)

</div>

---

## 📖 Описание

**FocusLeaf** — это мобильное приложение для Android, которое помогает пользователям повысить продуктивность с помощью техники Pomodoro. Приложение позволяет организовывать работу в фокус-интервалы, управлять проектами, отслеживать статистику и достижения.

### Основные возможности

- 🍅 **Таймер Pomodoro** — работа и отдых в структурированных интервалах
- 📊 **Управление проектами** — создание и организация проектов с приоритетами
- 📈 **Аналитика и статистика** — отслеживание прогресса по дням и неделям
- 🎯 **Достижения** — система наград за последовательную работу
- 👥 **Социальные функции** — совместные сессии и поиск друзей
- 💬 **Чат поддержки** — помощь и консультации
- 🎨 **Настройки темы** — светлая, темная и системная темы
- 📱 **Многоязычность** — поддержка русского и английского языков

---

## 🚀 Установка

### Требования

- **Android Studio** Hedgehog (2023.1.1) или новее
- **JDK** 8 или выше
- **Android SDK** с API Level 21 (Android 5.0 Lollipop) и выше
- **Kotlin** 1.9.20
- **Gradle** 8.3.2

### Шаг 1: Клонирование репозитория

```bash
git clone <repository-url>
cd FocusLeaf2
```

### Шаг 2: Настройка проекта

1. Откройте проект в Android Studio
2. Дождитесь синхронизации Gradle (может занять несколько минут при первом запуске)
3. Убедитесь, что SDK и зависимости загружены корректно

### Шаг 3: Настройка изображений онбординга (опционально)

Для корректного отображения экранов онбординга разместите изображения в папке:

```
app/src/main/res/drawable/
```

Требуемые файлы:
- `onboarding_0_character.png` — первый экран
- `onboarding_1_organization.png` — второй экран
- `onboarding_2_analytics.png` — третий экран
- `onboarding_3_success.png` — четвертый экран

### Шаг 4: Сборка и запуск

1. Подключите Android устройство или запустите эмулятор
2. В Android Studio: **Build → Rebuild Project**
3. Нажмите **Run** (Shift+F10) или выберите **Run → Run 'app'**

### Первый запуск

При первом запуске приложения:
1. Вы увидите экраны онбординга
2. После онбординга необходимо пройти авторизацию
3. Затем откроется главный экран с таймером Pomodoro

### Сброс онбординга для тестирования

**Способ 1:** Через приложение
- Откройте Профиль → зажмите и удерживайте кнопку "Выйти"
- Подтвердите сброс онбординга

**Способ 2:** Очистить данные приложения
- Настройки устройства → Приложения → FocusLeaf → Хранилище → Очистить данные

**Способ 3:** Через ADB
```bash
adb shell pm clear com.dasasergeeva.focusleaf2
```

---

## 📁 Структура проекта

```
FocusLeaf2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dasasergeeva/focusleaf2/
│   │   │   │   ├── analytics/              # Модуль аналитики
│   │   │   │   │   ├── AnalyticsPagerAdapter.kt
│   │   │   │   │   ├── AnalyticsTodayFragment.kt
│   │   │   │   │   └── AnalyticsWeekFragment.kt
│   │   │   │   ├── models/                 # Модели данных
│   │   │   │   │   ├── Project.kt
│   │   │   │   │   ├── ProjectStats.kt
│   │   │   │   │   └── OverallStats.kt
│   │   │   │   ├── onboarding/            # Онбординг
│   │   │   │   │   ├── OnboardingPagerAdapter.kt
│   │   │   │   │   └── OnboardingFragment[0-3].kt
│   │   │   │   ├── projects/              # Модуль проектов
│   │   │   │   │   └── ProjectAdapter.kt
│   │   │   │   ├── repository/            # Репозитории
│   │   │   │   │   └── ProjectRepository.kt
│   │   │   │   ├── search/                # Поиск
│   │   │   │   │   └── ProjectsSearchAdapter.kt
│   │   │   │   ├── ui/                    # UI компоненты
│   │   │   │   │   └── CircleTimerView.kt
│   │   │   │   ├── utils/                 # Утилиты и менеджеры
│   │   │   │   │   ├── PreferencesManager.kt
│   │   │   │   │   ├── UserManager.kt
│   │   │   │   │   ├── StatisticsManager.kt
│   │   │   │   │   ├── AnalyticsManager.kt
│   │   │   │   │   ├── ThemeManager.kt
│   │   │   │   │   ├── TimerSettingsManager.kt
│   │   │   │   │   └── ...
│   │   │   │   ├── *.kt                   # Activities
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── OnboardingActivity.kt
│   │   │   │   │   ├── LoginActivity.kt
│   │   │   │   │   ├── ProjectsActivity.kt
│   │   │   │   │   ├── ProfileActivity.kt
│   │   │   │   │   └── ...
│   │   │   │   └── BaseActivity.kt        # Базовый класс Activity
│   │   │   ├── res/                       # Ресурсы
│   │   │   │   ├── layout/                # Layout файлы
│   │   │   │   ├── drawable/              # Изображения и иконки
│   │   │   │   ├── values/                # Строки, цвета, темы
│   │   │   │   └── values-ru/             # Русская локализация
│   │   │   └── AndroidManifest.xml
│   │   └── test/                          # Тесты
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

### Основные пакеты

- **`analytics/`** — модуль аналитики (дневная и недельная статистика)
- **`models/`** — модели данных (Project, ProjectStats, OverallStats)
- **`onboarding/`** — экраны онбординга (4 фрагмента)
- **`projects/`** — управление проектами
- **`repository/`** — репозитории для работы с данными
- **`search/`** — поиск проектов и друзей
- **`ui/`** — кастомные UI компоненты
- **`utils/`** — утилиты и менеджеры (PreferencesManager, StatisticsManager и др.)

---

## 🏗️ Архитектура

### Общая архитектура

Проект использует **модульную архитектуру** с четким разделением ответственности:

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │Activity  │  │Activity  │  │Activity  │  ...         │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘              │
│       │             │             │                     │
│       └─────────────┼─────────────┘                     │
│                     │                                   │
│              ┌──────▼──────┐                           │
│              │  BaseActivity│                           │
│              └──────┬──────┘                           │
└─────────────────────┼──────────────────────────────────┘
                      │
┌─────────────────────┼──────────────────────────────────┐
│                     │      Business Logic Layer        │
│       ┌─────────────▼──────────────┐                  │
│       │      Managers / Utils       │                  │
│       │  ┌──────────────────────┐  │                  │
│       │  │PreferencesManager    │  │                  │
│       │  │StatisticsManager     │  │                  │
│       │  │AnalyticsManager      │  │                  │
│       │  │ThemeManager          │  │                  │
│       │  │UserManager           │  │                  │
│       │  └──────────┬───────────┘  │                  │
│       └─────────────┼──────────────┘                  │
│                     │                                  │
│       ┌─────────────▼──────────────┐                  │
│       │      Repository Layer       │                  │
│       │  ┌──────────────────────┐  │                  │
│       │  │ProjectRepository     │  │                  │
│       │  └──────────┬───────────┘  │                  │
│       └─────────────┼──────────────┘                  │
└─────────────────────┼──────────────────────────────────┘
                      │
┌─────────────────────┼──────────────────────────────────┐
│                     │      Data Layer                   │
│       ┌─────────────▼──────────────┐                  │
│       │   SharedPreferences        │                  │
│       │  ┌──────────────────────┐  │                  │
│       │  │focusleaf_prefs       │  │                  │
│       │  │projects_prefs        │  │                  │
│       │  └──────────────────────┘  │                  │
│       └────────────────────────────┘                  │
└─────────────────────────────────────────────────────────┘
```

### Поток данных

```
User Action
    ↓
Activity (ViewBinding)
    ↓
Manager/Utils (Business Logic)
    ↓
Repository (Data Access)
    ↓
SharedPreferences (Storage)
    ↓
StateFlow (Reactive Updates)
    ↓
Activity (UI Update)
```

### Компоненты архитектуры

#### 1. **Presentation Layer (Activities)**
- Все экраны приложения представлены как Activities
- Используется **ViewBinding** для доступа к view элементам
- Базовый класс `BaseActivity` для общих функций
- Каждая Activity отвечает за один экран

#### 2. **Business Logic Layer (Managers)**
- **PreferencesManager** — управление SharedPreferences (онбординг, авторизация)
- **StatisticsManager** — расчет статистики и аналитики
- **AnalyticsManager** — управление аналитикой
- **ThemeManager** — управление темами приложения
- **UserManager** — управление данными пользователя
- **TimerSettingsManager** — настройки таймера Pomodoro
- **NotificationSettingsManager** — настройки уведомлений

#### 3. **Repository Layer**
- **ProjectRepository** — управление проектами с реактивными обновлениями через StateFlow
- Хранение данных в SharedPreferences с JSON сериализацией (Gson)

#### 4. **Data Layer**
- **SharedPreferences** для локального хранения
- Структура данных:
  - `focusleaf_prefs` — пользовательские настройки, авторизация
  - `projects_prefs` — список проектов (JSON)

### Особенности реализации

- ✅ **Реактивность** — использование StateFlow для обновления UI
- ✅ **Разделение ответственности** — четкое разделение по слоям
- ✅ **Расширяемость** — легко добавлять новые экраны и функции
- ✅ **Локализация** — поддержка нескольких языков
- ✅ **Material Design** — современный UI с Material Components

---

## 🗄️ Схема базы данных

Приложение использует **SharedPreferences** для локального хранения данных. В будущем планируется миграция на Room Database.

### Структура хранения

#### 1. **focusleaf_prefs** (PreferencesManager)

```kotlin
SharedPreferences: "focusleaf_prefs"

┌──────────────────────────────────────────────┐
│ Key                           │ Type   │ Description        │
├──────────────────────────────────────────────┤
│ onboarding_completed          │ Boolean│ Статус онбординга  │
│ user_authorized               │ Boolean│ Авторизация        │
│ user_email                    │ String │ Email пользователя │
└──────────────────────────────────────────────┘
```

**Пример данных:**
```json
{
  "onboarding_completed": true,
  "user_authorized": true,
  "user_email": "user@example.com"
}
```

#### 2. **projects_prefs** (ProjectRepository)

```kotlin
SharedPreferences: "projects_prefs"

┌──────────────────────────────────────────────┐
│ Key                           │ Type   │ Description        │
├──────────────────────────────────────────────┤
│ projects                      │ String │ JSON массив        │
└──────────────────────────────────────────────┘
```

**Модель Project:**
```kotlin
data class Project(
    val id: String,                    // Уникальный ID проекта
    val name: String,                  // Название проекта
    val color: Int,                    // Цвет проекта (HEX)
    val priority: String,              // Приоритет: "Низкий", "Средний", "Высокий"
    val estimatedTime: Int,            // Оценочное время (минуты)
    val taskCount: Int,                // Количество задач
    val projectTotalTime: Int,         // Общее время проекта
    val createdAt: Long,               // Время создания
    val isCompleted: Boolean,          // Статус выполнения
    val estimatedTomatoes: Int,        // Оценка помидоров
    val completedSessions: Int,        // Завершенные сессии
    val totalFocusTime: Int            // Общее время фокуса
)
```

**Пример JSON:**
```json
[
  {
    "id": "1",
    "name": "Медитация",
    "color": -1245200,
    "priority": "Средний",
    "estimatedTime": 25,
    "taskCount": 1,
    "projectTotalTime": 25,
    "createdAt": 1234567890,
    "isCompleted": false,
    "estimatedTomatoes": 2,
    "completedSessions": 0,
    "totalFocusTime": 0
  }
]
```

### Диаграмма связей

```
┌─────────────────────────────────────────────┐
│         SharedPreferences                    │
├─────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────┐  ┌─────────────────┐ │
│  │ focusleaf_prefs  │  │ projects_prefs  │ │
│  │                  │  │                 │ │
│  │ • onboarding     │  │ • projects[]    │ │
│  │ • authorization  │  │   (JSON)        │ │
│  │ • user_email     │  │                 │ │
│  └──────────────────┘  └─────────────────┘ │
│                                              │
└─────────────────────────────────────────────┘
         │                      │
         │                      │
         ▼                      ▼
┌──────────────────┐  ┌──────────────────┐
│PreferencesManager│  │ProjectRepository │
│                  │  │                  │
│ • User State     │  │ • Project CRUD   │
│ • Onboarding     │  │ • StateFlow      │
└──────────────────┘  └──────────────────┘
```

### Управление данными

#### Сохранение проектов

```kotlin
// Сериализация списка проектов в JSON
val json = gson.toJson(projects)
sharedPreferences.edit()
    .putString(KEY_PROJECTS, json)
    .commit()
```

#### Загрузка проектов

```kotlin
// Десериализация JSON в список проектов
val json = sharedPreferences.getString(KEY_PROJECTS, "[]") ?: "[]"
val projects = gson.fromJson<List<Project>>(json, projectType)
```

### Реактивные обновления

Использование **StateFlow** для автоматического обновления UI при изменении данных:

```kotlin
// Репозиторий
private val _projects = MutableStateFlow<List<Project>>(emptyList())
val projects: StateFlow<List<Project>> = _projects.asStateFlow()

// В Activity
lifecycleScope.launch {
    ProjectRepository.projects.collect { projects ->
        // Обновление UI при изменении проектов
        adapter.updateProjects(projects)
    }
}
```

---

## 🔧 Основные зависимости

```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.10.0")

// UI Components
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.viewpager2:viewpager2:1.0.0")

// Lifecycle & Coroutines
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

// JSON Serialization
implementation("com.google.code.gson:gson:2.10.1")

// Local Broadcast
implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
```

---

## 📱 Основные экраны

### Онбординг и авторизация
- **OnboardingActivity** — 4 экрана знакомства с приложением
- **LoginActivity** — вход и регистрация

### Основные функции
- **MainActivity** — главный экран с таймером Pomodoro
- **ProjectsActivity** — управление проектами
- **CreateProjectActivity** — создание нового проекта
- **StatisticsActivity** — статистика пользователя

### Аналитика
- **TodayAnalyticsActivity** — аналитика за сегодня
- **WeekAnalyticsActivity** — аналитика за неделю
- **ProjectAnalyticsActivity** — аналитика по проектам

### Социальные функции
- **FriendsSearchActivity** — поиск друзей
- **GroupSessionActivity** — совместные сессии
- **ChatActivity** — чат поддержки

### Настройки
- **ProfileActivity** — личный кабинет
- **TimerSettingsActivity** — настройки таймера
- **NotificationSettingsActivity** — настройки уведомлений
- **ThemeSettingsActivity** — настройки темы

---

## 🚦 Навигация

```
OnboardingActivity (LAUNCHER)
    ↓
LoginActivity
    ↓
MainActivity
    ├── ProjectsActivity
    │   ├── CreateProjectActivity
    │   └── ProjectAnalyticsActivity
    ├── StatisticsActivity
    ├── ProfileActivity
    │   ├── ThemeSettingsActivity
    │   ├── TimerSettingsActivity
    │   └── NotificationSettingsActivity
    ├── FriendsSearchActivity
    │   └── GroupSessionActivity
    └── ChatActivity
```

---

## 🎨 UI/UX особенности

- **Material Design 3** — современный дизайн
- **Адаптивный layout** — поддержка различных размеров экранов
- **Темная и светлая тема** — автоматическое переключение
- **Анимации** — плавные переходы между экранами
- **Круглый таймер** — визуальное отображение прогресса Pomodoro
- **FAB меню** — быстрое меню с плавающими кнопками

---

## 🔐 Безопасность

- Данные хранятся локально на устройстве
- SharedPreferences используется в режиме `MODE_PRIVATE`
- Авторизация реализована через PreferencesManager
- В будущем планируется интеграция с Firebase Authentication

---

## 📝 Лицензия

Этот проект создан в образовательных целях.

---

## 👥 Авторы

- **Разработчик** — [Ваше имя]
- **Дизайн** — FocusLeaf Team

---

## 🔮 Планы на будущее

- [ ] Миграция на Room Database
- [ ] Интеграция с Firebase (Authentication, Firestore, Storage)
- [ ] Синхронизация данных между устройствами
- [ ] Push-уведомления
- [ ] Экспорт статистики
- [ ] Расширенная аналитика
- [ ] Виджеты для главного экрана

---

## 📞 Контакты

Если у вас есть вопросы или предложения, пожалуйста, создайте Issue в репозитории.

---

<div align="center">

**Сделано с ❤️ для повышения продуктивности**

</div>
