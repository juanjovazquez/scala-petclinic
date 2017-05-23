package petclinic
package mysql

import petclinic.mysql.implicits._

import scala.concurrent.{ExecutionContext, Future}

final class OwnerRepo(implicit ec: ExecutionContext) extends petclinic.OwnerRepo[Future] {

  private[this] val BaseSql =
    "select id, first_name, last_name, address, city, telephone from owners"

  private[this] val FindOwnerById = s"$BaseSql where id = ?"

  private[this] val FindOwnersByLastName = s"$BaseSql where last_name = ?"

  def findById(id: Int): Future[Owner] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnerById)
      pst.setInt(1, id)
      val resultSet = pst.executeQuery()
      resultSet.toEntity[Owner].getOrElse(throw SQLException(s"Owner with id: $id not found"))
    }

  def save(owner: Owner): Future[Unit] = ???

  def findByLastName(lastName: String): Future[List[Owner]] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnersByLastName)
      pst.setString(1, lastName)
      pst.executeQuery().toEntityList[Owner]
    }
}
