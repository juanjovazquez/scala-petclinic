package petclinic
package mysql

import scala.concurrent.{ ExecutionContext, Future }
import java.sql.SQLException

final class OwnerRepo(implicit ec: ExecutionContext) extends petclinic.OwnerRepo[Future] {
  def findById(id: Int): Future[Owner] =
    withConnection { conn =>
      val sql =
        """select id, first_name, last_name, address, city, telephone
          |from owners
          |where id = ?""".stripMargin
      val statement = conn.prepareStatement(sql)
      statement.setInt(1, id)
      val resultSet = statement.executeQuery()
      if (resultSet.next)
        Owner(
          resultSet.getInt("id"),
          resultSet.getString("first_name") + " " + resultSet.getString("last_name"),
          resultSet.getString("first_name"),
          resultSet.getString("last_name"),
          resultSet.getString("address"),
          resultSet.getString("city"),
          resultSet.getString("telephone")
        )
      else
        throw new SQLException(s"Owner with id: $id not found")
    }

  def save(owner: Owner): Future[Unit] = ???

  def findByLastName(lastName: String): Future[List[Owner]] = ???
}
