package petclinic

import akka.event.LoggingAdapter
import akka.http.scaladsl.marshalling.{ToEntityMarshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import cats.MonadError
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.language.higherKinds

trait PetClinicService[F[_]] {

  implicit def fmarshaller[A](
      implicit ma: ToEntityMarshaller[A],
      me: ToEntityMarshaller[PetClinicError]): ToResponseMarshaller[F[A]]
  implicit def monadEv: MonadError[F, PetClinicError]
  implicit def logger: Logger[F, LoggingAdapter]

  def route(implicit petRepo: PetRepo[F], ownerRepo: OwnerRepo[F]): Route =
    pathPrefix("petTypes") {
      pathEndOrSingleSlash {
        get {
          extractLog { implicit log =>
            complete(logger.info("Finding all pet types") >> petRepo.findPetTypes)
          }
        }
      }
    } ~
    pathPrefix("pet" / LongNumber) { petId =>
      pathEndOrSingleSlash {
        get {
          val petInfo =
            petRepo.findById(petId).flatMap { pet =>
              val owner   = ownerRepo.findById(pet.ownerId)
              val petType = petRepo.findPetTypeById(pet.petTypeId)
              (petType |@| owner).map((pt, ow) => PetInfo(pet, pt, Some(ow)))
            }
          complete(petInfo)
        }
      }
    } ~
    pathPrefix("pet") {
      pathEndOrSingleSlash {
        post {
          entity(as[PetInfo]) { petInfo =>
            complete(petInfo)
          }
        }
      }
    } ~
    pathPrefix("owner") {
      pathEndOrSingleSlash {
        get {
          parameter('lastName.?) { lastName =>
            val default = monadEv.pure(List.empty[Owner])
            val owners  = lastName.map(ownerRepo.findByLastName).getOrElse(default)
            lastName match {
              case Some(ln) =>
                complete {
                  owners.ensure(
                    PetClinicError(
                      s"No owners found with lastName: $ln",
                      Option(StatusCodes.NotFound.intValue)
                    ))(_.nonEmpty)
                }
              case None =>
                complete(owners)
            }
          }
        } ~
        post {
          entity(as[Owner]) { owner =>
            complete(ownerRepo.save(owner))
          }
        } ~
        put {
          entity(as[Owner]) { owner =>
            complete(ownerRepo.update(owner))
          }
        }
      }
    } ~
    pathPrefix("owner" / LongNumber) { ownerId =>
      pathEndOrSingleSlash {
        get {
          val owner     = ownerRepo.findById(ownerId)
          val pets      = petRepo.findPetsByOwnerId(ownerId)
          val ownerInfo = (owner |@| pets).map(OwnerInfo)
          complete(ownerInfo)
        }
      }
    }
}
