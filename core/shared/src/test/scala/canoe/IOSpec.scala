package canoe

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.AsyncTestSuite

trait IOSpec extends AsyncIOSpec { asyncTestSuite: AsyncTestSuite =>
}
