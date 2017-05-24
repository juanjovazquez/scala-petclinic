package petclinic
package mysql

import petclinic.mysql.implicits._
import scala.concurrent.{ ExecutionContext, Future }
import java.sql.Statement

final class OwnerRepo(implicit ec: ExecutionContext) extends petclinic.OwnerRepo[Future] {

  private[this] val BaseSql =
    "select id, first_name, last_name, address, city, telephone from owners"
  private[this] val FindOwnerById        = s"$BaseSql where id = ?"
  private[this] val FindOwnersByLastName = s"$BaseSql where last_name = ?"
  private[this] val InsertOwner =
    """|insert into owners (first_name, last_name, address, city, telephone)
       |values (?, ?, ?, ?, ?)""".stripMargin
  private[this] val UpdateOwner =
    """|update owners
       |set first_name = ?, last_name = ?, address = ?, city = ?, telephone = ?
       |where id = ?""".stripMargin

  def findById(id: Long): Future[Owner] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnerById)
      pst.setLong(1, id)
      val resultSet = pst.executeQuery()
      resultSet.toEntity[Owner].getOrElse(throw SQLException(s"Owner with id: $id not found"))
    }

  def save(owner: Owner): Future[Long] =
    withConnection { conn =>
      val pst = conn.prepareStatement(InsertOwner, Statement.RETURN_GENERATED_KEYS)
      pst.setString(1, owner.firstName)
      pst.setString(2, owner.lastName)
      pst.setString(3, owner.address)
      pst.setString(4, owner.city)
      pst.setString(5, owner.telephone)
      pst.executeUpdate()
      val keys = pst.getGeneratedKeys
      keys.next()
      keys.getLong(1)
    }

  def update(owner: Owner): Future[Unit] =
    withConnection { conn =>
      val pst = conn.prepareStatement(UpdateOwner)
      pst.setString(1, owner.firstName)
      pst.setString(2, owner.lastName)
      pst.setString(3, owner.address)
      pst.setString(4, owner.city)
      pst.setString(5, owner.telephone)
      pst.setLong(6, owner.id.get)
      pst.executeUpdate()
    }

  def findByLastName(lastName: String): Future[List[Owner]] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnersByLastName)
      pst.setString(1, lastName)
      pst.executeQuery().toEntityList[Owner]
    }
}
