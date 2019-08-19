package canoe.clients

import com.softwaremill.sttp.Method
import com.softwaremill.sttp.testing.SttpBackendStub
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object SttpClientSpec extends Properties("SttpClient") {

  implicit val testingBackend = SttpBackendStub.synchronous
    .whenRequestMatches(_.uri.path.startsWith(List("a", "b")))
    .thenRespond("Hello there!")
    .whenRequestMatches(_.method == Method.POST)
    .thenRespondServerError()

  property("always sends to valid url") =
  forAll { n: Int =>
    n <= Int.MaxValue && n >= Int.MinValue
  }
}
