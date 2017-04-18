package petclinic

sealed abstract class BaseEntity extends Product with Serializable {
  def id: Int
}

sealed abstract class NamedEntity extends BaseEntity {
  def name: String
}

sealed abstract class PersonLike extends NamedEntity {
  def firstName: String
  def lastName: String
}

final case class Person(id: Int, name: String, firstName: String, lastName: String)
    extends PersonLike

final case class Owner(
    id: Int,
    name: String,
    firstName: String,
    lastName: String,
    address: String,
    city: String,
    telephone: String)
    extends PersonLike

final case class PetType(id: Int, name: String) extends NamedEntity

final case class Pet(
    id: Int,
    name: String,
    birthDate: java.util.Date,
    petTypeId: Int,
    ownerId: Int)
    extends NamedEntity

final case class Speciality(id: Int, name: String) extends NamedEntity

final case class Visit(id: Int, date: java.util.Date, description: String, pet: Pet)
    extends BaseEntity
