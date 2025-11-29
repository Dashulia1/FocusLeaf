#!/bin/bash

# Скрипт для копирования изображений онбординга
SOURCE_DIR="/Users/dasasergeeva/Desktop/фокус"
TARGET_DIR="/Users/dasasergeeva/AndroidStudioProjects/FocusLeaf2/app/src/main/res/drawable"

echo "Копирование изображений онбординга..."

# Копируем файлы
cp "$SOURCE_DIR/Picture1.png" "$TARGET_DIR/onboarding_0_character.png" && echo "✓ onboarding_0_character.png скопирован"
cp "$SOURCE_DIR/933c4225c6421b9d211549dedb77339b-2.jpg" "$TARGET_DIR/onboarding_1_organization.jpg" && echo "✓ onboarding_1_organization.jpg скопирован"
cp "$SOURCE_DIR/aa55daee1ce15478cf6abd1473133b3f.jpg" "$TARGET_DIR/onboarding_2_analytics.jpg" && echo "✓ onboarding_2_analytics.jpg скопирован"
cp "$SOURCE_DIR/64c18f6027fbb55d92e1c6c41bb1784c.jpg" "$TARGET_DIR/onboarding_3_success.jpg" && echo "✓ onboarding_3_success.jpg скопирован"

echo ""
echo "Проверка скопированных файлов:"
ls -lh "$TARGET_DIR"/onboarding_*.{png,jpg} 2>/dev/null || echo "Файлы не найдены"

