package com.canoe.telegram.methods.users

import com.canoe.telegram.methods.JsonRequest
import com.canoe.telegram.models.User

/** A simple method for testing your bot's auth token. Requires no parameters.
  * Returns basic information about the bot in form of a User object.
  */
case object GetMe extends JsonRequest[User]
