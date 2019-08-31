package canoe.syntax

final class PartialFunctionOps[A, B](private val original: PartialFunction[A, B]) extends AnyVal {

  def when(p: B => Boolean): PartialFunction[A, B] =
    new PartialFunction[A, B] {
      def isDefinedAt(in: A): Boolean =
        original.isDefinedAt(in) && p(original.apply(in))

      def apply(in: A): B = original.apply(in)
    }

  def map[C](f: B => C): PartialFunction[A, C] = original andThen f
}
