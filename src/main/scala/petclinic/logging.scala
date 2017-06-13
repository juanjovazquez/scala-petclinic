package petclinic

import akka.event.LoggingAdapter
import petclinic.logging.AkkaLogger

import scala.language.higherKinds

trait Logger[F[_], Ctx] {
  def info(msg: String)(implicit ctx: Ctx): F[Unit]
}

object Logger {
  implicit def akkaLogger: Logger[Response, LoggingAdapter] =
    new AkkaLogger
}