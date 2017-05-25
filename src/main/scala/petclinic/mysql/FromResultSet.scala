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
        Some(rs.getLong(PetRepo.Id)),
        rs.getString(PetRepo.Name),
        rs.getObject(PetRepo.BirthDate, classOf[java.time.LocalDate]),
        rs.getLong(PetRepo.TypeId),
        rs.getLong(PetRepo.OwnerId))
    }

  implicit val petTypeFromRs: FromResultSet[PetType] =
    build { rs =>
      PetType(Some(rs.getLong(PetRepo.Id)), rs.getString(PetRepo.Name))
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
