import io.circe.{ Encoder, Json }
import java.util.Date

package object petclinic {
  type Seq[+A] = scala.collection.immutable.Seq[A]
  val Seq = scala.collection.immutable.Seq
  type Vet = Person

  // circe
  implicit val dateEncoder = new Encoder[Date] {
    def apply(date: Date): Json = {
      val sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")
      Json.fromString(sdf.format(date))
    }
  }
}
