#canoe

**canoe** is a compositional Scala Telegram Bot library. 
It allows you to build interactive Telegram bots in a purely functional style.

## Getting started
The library is not yet available on Maven Central, but it's coming soon.

## Usage example
Here's a quick sample of how the definition of simple bot behavior looks like in **canoe**. 
Complete usage examples will be added here. 
```scala
val bot = new Bot(client)

val greetings: Scenario[IO, Unit] =
  for {
      chat <- Scenario.start { case m: TextMessage if m.text.startsWith("/hi") => m.chat }
      _    <- Scenario.eval { chat.send(BotMessage("Hello. What's your name?")) }
      name <- Scenario.next { case m: TextMessage => m.text }
      _    <- Scenario.eval { chat.send(BotMessage(s"Nice to meet you, $name"))}
    } yield ()

bot.follow(greetings)
```