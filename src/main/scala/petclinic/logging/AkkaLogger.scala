package petclinic
package logging

import akka.event.LoggingAdapter
import cats.data.EitherT
import cats.syntax.either._
import scala.concurrent.Future

final class AkkaLogger extends Logger[Response, LoggingAdapter] {
  def info(msg: String)(implicit ctx: LoggingAdapter): Response[Unit] =
    EitherT(Future.successful(ctx.info(msg).asRight[PetClinicError]))
}
