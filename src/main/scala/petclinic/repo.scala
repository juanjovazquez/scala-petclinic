package petclinic

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

sealed trait Repo[A, F[_]] {
  def findById(id: Long): F[A]
  def save(entity: A): F[Long]
  def update(entity: A): F[Unit]
}

trait OwnerRepo[F[_]] extends Repo[Owner, F] {
  def findByLastName(lastName: String): F[List[Owner]]
}

object OwnerRepo {
  def apply[F[_]](implicit instance: OwnerRepo[F]): OwnerRepo[F] = instance

  implicit def ownerRepoInstance(implicit ec: ExecutionContext): OwnerRepo[Response] =
    new mysql.OwnerRepo
}

trait PetRepo[F[_]] extends Repo[Pet, F] {
  def findPetTypes: F[List[PetType]]
  def findPetTypeById(petTypeId: Long): F[PetType]
  def findPetsByOwnerId(ownerId: Long): F[List[Pet]]
}

object PetRepo {
  def apply[F[_]](implicit instance: PetRepo[F]): PetRepo[F] = instance

  implicit def petRepoInstance(implicit ec: ExecutionContext): PetRepo[Response] =
    new mysql.PetRepo
}

trait VetRepo[F[_]] extends Repo[Vet, F] {
  def findAll: F[List[Vet]]
}

object VetRepo {
  def apply[F[_]](implicit instance: VetRepo[F]): VetRepo[F] = instance
}

trait VisitRepo[F[_]] extends Repo[Visit, F] {
  def findByPetId(petId: Long): F[List[Visit]]
}
