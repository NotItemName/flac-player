package util

import scala.util.Try


object ImplicitsUtils {

  implicit class TryOps[T](val t: Try[T]) extends AnyVal {
    def eventually[Ignore](effect: => Ignore): Try[T] = {
      val ignoring = (_: Any) => { effect; t }
      t transform (ignoring, ignoring)
    }
  }

}
