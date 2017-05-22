package petclinic

import cats.data.State
import cats.data.State.{ get, modify }

trait MockRepos extends Data {

  type DBAction[A] = State[DB, A]

  implicit val petRepo: PetRepo[DBAction] =
    new PetRepo[DBAction] {
      def findById(id: Int): DBAction[Pet] =
        get.map(_.pets(id))

      def findPetTypes: DBAction[List[PetType]] =
        get.map(_.petTypes.values.toList)

      def findPetTypeById(petTypeId: Int): DBAction[PetType] =
        get.map(_.petTypes(petTypeId))

      def save(pet: Pet): DBAction[Unit] =
        modify { db =>
          db.copy(
            pets = db.pets + (pet.id -> pet)
          )
        }
    }

  implicit val ownerRepo: OwnerRepo[DBAction] =
    new OwnerRepo[DBAction] {
      def findById(id: Int): DBAction[Owner] =
        get.map(_.owners(id))

      def findByLastName(lastName: String): DBAction[List[Owner]] =
        get.map(_.owners.values.filter(_.lastName == lastName).toList)

      def save(owner: Owner): DBAction[Unit] =
        modify { db =>
          db.copy(
            owners = db.owners + (owner.id -> owner)
          )
        }
    }
}
