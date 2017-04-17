package petclinic

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("petclinic-as")
    implicit val materializer = ActorMaterializer()
    implicit val ec           = system.dispatcher
    val bindingFuture         = Http().bindAndHandle(PetClinicService.route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
