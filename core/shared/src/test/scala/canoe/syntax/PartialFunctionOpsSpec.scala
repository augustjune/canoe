package canoe.syntax

import org.scalatest.funsuite.AnyFunSuite

class PartialFunctionOpsSpec extends AnyFunSuite {

  val evenInt: PartialFunction[Int, Int] = {
    case i if i % 2 == 0 => i
  }

  test("#when appends new condition to isDefineAt") {
    val evenBiggerThanTen = evenInt.when(_ > 10)
    assert(evenBiggerThanTen.isDefinedAt(12))
    assert(!evenBiggerThanTen.isDefinedAt(6))
    assert(!evenBiggerThanTen.isDefinedAt(13))
  }

  test("#map applies provided function over the result") {
    val evenString = evenInt.map(_.toString)
    assert(evenString(6) == 6.toString)
  }
}
