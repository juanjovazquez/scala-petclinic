package petclinic

sealed abstract class BaseEntity extends Product with Serializable {
  def id: Long
}

sealed abstract class NamedEntity extends BaseEntity {
  def name: String
}

sealed abstract class PersonLike extends NamedEntity {
  def firstName: String
  def lastName: String
}

final case class Person(id: Long, name: String, firstName: String, lastName: String)
    extends PersonLike

final case class Owner(
    id: Long,
    name: String,
    firstName: String,
    lastName: String,
    address: String,
    city: String,
    telephone: String)
    extends PersonLike

final case class PetType(id: Long, name: String) extends NamedEntity

final case class Pet(
    id: Long,
    name: String,
    birthDate: java.util.Date,
    petTypeId: Long,
    ownerId: Long)
    extends NamedEntity

final case class Speciality(id: Long, name: String) extends NamedEntity

final case class Visit(id: Long, date: java.util.Date, description: String, pet: Pet)
    extends BaseEntity
