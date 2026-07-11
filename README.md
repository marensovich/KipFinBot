# 🤖 KipFinBot

A **Discord bot** for **KIPFIN College** integrating with the Eljur electronic gradebook system.

## 📋 Description

Bridges KIPFIN College's Eljur electronic journal with Discord. Students can access their grades, schedules, and other academic data directly through Discord commands.

## 🛠️ Tech Stack

- **Java** — core language
- **JDA (Java Discord API)** — Discord bot framework
- **Eljur API** — electronic gradebook integration
- **Maven** — build tool

## ✨ Features

- Eljur electronic journal integration via Discord
- Group commands for shared information
- Private commands for personal data
- EljurDatabase abstraction layer
- Grade and schedule lookups through Discord

## 🚀 Running

```bash
git clone https://github.com/marensovich/KipFinBot.git
cd KipFinBot

# Configure Discord token and Eljur credentials
# in config.properties or environment variables

mvn package
java -jar target/KipFinBot.jar
```

> Requires: Java 17+, Maven, Discord Bot Token, Eljur credentials
