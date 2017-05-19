package petclinic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.GenericMarshallers.futureMarshaller
import akka.http.scaladsl.marshalling.Marshaller
import cats.Monad
import cats.implicits._
import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("petclinic-as")
    implicit val materializer = ActorMaterializer()
    implicit val ec           = system.dispatcher
    val service = new PetClinicService[Future] {
      def fmarshaller[A, B](implicit m: Marshaller[A, B]): Marshaller[Future[A], B] = implicitly
      val monadEv: Monad[Future]                                                    = implicitly
    }
    Http().bindAndHandle(service.route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}
