# ⚡ Быстрый старт: Загрузка на GitHub

## Автоматическая настройка

Выполните скрипт:

```bash
cd /Users/dasasergeeva/AndroidStudioProjects/FocusLeaf2
./setup_git.sh
```

## Ручная настройка

### 1. Инициализация Git

```bash
cd /Users/dasasergeeva/AndroidStudioProjects/FocusLeaf2
git init
```

### 2. Первый коммит

```bash
git add .
git commit -m "Initial commit: FocusLeaf 2.0 - Pomodoro productivity app"
```

### 3. Создайте репозиторий на GitHub

1. Откройте https://github.com/new
2. Имя репозитория: `FocusLeaf2`
3. Описание: "Приложение для повышения продуктивности на основе техники Pomodoro"
4. Выберите Public или Private
5. **НЕ** добавляйте README, .gitignore или license
6. Нажмите "Create repository"

### 4. Подключите и загрузите

```bash
# Замените YOUR_USERNAME на ваш GitHub username
git remote add origin https://github.com/YOUR_USERNAME/FocusLeaf2.git
git branch -M main
git push -u origin main
```

## ⚠️ Важно

Если используете HTTPS, может потребоваться Personal Access Token вместо пароля:
- GitHub → Settings → Developer settings → Personal access tokens → Generate new token
- Выберите право: `repo`
- Используйте токен как пароль при push

---

**Готово!** 🎉 После выполнения всех шагов ваш проект будет на GitHub!

