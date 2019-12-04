package canoe.syntax

import org.scalatest.freespec.AnyFreeSpec

class PartialFunctionOpsSpec extends AnyFreeSpec {
  val evenInt: PartialFunction[Int, Int] = {
    case i if i % 2 == 0 => i
  }

  "when" - {
    "appends new condition to isDefineAt" in {
      val evenBiggerThanTen = evenInt.when(_ > 10)
      assert(evenBiggerThanTen.isDefinedAt(12))
      assert(!evenBiggerThanTen.isDefinedAt(6))
      assert(!evenBiggerThanTen.isDefinedAt(13))
    }
  }

  "map" - {
    "applies provided function over the result" in {
      val evenString = evenInt.map(_.toString)
      assert(evenString(6) == 6.toString)
    }
  }
}
