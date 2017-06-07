package petclinic

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller, ToResponseMarshaller }
import akka.http.scaladsl.model.HttpResponse
import cats.data.EitherT
import scala.concurrent.Future

case object implicits {

  implicit def futureEitherMarshaller[A](
      implicit m1: ToEntityMarshaller[A],
      m2: ToEntityMarshaller[PetClinicError]): ToResponseMarshaller[Future[Either[PetClinicError, A]]] =
    Marshaller(implicit ec =>
      _.flatMap {
        case Right(a) => m1.map(me => HttpResponse(entity = me))(a)
        case Left(e)  => m2.map(me => HttpResponse(status = e.httpCode, entity = me))(e)
    })

  implicit def eitherTMarshaller[E, A](
      implicit m: ToEntityMarshaller[Future[Either[E, A]]]): ToEntityMarshaller[EitherT[Future, E, A]] =
    Marshaller(implicit ec => et => m(et.value))
}
