package canoe.marshalling

import org.scalatest.freespec.AnyFreeSpec

class CaseStringSpec extends AnyFreeSpec {
  val snake: String = "some_snake_case_string"
  val camel: String = "someSnakeCaseString"
  val pascal: String = camel.capitalize

  "snake_case" - {
    "to camelCase" in {
      assert(snake.camelCase == camel)
    }

    "to PascalCase" in {
      assert(snake.pascalCase == pascal)
    }
  }

  "camelCase" - {
    "to snake_case" in {
      assert(camel.snakeCase == snake)
    }

    "to PascalCase" in {
      assert(camel.pascalCase == pascal)
    }
  }

  "PascalCase" - {
    "to snake_case" in {
      assert(pascal.snakeCase == snake)
    }

    "to camelCase" in {
      assert(pascal.camelCase == camel)
    }
  }
}
