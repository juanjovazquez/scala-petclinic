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
        rs.getInt("id"),
        rs.getString("name"),
        rs.getDate("birth_date"),
        rs.getInt("type_id"),
        rs.getInt("owner_id"))
    }

  implicit val petTypeFromRs: FromResultSet[PetType] =
    build { rs =>
      PetType(rs.getInt("id"), rs.getString("name"))
    }

  implicit val ownerFromRs: FromResultSet[Owner] =
    build { rs =>
      Owner(
        rs.getInt("id"),
        rs.getString("first_name") + " " + rs.getString("last_name"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getString("address"),
        rs.getString("city"),
        rs.getString("telephone")
      )
    }
}
