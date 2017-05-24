package petclinic

import cats.data.State
import cats.data.State.get

trait MockRepos extends Data {

  type DBAction[A] = State[DB, A]

  implicit val petRepo: PetRepo[DBAction] =
    new PetRepo[DBAction] {
      def findById(id: Long): DBAction[Pet] =
        get.map(_.pets(id))

      def findPetTypes: DBAction[List[PetType]] =
        get.map(_.petTypes.values.toList)

      def findPetTypeById(petTypeId: Long): DBAction[PetType] =
        get.map(_.petTypes(petTypeId))

      def findPetsByOwnerId(ownerId: Long): DBAction[List[Pet]] =
        get.map(_.pets.values.filter(_.ownerId == ownerId).toList)

      def save(pet: Pet): DBAction[Long] =
        get.transform {
          case (db, _) =>
            val genId = db.pets.keys.max + 1
            (db.copy(pets = db.pets + (genId -> pet)), genId)
        }
    }

  implicit val ownerRepo: OwnerRepo[DBAction] =
    new OwnerRepo[DBAction] {
      def findById(id: Long): DBAction[Owner] =
        get.map(_.owners(id))

      def findByLastName(lastName: String): DBAction[List[Owner]] =
        get.map(_.owners.values.filter(_.lastName == lastName).toList)

      def save(owner: Owner): DBAction[Long] =
        get.transform {
          case (db, _) =>
            val genId = db.owners.keys.max + 1
            (db.copy(owners = db.owners + (genId -> owner)), genId)
        }
    }
}
