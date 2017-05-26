package petclinic

final case class PetInfo(pet: Pet, petType: PetType, owner: Option[Owner])
final case class OwnerInfo(owner: Owner, pets: List[Pet])
