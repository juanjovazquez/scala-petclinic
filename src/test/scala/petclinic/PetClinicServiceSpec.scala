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

  "PetClinicService" should {
    "return the pet types" in {
      Get("/petTypes") ~> service.route ~> check {
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
      Get("/pet/1") ~> service.route ~> check {
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

    "return an owner with its pets by id" in {
      Get("/owner/1") ~> service.route ~> check {
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
      Get("/owner?lastName=Davis") ~> service.route ~> check {
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
      Get("/owner") ~> service.route ~> check {
        checkResponseOk
        entityAs[List[Owner]] shouldBe List()
      }
    }

    "save a new owner" in {
      var initialState = initialDB
      Post(
        "/owner",
        Owner(
          id = None,
          "Sam Schultz",
          "Sam",
          "Schultz",
          "4, Evans Street",
          "Wollongong",
          "4444444444"
        )) ~> service(initialState) { initialState = _ }.route ~> check {
        checkResponseOk
        val expectedOwnerId =
        initialDB.owners.keys.max + 1
        entityAs[Long] shouldBe expectedOwnerId
      }
      Get("/owner?lastName=Schultz") ~> service(initialState)().route ~> check {
        checkResponseOk
        entityAs[List[Owner]].length shouldBe 1
      }
    }

    "update an existing owner" in {
      var initialState = initialDB
      Put(
        "/owner",
        Owner(
          id = Some(1),
          "Sam Changed",
          "Sam",
          "Changed",
          "4, New Evans Street",
          "Wollongong",
          "4444444444"
        )) ~> service(initialState) { initialState = _ }.route ~> check {
        checkResponseOk
      }
      Get("/owner/1") ~> service(initialState)().route ~> check {
        checkResponseOk
        entityAs[OwnerInfo].owner.name shouldBe "Sam Changed"
      }
    }
  }

  private[this] def service: PetClinicService[DBAction] = service()()

  private[this] def service(
      initialState: DB = initialDB
  )(onResponse: DB => Unit = _ => ()): PetClinicService[DBAction] =
    new PetClinicService[DBAction] {
      def fmarshaller[A, B](implicit m: Marshaller[A, B]): Marshaller[DBAction[A], B] =
        stateMarshaller(initialState)(onResponse)
      val monadEv: Monad[DBAction] = implicitly
    }

  private[this] def checkResponseOk = {
    status shouldBe StatusCodes.OK
    contentType shouldBe `application/json`
  }
}
