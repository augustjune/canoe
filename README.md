canoe
=============

[![Build Status](https://travis-ci.org/augustjune/canoe.svg?branch=master)](https://travis-ci.org/augustjune/canoe)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.augustjune/canoe_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.augustjune/canoe_2.12)
    
### Overview
**canoe** provides a purely functional streaming interface over [Telegram Bot API](https://core.telegram.org/bots/api) 
and allows you to build interactive chatbots using idiomatic Scala code.

### Getting started
sbt dependency:
```scala
libraryDependencies += "org.augustjune" %% "canoe" % "<version>"
```
You can find the latest version in [releases](https://github.com/augustjune/canoe/releases) tab or by clicking on the maven-central badge.

Imports:
```scala
import canoe.api._
import canoe.syntax._
```

### The problem
Building interactive chatbots requires maintaining the state of each conversation, 
with possible interaction across them and/or using shared resources.
The complexity of this task grows rapidly with the advancement of the bot.
**canoe** solves this problem by decomposing behavior of the bot into the set of scenarios 
which the chatbot will follow.

### Example
Here's a quick example of how the definition of simple bot behavior looks like in **canoe**. 
More samples can be found [here](https://github.com/augustjune/canoe/tree/master/examples/src/main/scala/samples). 
```scala
import canoe.api._
import canoe.syntax._
import cats.effect.ConcurrentEffect
import fs2.Stream

def app[F[_]: ConcurrentEffect]: F[Unit] = 
  Stream
    .resource(TelegramClient.global[F](token))
    .flatMap { implicit client => Bot.polling[F].follow(greetings) }
    .compile.drain

def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat  <- Scenario.start(command("hi").chat)
      _     <- Scenario.eval(chat.send("Hello. What's your name?"))
      name  <- Scenario.next(text)
      _     <- Scenario.eval(chat.send(s"Nice to meet you, $name"))
    } yield ()
```

Regardless of whether you decide to use scenarios for steering the bot, 
you are still able to use all functionality of Telegram Bot API in a streaming context, 
as it is demonstrated [here](https://github.com/augustjune/canoe/blob/master/examples/src/main/scala/samples/NoScenario.scala).

### Using webhooks
**canoe** also provides a support for obtaining messages from Telegram by setting a webhook.
The same app described above would look this way using webhook version.
```scala
import canoe.api._
import canoe.syntax._
import cats.effect.ConcurrentEffect
import fs2.Stream

val url: String = "<your server url>"

def app[F[_]: ConcurrentEffect: Timer]: F[Unit] =
    Stream
      .resource(TelegramClient.global[F](token))
      .flatMap { implicit client =>
        Stream
          .resource(Bot.hook[F](url))
          .flatMap(_.follow(greetings))
      }
      .compile.drain

def greetings[F[_]: TelegramClient]: Scenario[F, Unit] = ???  // Scenario stays unchanged
```
When using webhook version you have to specify the `url` to which Telegram messages will be sent. 
This address must be reachable for the Telegram, 
so in case you're using your local environment, you have to expose your local host to the Internet.
It can be achieved using **ngrok** simply following this [comprehensive guide](https://developer.github.com/webhooks/configuring/#using-ngrok). 