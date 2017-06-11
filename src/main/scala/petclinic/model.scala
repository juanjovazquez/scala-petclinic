package petclinic

case class PetClinicError(
    msg: String = "Not expected error",
    httpErrorCode: Option[Int] = Some(500)
)

sealed abstract class BaseEntity extends Product with Serializable {
  def id: Option[Id]
}

sealed abstract class NamedEntity extends BaseEntity {
  def name: String
}

sealed abstract class PersonLike extends NamedEntity {
  def firstName: String
  def lastName: String
}

final case class Person(id: Option[Id], name: String, firstName: String, lastName: String)
    extends PersonLike

final case class Owner(
    id: Option[Id],
    name: String,
    firstName: String,
    lastName: String,
    address: String,
    city: String,
    telephone: String)
    extends PersonLike

final case class PetType(id: Option[Id], name: String) extends NamedEntity

final case class Pet(
    id: Option[Id],
    name: String,
    birthDate: java.time.LocalDate,
    petTypeId: Id,
    ownerId: Id)
    extends NamedEntity

final case class Speciality(id: Option[Id], name: String) extends NamedEntity

final case class Visit(id: Option[Id], date: java.time.LocalDate, description: String, pet: Pet)
    extends BaseEntity
