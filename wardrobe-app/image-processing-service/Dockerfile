FROM python:3.11-slim

WORKDIR /app

# Установка системных библиотек, необходимых для OpenCV
RUN apt-get update && apt-get install -y libgl1 libglib2.0-0 && rm -rf /var/lib/apt/lists/*

# Копируем requirements и устанавливаем
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копируем код приложения
COPY . .

# Создаем папку для кеша моделей rembg
RUN mkdir -p /root/.u2net

EXPOSE 8000

# Запуск uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]