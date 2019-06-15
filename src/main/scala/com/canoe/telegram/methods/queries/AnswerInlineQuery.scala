package com.canoe.telegram.methods.queries

import com.canoe.telegram.methods.JsonRequest
import com.canoe.telegram.models.InlineQueryResult

/** Use this method to send answers to an inline query. On success, True is returned.
  * No more than 50 results per query are allowed.
  *
  * @param inline_query_id     String Unique identifier for the answered query
  * @param results           Array of InlineQueryResult A JSON-serialized array of results for the inline query
  * @param cache_time         Integer Optional The maximum amount of time in seconds that the result of the inline query may be cached on the server. Defaults to 300.
  * @param is_personal        Boolean Optional Pass True, if results may be cached on the server side only for the user that sent the query. By default, results may be returned to any user who sends the same query
  * @param next_offset        String Optional Pass the offset that a client should send in the next query with the same text to receive more results. Pass an empty string if there are no more results or if you don't support pagination. Offset length can't exceed 64 bytes.
  * @param switch_pm_text      String Optional If passed, clients will display a button with specified text that switches the user to a private chat with the bot and sends the bot a start message with the parameter switch_pm_parameter
  * @param switch_pm_parameter String Optional Parameter for the start message sent to the bot when user presses the switch buttonExample: An inline bot that sends YouTube videos can ask the user to connect the bot to their YouTube account to adapt search results accordingly. To do this, it displays a 'Connect your YouTube account' button above the results, or even before showing any. The user presses the button, switches to a private chat with the bot and, in doing so, passes a start parameter that instructs the bot to return an oauth link. Once done, the bot can offer a switch_inline button so that the user can easily return to the chat where they wanted to use the bot's inline capabilities.
  */
case class AnswerInlineQuery(inline_query_id: String,
                             results: Seq[InlineQueryResult],
                             cache_time: Option[Int] = None,
                             is_personal: Option[Boolean] = None,
                             next_offset: Option[String] = None,
                             switch_pm_text: Option[String] = None,
                             switch_pm_parameter: Option[String] = None
                            ) extends JsonRequest[Boolean]
