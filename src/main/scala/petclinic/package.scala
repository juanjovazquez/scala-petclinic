import io.circe.{ Decoder, Encoder, Json }
import java.util.Date

package object petclinic {
  //type Seq[+A] = scala.collection.immutable.Seq[A]
  //val Seq = scala.collection.immutable.Seq
  type Vet = Person

  def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")

  // circe
  implicit val dateEncoder: Encoder[Date] = new Encoder[Date] {
    def apply(date: Date): Json =
      Json.fromString(sdf.format(date))

  }

  implicit val dateDecoder: Decoder[Date] =
    Decoder.decodeString.map(sdf.parse(_))
}
