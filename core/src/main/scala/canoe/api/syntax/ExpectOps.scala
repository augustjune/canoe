package canoe.api.syntax

final class ExpectOps[A, B](private val original: Expect[A, B]) extends AnyVal {

  def when(p: B => Boolean): Expect[A, B] =
    new PartialFunction[A, B] {
      def isDefinedAt(a: A): Boolean =
        original.isDefinedAt(a) && p(original.apply(a))

      def apply(a: A): B = original.apply(a)
    }

  def map[C](f: B => C): Expect[A, C] = original andThen f
}
