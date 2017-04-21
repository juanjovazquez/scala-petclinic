package petclinic
package mysql

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ ExecutionContext, Future }
import java.sql.SQLException

final class PetRepo(implicit ec: ExecutionContext) extends petclinic.PetRepo[Future] {
  def findById(id: Int): Future[Pet] =
    withConnection { conn =>
      val sql =
        """select id, name, birth_date, type_id, owner_id
          |from pets
          |where id = ?""".stripMargin
      val statement = conn.prepareStatement(sql)
      statement.setInt(1, id)
      val resultSet = statement.executeQuery()
      if (resultSet.next)
        Pet(
          resultSet.getInt("id"),
          resultSet.getString("name"),
          resultSet.getDate("birth_date"),
          resultSet.getInt("type_id"),
          resultSet.getInt("owner_id"))
      else
        throw new SQLException(s"Pet with id: $id not found")
    }

  def save(pet: Pet): Future[Unit] = ???

  def findPetTypeById(petTypeId: Int): Future[PetType] =
    withConnection { conn =>
      val sql       = "select id, name from types where id = ?"
      val statement = conn.prepareStatement(sql)
      statement.setInt(1, petTypeId)
      val resultSet = statement.executeQuery()
      if (resultSet.next)
        PetType(resultSet.getInt("id"), resultSet.getString("name"))
      else
        throw new SQLException(s"PetType with id $petTypeId not found")
    }

  def findPetTypes: Future[List[PetType]] =
    withConnection { conn =>
      val buffer    = new ListBuffer[PetType]
      val statement = conn.createStatement()
      val resultSet = statement.executeQuery("select id, name from types")
      while (resultSet.next) {
        buffer +=
          PetType(resultSet.getInt("id"), resultSet.getString("name"))
      }
      buffer.toList
    }
}
