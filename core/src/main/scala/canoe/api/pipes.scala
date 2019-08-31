package canoe.api

import canoe.models.messages.TelegramMessage
import canoe.models.{CallbackQuery, ChosenInlineResult, InlineQuery, Poll, PreCheckoutQuery, ShippingQuery, Update, _}
import fs2.Pipe

object pipes {

  def messages[F[_]]: Pipe[F, Update, TelegramMessage] =
    _.collect { case MessageReceived(_, message) => message }

  def editedMessages[F[_]]: Pipe[F, Update, TelegramMessage] =
    _.collect { case MessageEdited(_, message) => message }

  def channelPosts[F[_]]: Pipe[F, Update, TelegramMessage] =
    _.collect { case ChannelPost(_, post) => post }

  def editedPosts[F[_]]: Pipe[F, Update, TelegramMessage] =
    _.collect { case ChannelPostEdited(_, post) => post }

  def pollUpdates[F[_]]: Pipe[F, Update, Poll] =
    _.collect { case PollUpdated(_, poll) => poll }

  def inlineQueries[F[_]]: Pipe[F, Update, InlineQuery] =
    _.collect { case InlineQueryReceived(_, query) => query }

  def chosenInlineResults[F[_]]: Pipe[F, Update, ChosenInlineResult] =
    _.collect { case InlineResultSelected(_, result) => result }

  def callbackQueries[F[_]]: Pipe[F, Update, CallbackQuery] =
    _.collect { case CallbackButtonSelected(_, query) => query }

  def shippingQueries[F[_]]: Pipe[F, Update, ShippingQuery] =
    _.collect { case ShippingQueryReceived(_, query) => query }

  def preCheckoutQueries[F[_]]: Pipe[F, Update, PreCheckoutQuery] =
    _.collect { case PreCheckoutQueryReceived(_, query) => query }

}
