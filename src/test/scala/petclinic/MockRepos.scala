package petclinic

import cats.data.State
import cats.data.State.get

trait MockRepos extends Data {

  implicit val petRepo: PetRepo[State[DB, ?]] =
    new PetRepo[State[DB, ?]] {
      def findById(id: Int): State[DB, Pet] =
        get.map(_.pets(id))

      def findPetTypes: State[DB, List[PetType]] =
        get.map(_.petTypes.values.toList)

      def findPetTypeById(petTypeId: Int): State[DB, PetType] =
        get.map(_.petTypes(petTypeId))

      def save(entity: Pet): State[DB, Unit] = ???
    }

  implicit val ownerRepo: OwnerRepo[State[DB, ?]] =
    new OwnerRepo[State[DB, ?]] {
      def findById(id: Int): State[DB, Owner] =
        get.map(_.owners(id))

      def findByLastName(lastName: String): State[DB, List[Owner]] = ???
      def save(entity: Owner): State[DB, Unit]                     = ???
    }
}
