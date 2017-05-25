package petclinic
package mysql

import scala.collection.mutable.ListBuffer
import java.sql.ResultSet

trait FromResultSet[A] {
  def toEntityList(rs: ResultSet): List[A] = {
    val buffer = new ListBuffer[A]
    while (rs.next) {
      buffer += convert(rs)
    }
    buffer.toList
  }

  def toEntity(rs: ResultSet): Option[A] =
    toEntityList(rs).headOption

  protected def convert(rs: ResultSet): A
}

object FromResultSet {

  def apply[A](implicit instance: FromResultSet[A]): FromResultSet[A] = instance
  private def build[A](f: ResultSet => A): FromResultSet[A] = new FromResultSet[A] {
    def convert(rs: ResultSet): A = f(rs)
  }

  implicit val petFromRs: FromResultSet[Pet] =
    build { rs =>
      Pet(
        Some(rs.getLong("id")),
        rs.getString("name"),
        rs.getObject("birth_date", classOf[java.time.LocalDate]),
        rs.getLong("type_id"),
        rs.getLong("owner_id"))
    }

  implicit val petTypeFromRs: FromResultSet[PetType] =
    build { rs =>
      PetType(Some(rs.getLong("id")), rs.getString("name"))
    }

  implicit val ownerFromRs: FromResultSet[Owner] =
    build { rs =>
      Owner(
        Some(rs.getLong(OwnerRepo.Id)),
        rs.getString(OwnerRepo.FirstName) + " " + rs.getString(OwnerRepo.LastName),
        rs.getString(OwnerRepo.FirstName),
        rs.getString(OwnerRepo.LastName),
        rs.getString(OwnerRepo.Address),
        rs.getString(OwnerRepo.City),
        rs.getString(OwnerRepo.Telephone)
      )
    }
}
