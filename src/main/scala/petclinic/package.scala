import cats.data.EitherT
import io.circe.{ Decoder, Encoder, Json }
import scala.concurrent.Future

package object petclinic {

  type Id = Long

  type Vet = Person

  type Response[A] = EitherT[Future, PetClinicError, A]

  private val dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // circe
  implicit val dateEncoder: Encoder[java.time.LocalDate] = new Encoder[java.time.LocalDate] {
    def apply(date: java.time.LocalDate): Json =
      Json.fromString(date.format(dtf))
  }

  implicit val dateDecoder: Decoder[java.time.LocalDate] =
    Decoder.decodeString.map(java.time.LocalDate.parse(_, dtf))
}
