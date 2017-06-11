package petclinic
package mysql

import petclinic.mysql.implicits._
import petclinic.mysql.PetRepo._
import scala.concurrent.ExecutionContext
import java.sql.Statement

final class PetRepo(implicit ec: ExecutionContext) extends petclinic.PetRepo[Response] {

  def findById(id: Long): Response[Pet] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsById)
      statement.setLong(1, id)
      val resultSet = statement.executeQuery()
      resultSet.toEntity[Pet] match {
        case Some(pet) => Right(pet)
        case None =>
          Left(PetClinicError(s"Pet with id: $id not found", httpErrorCode = Some(404)))
      }
    }

  def save(pet: Pet): Response[Long] =
    withConnection { conn =>
      val pst = conn.prepareStatement(InsertPet, Statement.RETURN_GENERATED_KEYS)
      pst.setString(1, pet.name)
      pst.setObject(2, pet.birthDate)
      pst.setLong(3, pet.petTypeId)
      pst.setLong(4, pet.ownerId)
      pst.executeUpdate()
      val keys = pst.getGeneratedKeys
      if (keys.next())
        Right(keys.getLong(1))
      else
        Left(PetClinicError())
    }

  def update(pet: Pet): Response[Unit] =
    withConnection { conn =>
      val pst = conn.prepareStatement(UpdatePet)
      pst.setString(1, pet.name)
      pst.setObject(2, pet.birthDate)
      pst.setLong(3, pet.petTypeId)
      pst.setLong(4, pet.ownerId)
      pst.setLong(5, pet.id.get)
      Right(pst.executeUpdate())
    }

  def findPetTypeById(petTypeId: Long): Response[PetType] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetTypesById)
      statement.setLong(1, petTypeId)
      val resultSet = statement.executeQuery()
      resultSet.toEntity[PetType] match {
        case Some(petType) => Right(petType)
        case None =>
          Left(PetClinicError(s"PetType with id $petTypeId not found", httpErrorCode = Some(404)))
      }
    }

  def findPetTypes: Response[List[PetType]] =
    withConnection { conn =>
      val statement = conn.createStatement()
      val resultSet = statement.executeQuery(PetTypesBaseSql)
      Right(resultSet.toEntityList[PetType])
    }

  def findPetsByOwnerId(ownerId: Long): Response[List[Pet]] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsByOwnerId)
      statement.setLong(1, ownerId)
      Right(statement.executeQuery().toEntityList[Pet])
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
