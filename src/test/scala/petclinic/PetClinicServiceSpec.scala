package petclinic

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ContentTypes.`application/json`
import cats.Id
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.scalatest.{ Matchers, WordSpec }

class PetClinicServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with MockRepos {

  val route = PetClinicService.route[Id]

  "PetClinicService" should {
    "return the pet types" in {
      Get("/petTypes") ~> route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldBe `application/json`
        entityAs[List[PetType]] shouldBe
        List(
          PetType(1, "cat"),
          PetType(2, "dog"),
          PetType(3, "lizard"),
          PetType(4, "snake"),
          PetType(5, "bird"),
          PetType(6, "hamster"))
      }
    }
    "return a pet by id" in {
      Get("/pet/1") ~> route ~> check {

      }
    }
  }
}
