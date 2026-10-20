# 📬 Notification Hub Service

Микросервис централизованной доставки уведомлений. Обрабатывает входящие события из Apache Kafka и внешние REST-запросы, сохраняет задачи в базу данных и надежно доставляет сообщения получателям через Telegram Bot API и SMTP Email с поддержкой retry-политики и конкурентной обработки.

---

## 🚀 Основной функционал

- **Прием алертов из Kafka:** Автоматический консьюминг событий из топика `url_alerts`.
- **Паттерн Database Queue / Outbox:**
  - Задачи сохраняются в статусе `PENDING`.
  - Фоновый планировщик (`NotificationWorker`) батчами забирает задачи с использованием **пессимистических блокировок (`SKIP LOCKED`)**, предотвращая состояние гонки между несколькими инстансами.
- **Паттерн «Стратегия» (Strategy Pattern):**
  - Легко расширяемая архитектура каналов связи (`ChannelSender`).
  - Реализованы провайдеры: **Telegram** (`TelegramChannelSender` через `RestClient`) и **Email** (`EmailChannelSender` через `JavaMailSender`).
- **Отказоустойчивость и Retry:**
  - Подсчет количества попыток (`retry_count`).
  - Автоматический возврат в очередь при временных сетевых ошибках и перевод в статус `FAILED` при превышении лимита (3 попытки).
  - Воркер восстановления зависших задач (`recoverStuckNotifications`), возвращающий упавшие процессы из `PROGRESSING` обратно в работу.
- **Интеграционное тестирование:** Тестирование репозиториев на реальной базе данных с использованием **Testcontainers**.

---

## 🛠 Технологический стек

- **Язык программирования:** Java 21
- **Фреймворк:** Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Kafka, Spring Mail, Async Execution)
- **База данных:** PostgreSQL 15
- **Миграции БД:** Flyway (V1, V2)
- **Брокер сообщений:** Apache Kafka
- **Внешние API:** Telegram Bot API, Yandex SMTP
- **Тестирование:** JUnit 5, Mockito, Testcontainers (PostgreSQL)
- **Сборка и развертывание:** Maven, Docker, Docker Compose

---

## 🏛 Архитектура обработки уведомлений

```
+--------------------+        +-------------------------+
|    Apache Kafka    |        |       REST Client       |
| (topic: url_alerts)|        | (CreateNotificationReq) |
+---------+----------+        +------------+------------+
          |                                |
          v                                v
+--------------------+        +-------------------------+
|AlertConsumerService|        | NotificationController  |
+---------+----------+        +------------+------------+
          |                                |
          +---------------+----------------+
                          |
                          v
              +-----------------------+
              |  NotificationService  |
              +-----------+-----------+
                          |
                          v (Save with status PENDING)
              +-----------------------+
              |      PostgreSQL       |
              +-----------+-----------+
                          |
                          | (findTop10... FOR UPDATE SKIP LOCKED)
                          v
              +-----------------------+
              |  NotificationWorker   |
              +-----------+-----------+
                          |
                          v (@Async Pool)
              +-----------------------+
              |  NotificationSender   |
              +-----------+-----------+
                          |
        +-----------------+-----------------+
        | (ChannelSender)                   | (ChannelSender)
        v                                   v
+-----------------------+           +-----------------------+
| TelegramChannelSender |           |   EmailChannelSender  |
|  (Telegram Bot API)   |           |    (SMTP MailSender)  |
+-----------------------+           +-----------------------+
```

---

## ⚙️ Переменные окружения

| Переменная | Описание | Значение по умолчанию |
|---|---|---|
| `DB_HOST` | Хост PostgreSQL | `localhost` |
| `DB_PORT` | Порт PostgreSQL | `5432` |
| `DB_NAME` | Имя базы данных | `notificationdb` |
| `DB_USER` | Пользователь БД | `postgres` |
| `DB_PASSWORD` | Пароль БД | `password` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Адрес кластера Kafka | `localhost:9092` |
| `TELEGRAM_BOT_TOKEN` | Токен Telegram-бота | — *(обязательно)* |
| `MAIL_USERNAME` | Email отправителя | — |
| `MAIL_PASSWORD` | Пароль приложения для SMTP | — |

---

## 🔌 REST API Endpoints

### 1. Создать уведомление вручную
`POST /api/v1/notifications`

**Тело запроса (JSON):**
```json
{
  "recipient": "123456789",
  "message": "Тестовое оповещение о статусе системы",
  "channel": "TELEGRAM"
}
```

### 2. Получить статус уведомления по ID
`GET /api/v1/notifications/{id}`

### 3. Список уведомлений (с пагинацией)
`GET /api/v1/notifications?page=0&size=10`

---

## 🧪 Запуск тестов

Для выполнения интеграционных тестов с поднимаемым в Docker контейнером PostgreSQL:

```bash
mvn clean test
```

---

## 🐳 Запуск сервиса

```bash
docker compose up -d --build
```
Сервис будет доступен по адресу: `http://localhost:8080`