package canoe.models

/**
 *
 * @param emoji Emoji on which the dice throw animation is based
 * @param value Value of the dice, 1-6 for currently supported base emoji
 */
final case class Dice(emoji: String, value: Int)
