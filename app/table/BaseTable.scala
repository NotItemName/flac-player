package table

import table.Tables.dbConfig.driver.api._

abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
}
