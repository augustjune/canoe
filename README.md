canoe
=============

[![Continuous Integration](https://github.com/augustjune/canoe/actions/workflows/ci.yml/badge.svg)](https://github.com/augustjune/canoe/actions/workflows/ci.yml)
[![Gitter](https://badges.gitter.im/augustjune-canoe/community.svg)](https://gitter.im/augustjune-canoe/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.augustjune/canoe_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.augustjune/canoe_2.12)
[![Telegram](https://img.shields.io/badge/Bot%20API-4.9%20-00aced.svg)](https://core.telegram.org/bots/api#recent-changes)

### Overview
**canoe** is a purely functional, compositional library for building interactive Telegram bots.
It provides functional streaming interface over [Telegram Bot API](https://core.telegram.org/bots/api)
with built-in abstractions for describing your chatbot behavior.

### Getting started
sbt dependency:
```scala
libraryDependencies += "org.augustjune" %% "canoe" % "<version>"
```
You can find the latest version in [releases](https://github.com/augustjune/canoe/releases) tab
or by clicking on the maven-central badge. The library is available for Scala 2.12, 2.13, Scala 3 and Scala.js.

Imports:
```scala
import canoe.api._
import canoe.syntax._
```

### The problem
Building interactive chatbots requires maintaining the state of each conversation,
with possible interaction across them and/or using shared resources.
The complexity of this task grows rapidly with the advancement of the bot.
**canoe** solves this problem by decomposing behavior of the bot into a set of scenarios
which the chatbot will follow.

### Basic example
Here's a quick example of how the definition of simple bot behavior looks like in **canoe**.
More samples can be found [here](https://github.com/augustjune/canoe/tree/master/examples/src/main/scala/samples).

```scala
import canoe.api._
import canoe.syntax._
import cats.effect.Async
import fs2.Stream

def app[F[_]: Async]: F[Unit] =
  Stream
    .resource(TelegramClient[F](token))
    .flatMap(implicit client => Bot.polling[F].follow(greetings))
    .compile
    .drain

def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("hi").chat)
      _    <- Scenario.eval(chat.send("Hello. What's your name?"))
      name <- Scenario.expect(text)
      _    <- Scenario.eval(chat.send(s"Nice to meet you, $name"))
    } yield ()
```

Scenarios are executed concurrently in a non-blocking fashion,
allowing to handle multiple users at the same time.
In fact, even the same scenario can be triggered multiple times before
the previous execution is completed.
This can be extremely useful when you allow users to schedule long-running jobs
and don't want to make them wait before they can schedule the new ones.
As example may serve a simple [alarm clock implementation](https://github.com/augustjune/canoe/tree/master/examples/src/main/scala/samples/TimerAlarm.scala).


### Telegram Bot API methods
Low level abstractions are available through standalone Telegram Bot API methods from `canoe.methods` package.
Having instance of `TelegramClient` in implicit scope,
you can use `call` method on constructed action in order to execute it in effect `F`.

```scala
def sendText[F[_]: TelegramClient](chatId: Long, text: String): F[TextMessage] =
  SendMessage(chatId, text).call
```

As an alternative, all the methods from Telegram Bot API are available from corresponding models,
e.g.`chat.kickUser(user.id)`, `message.editText("edited")`.

### Webhook support
**canoe** also provides support for obtaining messages from Telegram by setting a webhook.
Full example may be found [here](https://github.com/augustjune/canoe/blob/master/examples/src/main/scala/samples/Webhook.scala).

### Handling errors
There's a lot of things that may go wrong during your scenarios executions,
from user input to the network issues.
For this reason, `Scenario` forms a `MonadError` for any `F`.
It means that you can use built-in `handleErrorWith` and `attempt` methods,
in order to react to the raised error or ensure that bot workflow won't break.
Full example may be found [here](https://github.com/augustjune/canoe/blob/master/examples/src/main/scala/samples/ErrorHandling.scala).

### Contribution
If you're interested in the project PRs are very welcomed.
In case it's a feature you'd like to introduce, it is recommended to discuss it first by raising an issue
or simply using [gitter](https://gitter.im/augustjune-canoe/community).
