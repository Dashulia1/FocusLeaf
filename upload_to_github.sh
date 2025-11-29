#!/bin/bash

# Скрипт для загрузки проекта FocusLeaf2 на GitHub
# Использование: ./upload_to_github.sh

set -e  # Остановить выполнение при ошибке

echo "🚀 Загрузка FocusLeaf2 на GitHub"
echo "=================================="
echo ""

# Проверка наличия Git
if ! command -v git &> /dev/null; then
    echo "❌ Git не установлен. Установите Git и повторите попытку."
    exit 1
fi

cd "$(dirname "$0")"

# Инициализация Git (если еще не инициализирован)
if [ ! -d ".git" ]; then
    echo "📦 Инициализация Git репозитория..."
    git init
    echo "✅ Git репозиторий инициализирован"
else
    echo "ℹ️  Git репозиторий уже инициализирован"
fi

# Проверка конфигурации Git
echo ""
echo "🔍 Проверка конфигурации Git..."
if [ -z "$(git config user.name)" ] || [ -z "$(git config user.email)" ]; then
    echo "⚠️  Git пользователь не настроен!"
    echo ""
    read -p "Введите ваше имя для Git: " git_name
    read -p "Введите ваш email для Git: " git_email
    git config user.name "$git_name"
    git config user.email "$git_email"
    echo "✅ Git пользователь настроен"
fi

# Добавление файлов
echo ""
echo "📝 Добавление файлов в репозиторий..."
git add .

# Показ статуса
echo ""
echo "📊 Статус файлов:"
git status --short | head -30

# Проверка, есть ли уже коммиты
if git rev-parse --verify HEAD >/dev/null 2>&1; then
    echo ""
    echo "ℹ️  В репозитории уже есть коммиты"
    read -p "Создать новый коммит? (y/n): " create_commit
    if [ "$create_commit" != "y" ]; then
        echo "Операция отменена"
        exit 0
    fi
    read -p "Введите сообщение коммита: " commit_message
else
    commit_message="Initial commit: FocusLeaf 2.0 - Pomodoro productivity app"
fi

# Создание коммита
echo ""
echo "💾 Создание коммита..."
git commit -m "$commit_message"
echo "✅ Коммит создан"

# Переименование ветки на main
echo ""
echo "🌿 Настройка ветки main..."
git branch -M main 2>/dev/null || true

# Проверка наличия remote
echo ""
if git remote | grep -q "^origin$"; then
    echo "ℹ️  Remote 'origin' уже настроен:"
    git remote -v
    echo ""
    read -p "Использовать существующий remote? (y/n): " use_existing
    if [ "$use_existing" != "y" ]; then
        git remote remove origin
        use_existing="n"
    fi
else
    use_existing="n"
fi

# Настройка remote
if [ "$use_existing" != "y" ]; then
    echo ""
    echo "🔗 Настройка подключения к GitHub..."
    echo "Вам нужно создать репозиторий на GitHub.com, если еще не создали."
    echo ""
    read -p "Введите ваш GitHub username: " github_username
    read -p "Введите имя репозитория (по умолчанию: FocusLeaf2): " repo_name
    repo_name=${repo_name:-FocusLeaf2}
    
    read -p "Использовать SSH? (y/n, по умолчанию: n): " use_ssh
    if [ "$use_ssh" == "y" ]; then
        remote_url="git@github.com:$github_username/$repo_name.git"
    else
        remote_url="https://github.com/$github_username/$repo_name.git"
    fi
    
    git remote add origin "$remote_url"
    echo "✅ Remote настроен: $remote_url"
fi

# Загрузка на GitHub
echo ""
echo "📤 Загрузка на GitHub..."
echo "Если потребуется авторизация:"
echo "  - Для HTTPS: используйте Personal Access Token (не пароль)"
echo "  - Для SSH: убедитесь, что SSH ключи настроены"
echo ""
read -p "Загрузить код на GitHub? (y/n): " push_code
if [ "$push_code" == "y" ]; then
    git push -u origin main
    echo ""
    echo "✅ Код успешно загружен на GitHub!"
    echo "🌐 Откройте: https://github.com/$github_username/$repo_name"
else
    echo "Код не загружен. Выполните вручную:"
    echo "  git push -u origin main"
fi

echo ""
echo "🎉 Готово!"

