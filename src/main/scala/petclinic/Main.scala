package petclinic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.{ ToEntityMarshaller, ToResponseMarshaller }
import cats.MonadError
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
      def fmarshaller[A](
          implicit ma: ToEntityMarshaller[A],
          me: ToEntityMarshaller[PetClinicError]): ToResponseMarshaller[Response[A]] =
        implicitly
      val monadEv: MonadError[Response, PetClinicError] = implicitly
    }
}
