package petclinic
package mysql

import petclinic.mysql.implicits._
import scala.concurrent.{ ExecutionContext, Future }

final class PetRepo(implicit ec: ExecutionContext) extends petclinic.PetRepo[Future] {

  private[this] val PetsBaseSql =
    "select id, name, birth_date, type_id, owner_id from pets"

  private[this] val PetTypesBaseSql =
    "select id, name from types"

  def findById(id: Int): Future[Pet] =
    withConnection { conn =>
      val sql       = s"$PetsBaseSql where id = ?"
      val statement = conn.prepareStatement(sql)
      statement.setInt(1, id)
      val resultSet = statement.executeQuery()
      resultSet.toEntity[Pet].getOrElse(throw SQLException(s"Pet with id: $id not found"))
    }

  def save(pet: Pet): Future[Unit] = ???

  def findPetTypeById(petTypeId: Int): Future[PetType] =
    withConnection { conn =>
      val sql       = s"$PetTypesBaseSql where id = ?"
      val statement = conn.prepareStatement(sql)
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
      val sql       = s"$PetsBaseSql where owner_id = ?"
      val statement = conn.prepareStatement(sql)
      statement.setInt(1, ownerId)
      statement.executeQuery().toEntityList[Pet]
    }
}
