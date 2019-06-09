package com.canoe.telegram.methods

import com.canoe.telegram.models.InputFile

/** Base type for multipart API requests (for file uploads)
  *
  * @tparam R Expected result type.
  *
  * Request will be serialized as multipart/form-data
  */
trait MultipartRequest[R] extends Request[R] {
  def getFiles: List[(String, InputFile)]
}
