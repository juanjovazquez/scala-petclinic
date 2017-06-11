package petclinic

import cats.data.EitherT
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }
import scala.util.control.NonFatal
import scala.sys.process._
import java.sql.{ Connection, DriverManager }

package object mysql {

  final val Config = ConfigFactory.load()

  final val Ip =
    Try("docker-machine ip".!!).orElse(Try(Config.getString("mysql.ip"))).getOrElse("localhost")
  final val Driver   = "com.mysql.jdbc.Driver"
  final val Url      = s"jdbc:mysql://$Ip/petclinic"
  final val username = Try(Config.getString("mysql.username")).getOrElse("root")
  final val password = Try(Config.getString("mysql.password")).getOrElse("root")

  def withConnection[A](f: Connection => Either[PetClinicError, A])(
      implicit ec: ExecutionContext): Response[A] =
    EitherT(Future {
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
    })
}
