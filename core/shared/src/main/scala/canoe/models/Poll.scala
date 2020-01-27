package canoe.models

/**
  * Represents a poll in one of the Telegram chats.
  *
  * @param id                    Unique poll identifier
  * @param question              Poll question, 1-255 characters
  * @param options               List of poll options
  * @param totalVoterCount       Total number of users that voted in the poll
  * @param isClosed              True, if the poll is closed
  * @param isAnonymous           True, if the poll is anonymous
  * @param `type`                Poll type, currently can be “regular” or “quiz”
  * @param allowsMultipleAnswers True, if the poll allows multiple answers
  * @param correctOptionId       0-based identifier of the correct answer option.
  *                              Available only for polls in the quiz mode, which are closed,
  *                              or was sent (not forwarded) by the bot or to the private chat with the bot.
  */
final case class Poll(id: String,
                      question: String,
                      options: List[PollOption],
                      totalVoterCount: Int,
                      isClosed: Boolean,
                      isAnonymous: Boolean,
                      `type`: String,
                      allowsMultipleAnswers: Boolean,
                      correctOptionId: Option[Int])
