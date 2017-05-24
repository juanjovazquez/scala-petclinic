package petclinic

import akka.http.scaladsl.marshalling.Marshaller
import cats.data.State

trait Marshallers {

  implicit def stateMarshaller[S, A, B](initial: S)(onResponse: S => Unit)(
      implicit m: Marshaller[A, B]): Marshaller[State[S, A], B] =
    Marshaller(implicit ec =>
      s => {
        val (s2, a) = s.run(initial).value
        onResponse(s2) // side effect
        m(a)
    })
}

object Marshallers extends Marshallers
