package petclinic
package mysql

import petclinic.mysql.implicits._
import petclinic.mysql.OwnerRepo._
import scala.concurrent.ExecutionContext
import java.sql.Statement

final class OwnerRepo(implicit ec: ExecutionContext) extends petclinic.OwnerRepo[Response] {

  def findById(id: Long): Response[Owner] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnerById)
      pst.setLong(1, id)
      val resultSet = pst.executeQuery()
      resultSet.toEntity[Owner] match {
        case Some(owner) => Right(owner)
        case None =>
          Left(PetClinicError(s"Owner with id: $id not found", httpErrorCode = Some(404)))
      }
    }

  def save(owner: Owner): Response[Long] =
    withConnection { conn =>
      val pst = conn.prepareStatement(InsertOwner, Statement.RETURN_GENERATED_KEYS)
      pst.setString(1, owner.firstName)
      pst.setString(2, owner.lastName)
      pst.setString(3, owner.address)
      pst.setString(4, owner.city)
      pst.setString(5, owner.telephone)
      pst.executeUpdate()
      val keys = pst.getGeneratedKeys
      if (keys.next())
        Right(keys.getLong(1))
      else
        Left(PetClinicError())
    }

  def update(owner: Owner): Response[Unit] =
    withConnection { conn =>
      val pst = conn.prepareStatement(UpdateOwner)
      pst.setString(1, owner.firstName)
      pst.setString(2, owner.lastName)
      pst.setString(3, owner.address)
      pst.setString(4, owner.city)
      pst.setString(5, owner.telephone)
      pst.setLong(6, owner.id.get)
      Right(pst.executeUpdate())
    }

  def findByLastName(lastName: String): Response[List[Owner]] =
    withConnection { conn =>
      val pst = conn.prepareStatement(FindOwnersByLastName)
      pst.setString(1, lastName)
      Right(pst.executeQuery().toEntityList[Owner])
    }
}

object OwnerRepo {
  val Owners    = "owners"
  val Id        = "id"
  val FirstName = "first_name"
  val LastName  = "last_name"
  val Address   = "address"
  val City      = "city"
  val Telephone = "telephone"

  private val BaseSql =
    s"select $Id, $FirstName, $LastName, $Address, $City, $Telephone from $Owners"
  private val FindOwnerById        = s"$BaseSql where $Id = ?"
  private val FindOwnersByLastName = s"$BaseSql where $LastName = ?"
  private val InsertOwner =
    s"""|insert into $Owners ($FirstName, $LastName, $Address, $City, $Telephone)
        |values (?, ?, ?, ?, ?)""".stripMargin
  private val UpdateOwner =
    s"""|update $Owners
        |set $FirstName = ?, $LastName = ?, $Address = ?, $City = ?, $Telephone = ?
        |where $Id = ?""".stripMargin
}
