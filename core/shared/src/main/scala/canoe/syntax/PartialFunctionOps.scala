package canoe.syntax

final class PartialFunctionOps[A, B](private val original: PartialFunction[A, B]) extends AnyVal {

  /**
    * Appends additional condition for the function to be defined at
    */
  def when(p: B => Boolean): PartialFunction[A, B] =
    new PartialFunction[A, B] {
      def isDefinedAt(in: A): Boolean =
        original.isDefinedAt(in) && p(original.apply(in))

      def apply(in: A): B =
        if (isDefinedAt(in)) original.apply(in)
        else throw new MatchError(in)
    }
}
