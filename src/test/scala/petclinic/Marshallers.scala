package petclinic

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.marshalling.{ ToEntityMarshaller, ToResponseMarshaller }
import cats.data.{ EitherT, State }

trait Marshallers {

  def dbActionMarshaller[S, A](initial: S)(onResponse: S => Unit)(
      implicit ma: ToEntityMarshaller[A],
      me: ToEntityMarshaller[PetClinicError])
    : ToResponseMarshaller[EitherT[State[S, ?], PetClinicError, A]] =
    Marshaller(implicit ec =>
      s => {
        val (s2, r) = s.value.run(initial).value
        onResponse(s2)
        r match {
          case Right(a) =>
            ma.map(me => HttpResponse(entity = me))(a)
          case Left(e) =>
            me.map(
              me =>
                e.httpErrorCode
                  .map(
                    code => HttpResponse(status = code, entity = me)
                  )
                  .getOrElse(HttpResponse(entity = me)))(e)
        }
    })
}

object Marshallers extends Marshallers
