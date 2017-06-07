package petclinic

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller, ToResponseMarshaller }
import akka.http.scaladsl.model.HttpResponse
import cats.data.EitherT
import scala.concurrent.Future

case object implicits {

  implicit def eitherTMarshaller[A](
    implicit ma: ToEntityMarshaller[A],
    me: ToEntityMarshaller[PetClinicError]): ToResponseMarshaller[EitherT[Future, PetClinicError, A]] =
    Marshaller(implicit ec =>
      _.value.flatMap {
        case Right(a) => ma.map(me => HttpResponse(entity = me))(a)
        case Left(e)  => me.map(me => HttpResponse(status = e.httpCode, entity = me))(e)
      }
    )
}
