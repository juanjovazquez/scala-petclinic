package petclinic

import akka.http.scaladsl.marshalling.Marshaller
import cats.data.EitherT
import scala.concurrent.Future

case object implicits {

  implicit def futureEitherMarshaller[E, A, B](
      implicit m1: Marshaller[A, B],
      m2: Marshaller[E, B]): Marshaller[Future[Either[E, A]], B] =
    Marshaller(implicit ec =>
      _.flatMap {
        case Right(a) => m1(a)
        case Left(e)  => m2(e)
    })

  implicit def eitherTMarshaller[E, A, B](
      implicit m: Marshaller[Future[Either[E, A]], B]): Marshaller[EitherT[Future, E, A], B] =
    Marshaller(implicit ec => et => m(et.value))

}
