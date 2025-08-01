# URL Shortener Service (Pet)


## Описание

Сервис для сокращения длинных URL-адресов с возможностью:

- Генерации случайного короткого ключа (по умолчанию 5 символов из
строчных и заглавных латинских букв)
- Создания кастомного (пользовательского) ключа
- Статистики по использованию созданных ссылок
- Редиректа по созданным ссылкам

## Технологии

- Java
- Maven
- Spring Boot
- Spring Data JPA
- H2
- Lombok

## Функциональность приложения

### Запуск приложения

```shell
mvn spring-boot:run
```
Предполагается, что приложение запущено на `localhost:8080`

### Создание короткой ссылки

```commandline
curl -X POST -H "Content-Type: text/plain" -d "https://www.google.com" http://localhost:8080/shortened_url
```

В ответ придет сгенерированная ссылка:

```commandline
http://localhost:8080/AbCdE
```

### Создание кастомной ссылки

```commandline
curl -X POST http://localhost:8080/shortened_url/custom -H "Content-Type: application/json" -d "{\"longUrl\":\"https://www.google.com\",\"customKey\":\"myCustom\"}"
```

В ответ придет сгенерированная ссылка:

```commandline
http://localhost:8080/myCustom
```

### Переход по использованной ссылке

Можно перейти по ссылке в браузере или использовать консоль. Например:

```commandline
curl http://localhost:8080/AbCdE
```

### Просмотр статистики созданной ссылки

```commandline
curl http://localhost:8080/stats/AbCdE
```

Ответ:

```json
{
  "usages": 5,
  "creationDate": "2025-07-30T17:22:26.334+00:00"
}
```

## Логика работы

### Создание сокращенной ссылки

1) Проверяется валидность исходного url
2) Проверяется существование короткой ссылки для исходного url
3) 1) Для случайного ключа генерируется строка из A-Za-z длины 5
2) Для кастомного ключа проверяется его отсутствие в БД
4) Редирект сохраняется в БД с нулевым счетчиком использований

### Использование сокращенной ссылки

1) По короткому ключу находится запись в БД
2) Проверяется, не истек ли срок жизни ссылки
3) Увеличивается счетчик использований
4) Данные перезаписываются в БД
5) Выполняется редирект на исходный url

### Просмотр статистики

1) По короткому ключу находится запись в БД
2) Возвращается количество использований и дата создания укороченной ссылки

## Архитектура приложения

### Модель

Каждый переход (redirect) хранит в себе следующие данные:

- Исходный url (`longUrl`)
- Сокращенный url (`shortenedUrl`)
- Ключ сокращенного url (`shortKey`)
- Дата создания (`creationDate`)
- Количество использований (`usages`)

Статистика созданных ссылок состоит из:

- Количество использований (`usages`)
- Дата создания (`creationDate`)

### База данных

База данных имеет следующую структуру:

```
CREATE TABLE redirections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shortened_url VARCHAR(255) UNIQUE,
    long_url VARCHAR(255),
    short_key VARCHAR(255) UNIQUE,
    creation_date TIMESTAMP,
    usages BIGINT
);
```

### Контроллеры

- `CreateShortenedUrlController`
- - `/shortened_url` - создание короткой ссылки для переданного url
- - `/shortened_url/custom` - создание кастомной короткой ссылки для переданного url и кастомного ключа
- `StatisticController`
- - `/stats/{shortKey}` - просмотр статистики для короткой ссылки с ключом `shortKey`
- `UseShortUrlController`
- - `/{shortKey}` - редирект по короткой ссылке с ключом `shortKey`

### Сервис

- `UrlShortenerService` - бизнес-логика (генерация ключей, валидация URL, работа с БД)

### Репозиторий

- `UrlShortenerRepository` - взаимодействие с базой данных через Spring Data JPA

### Обработка ошибок

Сервис возвращает следующие HTTP-статусы при ошибках:

- `400 Bad Request` - при попытке создать уже существующий кастомный ключ
- `404 Not Found` - если ссылка не найдена или истек срок ее действия
- `400 Bad Request` (с сообщением "invalid url") - если передан некорректный URL

### Настройки (`application.properties`)

- `domainPart` - базовый домен для сокращенных ссылок (по умолчанию: `http://localhost:8080/`)
- `shortKeySize` - длина случайного ключа (по умолчанию: 5)
- `maxLiveTimeSeconds` - время жизни ссылки в секундах (по умолчанию: 86400 - 1 день)



## Возможные доработки/улучшения

1) Решение проблемы одинакого сгенерированного рандомного ключа
2) Улучшение валидации ссылок (ключей)
3) Написание тестов
4) Дальнейшая реализация с PostgreSQL
5) Реализация проекта в докере















