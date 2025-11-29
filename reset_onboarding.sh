#!/bin/bash

# Скрипт для сброса онбординга в приложении FocusLeaf2
# Использование: ./reset_onboarding.sh

echo "Сброс данных приложения FocusLeaf2..."
adb shell pm clear com.dasasergeeva.focusleaf2

if [ $? -eq 0 ]; then
    echo "✓ Данные приложения успешно очищены!"
    echo "Теперь при запуске приложения вы увидите онбординг."
else
    echo "✗ Ошибка: Убедитесь, что:"
    echo "  1. Устройство подключено к компьютеру"
    echo "  2. Включена отладка по USB"
    echo "  3. ADB установлен и доступен в PATH"
fi

