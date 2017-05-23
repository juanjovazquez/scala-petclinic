package petclinic
package mysql

import java.sql.ResultSet

object implicits {
  implicit class ResultSetOps(rs: ResultSet) {
    def toEntityList[A: FromResultSet]: List[A] = FromResultSet[A].toEntityList(rs)
    def toEntity[A: FromResultSet]: Option[A]   = FromResultSet[A].toEntity(rs)
  }
}
