package model

trait Model[M <: Model[M]] {
  def id: Option[Int]

  def withId(id: Int): M
}
