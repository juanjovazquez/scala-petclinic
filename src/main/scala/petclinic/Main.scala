package petclinic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshaller
import cats.Monad
import cats.implicits._
import petclinic.implicits._
import scala.concurrent.ExecutionContext

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("petclinic-as")
    implicit val materializer = ActorMaterializer()
    implicit val ec           = system.dispatcher
    Http().bindAndHandle(service().route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}

object service {
  def apply()(implicit ec: ExecutionContext): PetClinicService[Response] =
    new PetClinicService[Response] {
      def fmarshaller[A, B](
          implicit m: Marshaller[Either[PetClinicError, A], B]): Marshaller[Response[A], B] =
        implicitly
      val monadEv: Monad[Response] = implicitly
    }
}
