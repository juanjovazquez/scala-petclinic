package petclinic

import scala.language.higherKinds
import scala.concurrent.{ ExecutionContext, Future }

sealed trait Repo[A, F[_]] {
  def findById(id: Int): F[A]
  def save(entity: A): F[Unit]
}

trait OwnerRepo[F[_]] extends Repo[Owner, F] {
  def findByLastName(lastName: String): F[List[Owner]]
}

object OwnerRepo {
  def apply[F[_]](implicit instance: OwnerRepo[F]): OwnerRepo[F] = instance

  implicit def ownerRepoInstance(implicit ec: ExecutionContext): OwnerRepo[Future] =
    new mysql.OwnerRepo
}

trait PetRepo[F[_]] extends Repo[Pet, F] {
  def findPetTypes: F[List[PetType]]
  def findPetTypeById(petTypeId: Int): F[PetType]
}

object PetRepo {
  def apply[F[_]](implicit instance: PetRepo[F]): PetRepo[F] = instance

  implicit def petRepoInstance(implicit ec: ExecutionContext): PetRepo[Future] =
    new mysql.PetRepo
}

trait VetRepo[F[_]] extends Repo[Vet, F] {
  def findAll: F[List[Vet]]
}

object VetRepo {
  def apply[F[_]](implicit instance: VetRepo[F]): VetRepo[F] = instance
}

trait VisitRepo[F[_]] extends Repo[Visit, F] {
  def findByPetId(petId: Int): F[List[Visit]]
}
