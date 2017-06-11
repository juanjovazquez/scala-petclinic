package petclinic

import cats.data.{ EitherT, State }
import cats.syntax.all._

trait MockRepos extends Data {

  // the monad stack for testing
  type DBAction[A] = EitherT[State[DB, ?], PetClinicError, A]
  object DBAction {
    def apply[A](s: State[DB, Either[PetClinicError, A]]): DBAction[A] =
      EitherT[State[DB, ?], PetClinicError, A](s)
  }

  final val NotFoundStandardError = PetClinicError("Not Found", httpErrorCode = Some(404))

  implicit val petRepo: PetRepo[DBAction] =
    new PetRepo[DBAction] {
      def findById(id: Long): DBAction[Pet] =
        inspect(_.pets.get(id).fold(NotFoundStandardError.asLeft[Pet])(_.asRight))

      def findPetTypes: DBAction[List[PetType]] =
        inspect(_.petTypes.values.toList.asRight)

      def findPetTypeById(petTypeId: Long): DBAction[PetType] =
        inspect(_.petTypes.get(petTypeId).fold(NotFoundStandardError.asLeft[PetType])(_.asRight))

      def findPetsByOwnerId(ownerId: Long): DBAction[List[Pet]] =
        inspect(_.pets.values.filter(_.ownerId == ownerId).toList.asRight)

      def save(pet: Pet): DBAction[Long] =
        DBAction {
          State(db => {
            val genId = db.pets.keys.max + 1
            (db.copy(pets = db.pets + (genId -> pet)), genId.asRight)
          })
        }

      def update(pet: Pet): DBAction[Unit] =
        DBAction {
          State(db =>
            (db.copy(pets = db.pets + (pet.id.get -> pet)), ().asRight))
        }
    }

  implicit val ownerRepo: OwnerRepo[DBAction] =
    new OwnerRepo[DBAction] {
      def findById(id: Long): DBAction[Owner] =
        inspect(_.owners.get(id).fold(NotFoundStandardError.asLeft[Owner])(_.asRight))

      def findByLastName(lastName: String): DBAction[List[Owner]] =
        get.map(_.owners.values.filter(_.lastName == lastName).toList)

      def save(owner: Owner): DBAction[Long] =
        DBAction {
          State(db => {
            val genId = db.owners.keys.max + 1
            (db.copy(owners = db.owners + (genId -> owner)), genId.asRight)
          })
        }

      def update(owner: Owner): DBAction[Unit] =
        DBAction {
          State(db =>
            (db.copy(owners = db.owners + (owner.id.get -> owner)), ().asRight))
        }
    }

  private def inspect[A](f: DB => Either[PetClinicError, A]): DBAction[A]  =
    EitherT[State[DB, ?], PetClinicError, A](State.inspect(f))

  private def get: DBAction[DB] =
    EitherT.right[State[DB, ?], PetClinicError, DB](State.get)
}