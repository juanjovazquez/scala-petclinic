package petclinic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import cats.instances.future.catsStdInstancesForFuture
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("petclinic-as")
    implicit val materializer = ActorMaterializer()
    implicit val ec           = system.dispatcher
    Http().bindAndHandle(PetClinicService.route[Future], "localhost", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}
