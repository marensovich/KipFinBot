# KipFinBot

Discord-бот для студентов **КИПФИН колледжа**. Подключается к электронному журналу Eljur и выдаёт оценки с расписанием прямо в Discord — без входа на сайт журнала.

## Что умеет

- Посмотреть оценки за нужный период
- Узнать расписание на день или неделю
- Групповые команды — общая информация для всей группы
- Личные команды — только ваши данные

## Быстрый старт

```bash
git clone https://github.com/marensovich/KipFinBot.git
cd KipFinBot
```

Создайте файл конфигурации (или переменные окружения):

```properties
discord.token=ВАШ_ТОКЕН_БОТА
eljur.token=ТОКЕН_ELJUR
eljur.school=НАЗВАНИЕ_ШКОЛЫ
```

```bash
mvn package
java -jar target/KipFinBot.jar
```

## Стек

- **Java** + **Maven**
- **JDA** (Java Discord API) — Discord-интеграция
- **Eljur API** — получение данных из электронного журнала

## Что нужно

- Java 17+
- Discord Bot Token — создаётся на [discord.com/developers](https://discord.com/developers/applications)
- Учётные данные Eljur от колледжа

---

**Автор:** [@marensovich](https://github.com/marensovich)