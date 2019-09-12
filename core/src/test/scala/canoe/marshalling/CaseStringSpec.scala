package canoe.marshalling

import org.scalatest.FunSuite

class CaseStringSpec extends FunSuite {

  val snake: String = "some_snake_case_string"
  val camel: String = "someSnakeCaseString"
  val pascal: String = camel.capitalize

  test("snake_case to camelCase") {
    assert(snake.camelCase == camel)
  }

  test("snake_case to PascalCase") {
    assert(snake.pascalCase == pascal)
  }

  test("camelCase to snake_case") {
    assert(camel.snakeCase == snake)
  }

  test("camelcase to PascalCase") {
    assert(camel.pascalCase == pascal)
  }

  test("PascalCase to snake_case") {
    assert(pascal.snakeCase == snake)
  }

  test("PascalCase to camelCase") {
    assert(pascal.camelCase == camel)
  }
}
