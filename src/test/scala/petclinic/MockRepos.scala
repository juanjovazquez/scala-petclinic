package petclinic

import cats.Id
import scala.collection.SortedMap
p
trait MockRepos {

  implicit val petRepo: PetRepo[Id] =
    new PetRepo[Id] {
      def findById(id: Int): Pet =
        pets(id)
      def save(pet: Pet): Unit = ???
      def findPetTypes: List[PetType] =
        petTypes.values.toList
      def findPetTypeById(petTypeId: Int): PetType = ???
    }

  implicit val ownerRepo: OwnerRepo[Id] =
    new OwnerRepo[Id] {
      def findById(id: Int): Owner =
        owners(id)
      def save(owner: Owner): Unit                      = ???
      def findByLastName(lastName: String): List[Owner] = ???
    }

  private implicit def toDate(s: String): java.util.Date = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    format.parse(s)
  }

  val petTypes: SortedMap[Int, PetType] =
    SortedMap(
      1 -> PetType(1, "cat"),
      2 -> PetType(2, "dog"),
      3 -> PetType(3, "lizard"),
      4 -> PetType(4, "snake"),
      5 -> PetType(5, "bird"),
      6 -> PetType(6, "hamster")
    )

  val owners: SortedMap[Int, Owner] =
    SortedMap(
      1 -> Owner(
        1,
        "George Franklin",
        "George",
        "Franklin",
        "110 W. Liberty St.",
        "Madison",
        "6085551023"),
      2 -> Owner(
        2,
        "Betty Davis",
        "Betty",
        "Davis",
        "638 Cardinal Ave.",
        "Sun Prairie",
        "6085551749"),
      3 -> Owner(
        3,
        "Eduardo Rodriguez",
        "Eduardo",
        "Rodriguez",
        "2693 Commerce St.",
        "McFarland",
        "6085558763"),
      4 -> Owner(
        4,
        "Harold Davis",
        "Harold",
        "Davis",
        "563 Friendly St.",
        "Windsor",
        "6085553198"),
      5 -> Owner(
        5,
        "Peter McTavish",
        "Peter",
        "McTavish",
        "2387 S. Fair Way",
        "Madison",
        "6085552765"),
      6 -> Owner(6, "Jean Coleman", "Jean", "Coleman", "105 N. Lake St.", "Monona", "6085552654"),
      7 -> Owner(7, "Jeff Black", "Jeff", "Black", "1450 Oak Blvd.", "Monona", "6085555387"),
      8 -> Owner(
        8,
        "Maria Escobito",
        "Maria",
        "Escobito",
        "345 Maple St.",
        "Madison",
        "6085557683"),
      9 -> Owner(
        9,
        "David Schroeder",
        "David",
        "Schroeder",
        "2749 Blackhawk Trail",
        "Madison",
        "6085559435"),
      10 -> Owner(
        10,
        "Carlos Estaban",
        "Carlos",
        "Estaban",
        "2335 Independence La.",
        "Waunakee",
        "6085555487")
    )

  val pets: SortedMap[Int, Pet] =
    SortedMap(
      1  -> Pet(1, "Leo", "2000-09-07", 1, 1),
      2  -> Pet(2, "Basil", "2002-08-06", 6, 2),
      3  -> Pet(3, "Rosy", "2001-04-17", 2, 3),
      4  -> Pet(4, "Jewel", "2000-03-07", 2, 3),
      5  -> Pet(5, "Iggy", "2000-11-30", 3, 4),
      6  -> Pet(6, "George", "2000-01-20", 4, 5),
      7  -> Pet(7, "Samantha", "1995-09-04", 1, 6),
      8  -> Pet(8, "Max", "1995-09-04", 1, 6),
      9  -> Pet(9, "Lucky", "1999-08-06", 5, 7),
      10 -> Pet(10, "Mulligan", "1997-02-24", 2, 8),
      11 -> Pet(11, "Freddy", "2000-03-09", 5, 9),
      12 -> Pet(12, "Lucky", "2000-06-24", 2, 10),
      13 -> Pet(13, "Sly", "2002-06-08", 1, 10)
    )
}
