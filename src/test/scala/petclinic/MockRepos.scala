package petclinic

import cats.data.State
import cats.data.State.{ get, modify }

trait MockRepos extends Data {

  type DBTran[A] = State[DB, A]

  implicit val petRepo: PetRepo[DBTran] =
    new PetRepo[DBTran] {
      def findById(id: Int): DBTran[Pet] =
        get.map(_.pets(id))

      def findPetTypes: DBTran[List[PetType]] =
        get.map(_.petTypes.values.toList)

      def findPetTypeById(petTypeId: Int): DBTran[PetType] =
        get.map(_.petTypes(petTypeId))

      def save(pet: Pet): DBTran[Unit] =
        modify { db =>
          db.copy(
            pets = db.pets + (pet.id -> pet)
          )
        }
    }

  implicit val ownerRepo: OwnerRepo[DBTran] =
    new OwnerRepo[DBTran] {
      def findById(id: Int): DBTran[Owner] =
        get.map(_.owners(id))

      def findByLastName(lastName: String): DBTran[List[Owner]] =
        get.map(_.owners.values.filter(_.lastName == lastName).toList)

      def save(owner: Owner): DBTran[Unit] =
        modify { db =>
          db.copy(
            owners = db.owners + (owner.id -> owner)
          )
        }
    }
}
