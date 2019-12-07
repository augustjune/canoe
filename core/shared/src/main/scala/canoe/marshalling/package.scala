package canoe

package object marshalling {

  private[canoe] implicit class CaseString(private val word: String) extends AnyVal {

    def camelCase: String =
      if (word.isEmpty) word
      else word.substring(0, 1).toLowerCase + word.pascalCase.substring(1)

    def pascalCase: String = word.split('_').map(_.capitalize).mkString("")

    def snakeCase: String = {
      val spacesPattern = "[-\\s]".r
      val firstPattern = "([A-Z]+)([A-Z][a-z])".r
      val secondPattern = "([a-z\\d])([A-Z])".r
      val replacementPattern = "$1_$2"
      spacesPattern
        .replaceAllIn(
          secondPattern.replaceAllIn(
            firstPattern.replaceAllIn(word, replacementPattern),
            replacementPattern
          ),
          "_"
        )
        .toLowerCase
    }
  }
}
