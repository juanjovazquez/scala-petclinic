package petclinic

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ContentTypes.`application/json`
import cats.Monad
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.scalatest.{ Matchers, WordSpec }
import petclinic.Marshallers._

class PetClinicServiceSpec extends WordSpec with Matchers with ScalatestRouteTest with MockRepos {

  private[this] val service = new PetClinicService[DBAction] {
    def fmarshaller[A, B](implicit m: Marshaller[A, B]): Marshaller[DBAction[A], B] = implicitly
    val monadEv: Monad[DBAction]                                                    = implicitly
  }
  private[this] val route = service.route

  "PetClinicService" should {
    "return the pet types" in {
      Get("/petTypes") ~> route ~> check {
        checkResponseOk
        entityAs[List[PetType]] shouldBe
        List(
          PetType(1, "cat"),
          PetType(2, "dog"),
          PetType(3, "lizard"),
          PetType(4, "snake"),
          PetType(5, "bird"),
          PetType(6, "hamster")
        )
      }
    }

    "return a pet by id" in {
      Get("/pet/1") ~> route ~> check {
        checkResponseOk
        entityAs[PetInfo] shouldBe PetInfo(
          Pet(1, "Leo", "2000-09-07", 1, 1),
          PetType(1, "cat"),
          Owner(
            1,
            "George Franklin",
            "George",
            "Franklin",
            "110 W. Liberty St.",
            "Madison",
            "6085551023"
          )
        )
      }
    }

    "return an owner by id" in {
      Get("/owner/1") ~> route ~> check {
        checkResponseOk
        entityAs[OwnerInfo] shouldBe
        OwnerInfo(
          Owner(
            1,
            "George Franklin",
            "George",
            "Franklin",
            "110 W. Liberty St.",
            "Madison",
            "6085551023"
          ),
          List(Pet(1, "Leo", "2000-09-07", 1, 1))
        )
      }
    }

    "return owners by lastName" in {
      Get("/owner?lastName=Davis") ~> route ~> check {
        checkResponseOk
        entityAs[List[Owner]] shouldBe
        List(
          Owner(
            2,
            "Betty Davis",
            "Betty",
            "Davis",
            "638 Cardinal Ave.",
            "Sun Prairie",
            "6085551749"
          ),
          Owner(
            4,
            "Harold Davis",
            "Harold",
            "Davis",
            "563 Friendly St.",
            "Windsor",
            "6085553198"
          )
        )
      }
    }

    "return an empty list of owners when the parameter `lastName` is missing" in {
      Get("/owner") ~> route ~> check {
        checkResponseOk
        entityAs[List[Owner]] shouldBe List()
      }
    }
  }

  private[this] def checkResponseOk = {
    status shouldBe StatusCodes.OK
    contentType shouldBe `application/json`
  }
}
