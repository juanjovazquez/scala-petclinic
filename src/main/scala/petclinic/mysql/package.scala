package petclinic

import scala.concurrent.{ ExecutionContext, Future }
import scala.sys.process._
import scala.util.{ Failure, Success }
import scala.util.control.NonFatal
import java.sql.{ Connection, DriverManager, SQLException }

package object mysql {
  final val Ip       = "docker-machine ip".!!
  final val Driver   = "com.mysql.jdbc.Driver"
  final val Url      = s"jdbc:mysql://$Ip/petclinic"
  final val username = "root"
  final val password = "root"

  def withConnection[A](f: Connection => A)(implicit ec: ExecutionContext): Future[A] =
    Future {
      var connection: Connection = null
      val comp =
        try {
          connection = DriverManager.getConnection(Url, username, password)
          Success(f(connection))
        } catch {
          case NonFatal(e) => Failure(e)
        } finally {
          if (connection != null && !connection.isClosed)
            connection.close()
        }
      comp.get
    }

  object SQLException {
    def apply(msg: String) = new SQLException(msg)
  }
}
