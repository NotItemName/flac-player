package repository

import model.Model
import table.BaseTable
import table.Tables.dbConfig.driver.api._

import scala.language.higherKinds

class GenericRepository[M <: Model, B <: BaseTable[M]](table: TableQuery[B]) {

  def delete(id: Int): DBIO[Int] = table.filter(_.id === id).delete

  def update(id: Int, model: M): DBIO[Int] = table.filter(_.id === id).update(model)

  def findById(id: Int): DBIO[Option[M]] = table.filter(_.id === id).result.headOption

  def findAll(from: Int, limit: Int): DBIO[Seq[M]] = table.drop(from).take(limit).result

  def insert(model: M): DBIO[Int] = {
    (table returning table.map(_.id)) += model
  }

}
