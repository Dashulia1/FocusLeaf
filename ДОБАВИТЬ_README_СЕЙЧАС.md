# ⚡ Добавить README на GitHub - Прямо сейчас!

## 🎯 Быстрый способ (3 шага)

### Шаг 1: Создайте репозиторий на GitHub

1. Откройте: https://github.com/new
2. **Repository name**: `FocusLeaf2`
3. **Description**: "Приложение для повышения продуктивности на основе техники Pomodoro"
4. Выберите **Public** или **Private**
5. ❌ **НЕ** добавляйте README, .gitignore, license (они уже есть!)
6. Нажмите **"Create repository"**

### Шаг 2: Выполните команды в терминале

Откройте терминал и скопируйте/вставьте все команды сразу:

```bash
cd /Users/dasasergeeva/AndroidStudioProjects/FocusLeaf2

git init
git add README.md
git add .
git commit -m "Initial commit: FocusLeaf 2.0 - Add README.md and project documentation"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/FocusLeaf2.git
git push -u origin main
```

**⚠️ Важно:** Замените `YOUR_USERNAME` на ваш реальный GitHub username!

### Шаг 3: Авторизация

Когда запросит пароль:
- **Username**: ваш GitHub username
- **Password**: используйте **Personal Access Token** (не обычный пароль!)

#### Как получить токен:
1. GitHub.com → Settings (правый верхний угол)
2. Developer settings → Personal access tokens → Tokens (classic)
3. Generate new token (classic)
4. Название: `FocusLeaf2`
5. Выберите: `repo` ✅
6. Generate token
7. **Скопируйте токен** и используйте как пароль

---

## ✅ Готово!

После успешной загрузки откройте:
```
https://github.com/YOUR_USERNAME/FocusLeaf2
```

README.md будет отображаться на главной странице! 🎉

---

## 🔧 Если что-то пошло не так:

### Если remote уже существует:
```bash
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/FocusLeaf2.git
```

### Если нужно только обновить README:
```bash
cd /Users/dasasergeeva/AndroidStudioProjects/FocusLeaf2
git add README.md
git commit -m "docs: Update README.md"
git push origin main
```

### Проверить статус:
```bash
git status
git remote -v
```

---

**Все готово! Выполните команды и README появится на GitHub!** 🚀

