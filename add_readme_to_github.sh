#!/bin/bash

# Скрипт для добавления README.md и других файлов в GitHub репозиторий

echo "🚀 Добавление README.md в GitHub репозиторий"
echo "============================================="
echo ""

cd "$(dirname "$0")"

# Проверка наличия Git
if ! command -v git &> /dev/null; then
    echo "❌ Git не установлен. Установите Git и повторите попытку."
    exit 1
fi

# Инициализация Git (если еще не инициализирован)
if [ ! -d ".git" ]; then
    echo "📦 Инициализация Git репозитория..."
    git init
    echo "✅ Git репозиторий инициализирован"
else
    echo "ℹ️  Git репозиторий уже инициализирован"
fi

# Проверка конфигурации Git
if [ -z "$(git config user.name)" ] || [ -z "$(git config user.email)" ]; then
    echo ""
    echo "⚠️  Git пользователь не настроен!"
    echo "Настройка Git пользователя..."
    read -p "Введите ваше имя для Git: " git_name
    read -p "Введите ваш email для Git: " git_email
    git config user.name "$git_name"
    git config user.email "$git_email"
    echo "✅ Git пользователь настроен: $git_name <$git_email>"
fi

# Проверка наличия README.md
if [ ! -f "README.md" ]; then
    echo "❌ Файл README.md не найден!"
    exit 1
fi

echo ""
echo "📄 Файл README.md найден:"
ls -lh README.md

# Добавление README.md и других файлов
echo ""
echo "📝 Добавление файлов в репозиторий..."
git add README.md

# Добавляем другие важные файлы
git add .gitignore 2>/dev/null || true
git add GITHUB_SETUP.md 2>/dev/null || true
git add QUICK_START_GITHUB.md 2>/dev/null || true
git add НАЧНИ_ЗДЕСЬ.md 2>/dev/null || true

# Показываем, что будет добавлено
echo ""
echo "📊 Статус README.md:"
git status README.md

# Проверка, есть ли изменения для коммита
if git diff --cached --quiet && [ -z "$(git status --porcelain)" ]; then
    echo ""
    echo "ℹ️  Нет изменений для коммита. Все файлы уже закоммичены."
    
    # Проверяем, есть ли remote
    if git remote | grep -q "^origin$"; then
        echo ""
        echo "🔗 Remote репозиторий настроен:"
        git remote -v
        echo ""
        read -p "Загрузить README.md на GitHub? (y/n): " push_code
        if [ "$push_code" == "y" ]; then
            git push origin main 2>/dev/null || git push origin master 2>/dev/null || echo "Используйте: git push -u origin main"
        fi
    else
        echo ""
        echo "⚠️  Remote репозиторий не настроен."
        echo "Сначала настройте remote:"
        echo "  git remote add origin https://github.com/USERNAME/FocusLeaf2.git"
    fi
    exit 0
fi

# Создание коммита
echo ""
echo "💾 Создание коммита с README.md..."

# Проверка, есть ли уже коммиты
if git rev-parse --verify HEAD >/dev/null 2>&1; then
    commit_message="docs: Add/Update README.md with project documentation"
    echo "ℹ️  Обновление существующего репозитория"
else
    commit_message="Initial commit: FocusLeaf 2.0 - Add README.md and project documentation"
    echo "ℹ️  Первый коммит в репозитории"
fi

git commit -m "$commit_message"
echo "✅ Коммит создан: $commit_message"

# Переименование ветки на main (если нужно)
echo ""
echo "🌿 Настройка ветки main..."
git branch -M main 2>/dev/null || true

# Проверка наличия remote
echo ""
if git remote | grep -q "^origin$"; then
    echo "✅ Remote 'origin' уже настроен:"
    git remote -v
    echo ""
    read -p "Загрузить README.md на GitHub? (y/n): " push_code
    if [ "$push_code" == "y" ]; then
        echo "📤 Загрузка на GitHub..."
        git push -u origin main 2>/dev/null || git push -u origin master 2>/dev/null || {
            echo "⚠️  Не удалось автоматически определить ветку. Выполните:"
            echo "  git push -u origin main"
        }
        echo "✅ README.md успешно загружен на GitHub!"
    else
        echo "Код не загружен. Выполните вручную:"
        echo "  git push -u origin main"
    fi
else
    echo "⚠️  Remote репозиторий не настроен."
    echo ""
    echo "Для настройки remote выполните:"
    echo "  git remote add origin https://github.com/YOUR_USERNAME/FocusLeaf2.git"
    echo ""
    echo "Затем загрузите код:"
    echo "  git push -u origin main"
fi

echo ""
echo "🎉 Готово! README.md готов для GitHub!"

