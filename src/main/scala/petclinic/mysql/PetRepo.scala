package petclinic
package mysql

import petclinic.mysql.implicits._
import petclinic.mysql.PetRepo._
import scala.concurrent.{ExecutionContext, Future}
import java.sql.Statement

final class PetRepo(implicit ec: ExecutionContext) extends petclinic.PetRepo[Future] {

  def findById(id: Long): Future[Pet] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsById)
      statement.setLong(1, id)
      val resultSet = statement.executeQuery()
      resultSet.toEntity[Pet].getOrElse(throw SQLException(s"Pet with id: $id not found"))
    }

  def save(pet: Pet): Future[Long] =
    withConnection { conn =>
      val pst = conn.prepareStatement(InsertPet, Statement.RETURN_GENERATED_KEYS)
      pst.setString(1, pet.name)
      pst.setObject(2, pet.birthDate)
      pst.setLong(3, pet.petTypeId)
      pst.setLong(4, pet.ownerId)
      pst.executeUpdate()
      val keys = pst.getGeneratedKeys
      keys.next()
      keys.getLong(1)
    }

  def update(pet: Pet): Future[Unit] =
    withConnection { conn =>
      val pst = conn.prepareStatement(UpdatePet)
      pst.setString(1, pet.name)
      pst.setObject(2, pet.birthDate)
      pst.setLong(3, pet.petTypeId)
      pst.setLong(4, pet.ownerId)
      pst.setLong(5, pet.id.get)
      pst.executeUpdate()
    }

  def findPetTypeById(petTypeId: Long): Future[PetType] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetTypesById)
      statement.setLong(1, petTypeId)
      val resultSet = statement.executeQuery()
      resultSet
        .toEntity[PetType]
        .getOrElse(throw SQLException(s"PetType with id $petTypeId not found"))
    }

  def findPetTypes: Future[List[PetType]] =
    withConnection { conn =>
      val statement = conn.createStatement()
      val resultSet = statement.executeQuery(PetTypesBaseSql)
      resultSet.toEntityList[PetType]
    }

  def findPetsByOwnerId(ownerId: Long): Future[List[Pet]] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsByOwnerId)
      statement.setLong(1, ownerId)
      statement.executeQuery().toEntityList[Pet]
    }
}

object PetRepo {
  val Pets      = "pets"
  val Types     = "types"
  val Id        = "id"
  val Name      = "name"
  val BirthDate = "birth_date"
  val TypeId    = "type_id"
  val OwnerId   = "owner_id"

  private val PetsBaseSql     = s"select $Id, $Name, $BirthDate, $TypeId, $OwnerId from $Pets"
  private val PetTypesBaseSql = s"select $Id, $Name from $Types"
  private val PetsById        = s"$PetsBaseSql where $Id = ?"
  private val PetsByOwnerId   = s"$PetsBaseSql where $OwnerId = ?"
  private val PetTypesById    = s"$PetTypesBaseSql where $Id = ?"
  private val InsertPet =
    s"""|insert into $Pets ($Name, $BirthDate, $TypeId, $OwnerId)
        |values (?, ?, ?, ?)""".stripMargin
  private val UpdatePet =
    s"""|update $Pets
        |set $Name = ?, $BirthDate = ?, $TypeId = ?, $OwnerId = ?
        |where $Id = ?""".stripMargin
}
