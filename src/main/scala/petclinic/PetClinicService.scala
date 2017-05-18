package petclinic

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.Monad
import cats.implicits._
import scala.language.higherKinds

trait PetClinicService {
  def route[F[_]: Monad](
      implicit petRepo: PetRepo[F],
      ownerRepo: OwnerRepo[F],
      ownerMarsh: ToResponseMarshaller[F[Owner]],
      petTypesMarsh: ToResponseMarshaller[F[List[PetType]]],
      petMarsh: ToResponseMarshaller[F[PetInfo]]): Route =
    pathPrefix("petTypes") {
      pathEndOrSingleSlash {
        get {
          complete(petRepo.findPetTypes)
        }
      }
    } ~
    pathPrefix("pet" / IntNumber) { petId =>
      pathEndOrSingleSlash {
        get {
          val petInfo =
            petRepo.findById(petId).flatMap { pet =>
              val owner   = ownerRepo.findById(pet.ownerId)
              val petType = petRepo.findPetTypeById(pet.petTypeId)
              (petType, owner).map2(PetInfo(pet, _, _))
            }
          complete(petInfo)
        }
      }
    } ~
    pathPrefix("owner" / IntNumber) { ownerId =>
      pathEndOrSingleSlash {
        get {
          complete(ownerRepo.findById(ownerId))
        }
      }
    }
}

object PetClinicService extends PetClinicService
