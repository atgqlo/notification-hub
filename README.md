# 🔔 Notification Hub

Асинхронный сервис гарантированной доставки уведомлений (Email, Telegram, SMS), построенный на **Java 21** и **Spring Boot**. 

Проект спроектирован с упором на отказоустойчивость, безопасную конкурентную работу с базой данных (Transactional Outbox / DB Queue pattern) и соблюдение принципов **SOLID**.

---

## 🚀 Ключевые архитектурные решения

В отличие от простых CRUD-приложений, в проекте решены реальные проблемы продакшен-систем:

* **Защита от состояния гонки (Race Condition):** Выборка задач из очереди реализована через пессимистичную блокировку PostgreSQL (`SELECT ... FOR UPDATE SKIP LOCKED` с помощью `@Lock(LockModeType.PESSIMISTIC_WRITE)`). Это позволяет запускать несколько инстансов сервиса параллельно без риска отправить одно уведомление дважды.
* **Разделение `@Transactional` и `@Async`:** Захват задач и перевод их в статус `PROGRESSING` происходят в короткой пакетной транзакции (`saveAll`), после чего соединение с БД мгновенно возвращается в пул HikariCP. Долгие сетевые вызовы (SMTP / HTTP API) выполняются вне транзакции в выделенном пуле потоков `ThreadPoolTaskExecutor`.
* **Паттерн «Стратегия» (Strategy Pattern & OCP):** Маршрутизация по каналам связи (`EMAIL`, `TELEGRAM`, `SMS`) построена через интерфейс `ChannelSender`. Spring автоматически собирает все реализации в `Map<NotificationChannel, ChannelSender>`, что позволяет добавлять новые каналы доставки без изменения существующего кода.
* **Отказоустойчивость (Retry & Self-Healing):** 
  * При сбое внешней сети задача не теряется: инкрементируется счетчик `retry_count`, и уведомление возвращается в очередь (до 3 попыток, после чего переходит в статус `FAILED`).
  * Отдельный фоновый воркер отслеживает «зависшие» задачи (оставшиеся в статусе `PROGRESSING` дольше 5 минут из-за внезапного падения контейнера) и возвращает их обратно в `PENDING`.
* **Чистая архитектура API:** Разделение доменных сущностей (`Entity`) и транспортного слоя (`DTO` на базе Java `record`), валидация входящих данных (`Jakarta Validation`) и централизованная обработка ошибок через `@RestControllerAdvice`.

---

## 🛠 Технологический стек

* **Язык и платформа:** Java 21
* **Фреймворк:** Spring Boot (Web, Data JPA / Hibernate, Mail, Validation)
* **База данных:** PostgreSQL 15 + **Flyway** (миграции схемы БД)
* **Многопоточность:** Spring `@Scheduled`, `@Async` (кастомный `ThreadPoolTaskExecutor`)
* **Тестирование:** JUnit 5, Mockito, **Testcontainers** (интеграционные тесты с реальным контейнером PostgreSQL)
* **Инфраструктура:** Docker, Docker Compose (Multi-stage build), Maven, Lombok

---

## 🔄 Жизненный цикл уведомления

```text
[Клиент] ──(POST)──> [PENDING] ──(Захват воркером + SKIP LOCKED)──> [PROGRESSING]
                        ▲                                                 │
                        │ (Ошибка сети: retry_count < 3                   ├──(Успех)──> [SENT]
                        │  или сброс зависшей задачи через 5 мин)         │
                        └─────────────────────────────────────────────────┤
                                                                          └──(retry_count >= 3)──> [FAILED]
```

---

## ⚙️ Быстрый запуск (Docker Compose)

Проект полностью контейнеризирован и собирается через многоступенчатый `Dockerfile` (Maven + Eclipse Temurin 21 JRE).

### 1. Клонировать репозиторий
```bash
git clone https://github.com/ТВОЙ_ЛОГИН/notification-hub.git
cd notification-hub
```

### 2. Настроить переменные окружения
Создайте файл `.env` в корне проекта (или проверьте настройки в `docker-compose.yml`):
```env
DB_NAME=mydbname
DB_USER=postgres
DB_PASSWORD=postgres
DB_PORT=5432

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

### 3. Запустить контейнеры
```bash
docker-compose up --build
```
При старте **Flyway** автоматически накатит миграции (`V1`, `V2`), после чего приложение будет доступно на порту `8080`.

---

## 📡 Примеры API запросов

### Создание уведомления
**Запрос:**
```http
POST http://localhost:8080/api/v1/notifications
Content-Type: application/json

{
  "recipient": "user@example.com",
  "message": "Ваш заказ #42 успешно оформлен!",
  "channel": "EMAIL"
}
```
*(Доступные каналы: `EMAIL`, `TELEGRAM`)*

**Ответ (`201 Created`):**
```json
{
  "id": 1,
  "recipient": "user@example.com",
  "message": "Ваш заказ #42 успешно оформлен!",
  "channel": "EMAIL",
  "status": "PENDING",
  "createdAt": "2026-09-27T12:00:00.123456",
  "updatedAt": "2026-09-27T12:00:00.123456",
  "retryCount": 0
}
```

---
```bash
mvn test
```
