package canoe.models

/**
  * Answer of a user in a non-anonymous poll.
  */
final case class PollAnswer(pollId: String, user: User, optionIds: List[Int])