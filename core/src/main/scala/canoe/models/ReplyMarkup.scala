package canoe.models

/**
  * Base for custom (keyboard) markups.
  */
sealed trait ReplyMarkup

/**
  * Represents a custom keyboard with reply options (see Introduction to bots for details).
  *
  * ''Example:''
  * A user requests to change the bot's language, bot replies to the request with a keyboard to select the new language.
  * Other users in the group don't see the keyboard.
  *
  * @param keyboard        Sequence of button rows, each represented by an Array of KeyboardButton objects
  * @param resizeKeyboard  Requests clients to resize the keyboard vertically for optimal fit
  *                        (e.g., make the keyboard smaller if there are just two rows of buttons).
  *                        Defaults to false, in which case the custom keyboard is always of the same height as the app's standard keyboard.
  * @param oneTimeKeyboard Requests clients to hide the keyboard as soon as it's been used. Defaults to false.
  * @param selective       Use this parameter if you want to show the keyboard to specific users only.
  *                        Targets:
  *                        1) users that are @mentioned in the text of the Message object;
  *                        2) if the bot's message is a reply (has replyToMessage_id), sender of the original message.
  */
final case class ReplyKeyboardMarkup(keyboard: Seq[Seq[KeyboardButton]],
                                     resizeKeyboard: Option[Boolean] = None,
                                     oneTimeKeyboard: Option[Boolean] = None,
                                     selective: Option[Boolean] = None)
    extends ReplyMarkup

object ReplyKeyboardMarkup {

  /**
    * Markup with a single big button.
    */
  def singleButton(button: KeyboardButton,
                   resizeKeyboard: Option[Boolean] = None,
                   oneTimeKeyboard: Option[Boolean] = None,
                   selective: Option[Boolean] = None): ReplyKeyboardMarkup =
    ReplyKeyboardMarkup(Seq(Seq(button)), resizeKeyboard, oneTimeKeyboard, selective)

  /**
    * Markup with a single row of buttons.
    */
  def singleRow(buttonRow: Seq[KeyboardButton],
                resizeKeyboard: Option[Boolean] = None,
                oneTimeKeyboard: Option[Boolean] = None,
                selective: Option[Boolean] = None): ReplyKeyboardMarkup =
    ReplyKeyboardMarkup(Seq(buttonRow), resizeKeyboard, oneTimeKeyboard, selective)

  /**
    * Markup with a single column of stacked buttons.
    */
  def singleColumn(buttonColumn: Seq[KeyboardButton],
                   resizeKeyboard: Option[Boolean] = None,
                   oneTimeKeyboard: Option[Boolean] = None,
                   selective: Option[Boolean] = None): ReplyKeyboardMarkup =
    ReplyKeyboardMarkup(buttonColumn.map(Seq(_)), resizeKeyboard, oneTimeKeyboard, selective)
}

/**
  * Upon receiving a message with this object, Telegram clients will remove the current custom keyboard and display the default letter-keyboard.
  * By default, custom keyboards are displayed until a new keyboard is sent by a bot.
  * An exception is made for one-time keyboards that are hidden immediately after the user presses a button (see ReplyKeyboardMarkup).
  *
  * @param removeKeyboard Requests clients to remove the custom keyboard (user will not be able to summon this keyboard;
  *                       if you want to hide the keyboard from sight but keep it accessible, use one_time_keyboard in ReplyKeyboardMarkup)
  * @param selective      Use this parameter if you want to remove the keyboard for specific users only.
  *                       Targets:
  *                       1) users that are @mentioned in the text of the Message object;
  *                       2) if the bot's message is a reply (has reply_to_message_id), sender of the original message.
  *
  *                       Example:
  *                       A user votes in a poll, bot returns confirmation message in reply to the vote and removes the keyboard
  *                       for that user, while still showing the keyboard with poll options to users who haven't voted yet.
  */
final case class ReplyKeyboardRemove(removeKeyboard: Boolean = true, selective: Option[Boolean] = None)
    extends ReplyMarkup

/**
  * Represents an inline keyboard that appears right next to the message it belongs to.
  *
  * ''Warning:''
  * Inline keyboards are currently being tested and are only available in one-on-one chats (i.e., user-bot or user-user in the case of inline bots).
  *
  * ''Note:''
  * This will only work in Telegram versions released after 9 April, 2016. Older clients will display unsupported message.
  *
  * ''Warning:''
  * Inline keyboards are currently being tested and are not available in channels yet.
  * For now, feel free to use them in one-on-one chats or groups.
  *
  * @param inlineKeyboard Sequence of button rows, each represented by a sequence of InlineKeyboardButton objects
  */
final case class InlineKeyboardMarkup(inlineKeyboard: Seq[Seq[InlineKeyboardButton]]) extends ReplyMarkup

object InlineKeyboardMarkup {

  /**
    * Markup with a single button.
    */
  def singleButton(button: InlineKeyboardButton): InlineKeyboardMarkup =
    InlineKeyboardMarkup(Seq(Seq(button)))

  /**
    * Markup with a single row of buttons, stacked horizontally.
    */
  def singleRow(buttonRow: Seq[InlineKeyboardButton]): InlineKeyboardMarkup =
    InlineKeyboardMarkup(Seq(buttonRow))

  /**
    * Markup with a single column of buttons, stacked vertically.
    */
  def singleColumn(buttonColumn: Seq[InlineKeyboardButton]): InlineKeyboardMarkup =
    InlineKeyboardMarkup(buttonColumn.map(Seq(_)))
}

/**
  * Upon receiving a message with this object, Telegram clients will display a reply interface to the user
  * (act as if the user has selected the bot's message and tapped 'Reply').
  * This can be extremely useful if you want to create user-friendly step-by-step interfaces without having to sacrifice privacy mode.
  *
  * '''Example:'''
  * A poll bot for groups runs in privacy mode (only receives commands, replies to its messages and mentions).
  * There could be two ways to create a new poll:
  * Explain the user how to send a command with parameters (e.g. /newpoll question answer1 answer2).
  * May be appealing for hardcore users but lacks modern day polish.
  * Guide the user through a step-by-step process.
  * 'Please send me your question', 'Cool, now let's add the first answer option', 'Great. Keep adding answer options,
  * then send /done when you're ready'.
  *
  * The last option is definitely more attractive. And if you use ForceReply in your bot's questions, it will receive
  * the user's answers even if it only receives replies, commands and mentions - without any extra work for the user.
  *
  * @param forceReply Shows reply interface to the user, as if they manually selected the bot's message and tapped 'Reply'
  * @param selective  Use this parameter if you want to force reply from specific users only.
  *                   Targets:
  *                   1) users that are @mentioned in the text of the Message object;
  *                   2) if the bot's message is a reply (has replyToMessage_id), sender of the original message.
  */
final case class ForceReply(forceReply: Boolean = true, selective: Option[Boolean] = None) extends ReplyMarkup
