package canoe.models

/**
  * One row of the high scores table for a game.
  *
  * @param position  Position in high score table for the game
  */
case class GameHighScore(position: Long, user: User, score: Long)
