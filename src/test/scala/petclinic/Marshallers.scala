package petclinic

import akka.http.scaladsl.marshalling.Marshaller
import cats.data.State

trait Marshallers {
  implicit def stateMarshaller[S, A, B](
      implicit m: Marshaller[A, B],
      initial: S
  ): Marshaller[State[S, A], B] =
    Marshaller(implicit ec =>
      s => {
        val (_, a) = s.run(initial).value
        m(a)
    })
}

object Marshallers extends Marshallers
