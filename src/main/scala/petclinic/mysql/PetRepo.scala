package petclinic
package mysql

import petclinic.mysql.implicits._
import scala.concurrent.{ ExecutionContext, Future }

final class PetRepo(implicit ec: ExecutionContext) extends petclinic.PetRepo[Future] {

  private[this] val PetsBaseSql     = "select id, name, birth_date, type_id, owner_id from pets"
  private[this] val PetTypesBaseSql = "select id, name from types"
  private[this] val PetsById        = s"$PetsBaseSql where id = ?"
  private[this] val PetsByOwnerId   = s"$PetsBaseSql where owner_id = ?"
  private[this] val PetTypesById    = s"$PetTypesBaseSql where id = ?"

  def findById(id: Int): Future[Pet] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsById)
      statement.setInt(1, id)
      val resultSet = statement.executeQuery()
      resultSet.toEntity[Pet].getOrElse(throw SQLException(s"Pet with id: $id not found"))
    }

  def save(pet: Pet): Future[Unit] = ???

  def findPetTypeById(petTypeId: Int): Future[PetType] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetTypesById)
      statement.setInt(1, petTypeId)
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

  def findPetsByOwnerId(ownerId: Int): Future[List[Pet]] =
    withConnection { conn =>
      val statement = conn.prepareStatement(PetsByOwnerId)
      statement.setInt(1, ownerId)
      statement.executeQuery().toEntityList[Pet]
    }
}
