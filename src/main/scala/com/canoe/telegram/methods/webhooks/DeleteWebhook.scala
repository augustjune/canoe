package com.canoe.telegram.methods.webhooks

import com.canoe.telegram.methods.JsonRequest

/** Use this method to remove webhook integration if you decide to switch back to getUpdates.
  * Returns True on success. Requires no parameters.
  */
case object DeleteWebhook extends JsonRequest[Boolean]
