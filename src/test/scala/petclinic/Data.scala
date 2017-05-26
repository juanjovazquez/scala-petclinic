package petclinic

import scala.collection.SortedMap
import scala.language.implicitConversions

trait Data {
  type Owners   = SortedMap[Long, Owner]
  type PetTypes = SortedMap[Long, PetType]
  type Pets     = SortedMap[Long, Pet]

  case class DB(owners: Owners, petTypes: PetTypes, pets: Pets)

  val petTypes: PetTypes =
    SortedMap(
      1L -> PetType(1, "cat"),
      2L -> PetType(2, "dog"),
      3L -> PetType(3, "lizard"),
      4L -> PetType(4, "snake"),
      5L -> PetType(5, "bird"),
      6L -> PetType(6, "hamster")
    )

  val owners: Owners =
    SortedMap(
      1L -> Owner(
        1,
        "George Franklin",
        "George",
        "Franklin",
        "110 W. Liberty St.",
        "Madison",
        "6085551023"),
      2L -> Owner(
        2,
        "Betty Davis",
        "Betty",
        "Davis",
        "638 Cardinal Ave.",
        "Sun Prairie",
        "6085551749"),
      3L -> Owner(
        3,
        "Eduardo Rodriguez",
        "Eduardo",
        "Rodriguez",
        "2693 Commerce St.",
        "McFarland",
        "6085558763"),
      4L -> Owner(
        4,
        "Harold Davis",
        "Harold",
        "Davis",
        "563 Friendly St.",
        "Windsor",
        "6085553198"),
      5L -> Owner(
        5,
        "Peter McTavish",
        "Peter",
        "McTavish",
        "2387 S. Fair Way",
        "Madison",
        "6085552765"),
      6L -> Owner(6, "Jean Coleman", "Jean", "Coleman", "105 N. Lake St.", "Monona", "6085552654"),
      7L -> Owner(7, "Jeff Black", "Jeff", "Black", "1450 Oak Blvd.", "Monona", "6085555387"),
      8L -> Owner(
        8,
        "Maria Escobito",
        "Maria",
        "Escobito",
        "345 Maple St.",
        "Madison",
        "6085557683"),
      9L -> Owner(
        9,
        "David Schroeder",
        "David",
        "Schroeder",
        "2749 Blackhawk Trail",
        "Madison",
        "6085559435"),
      10L -> Owner(
        10,
        "Carlos Estaban",
        "Carlos",
        "Estaban",
        "2335 Independence La.",
        "Waunakee",
        "6085555487")
    )

  val pets: Pets =
    SortedMap(
      1L  -> Pet(1, "Leo", "2000-09-07", 1, 1),
      2L  -> Pet(2, "Basil", "2002-08-06", 6, 2),
      3L  -> Pet(3, "Rosy", "2001-04-17", 2, 3),
      4L  -> Pet(4, "Jewel", "2000-03-07", 2, 3),
      5L  -> Pet(5, "Iggy", "2000-11-30", 3, 4),
      6L  -> Pet(6, "George", "2000-01-20", 4, 5),
      7L  -> Pet(7, "Samantha", "1995-09-04", 1, 6),
      8L  -> Pet(8, "Max", "1995-09-04", 1, 6),
      9L  -> Pet(9, "Lucky", "1999-08-06", 5, 7),
      10L -> Pet(10, "Mulligan", "1997-02-24", 2, 8),
      11L -> Pet(11, "Freddy", "2000-03-09", 5, 9),
      12L -> Pet(12, "Lucky", "2000-06-24", 2, 10),
      13L -> Pet(13, "Sly", "2002-06-08", 1, 10)
    )

  val initialDB = DB(owners, petTypes, pets)

  implicit def toDate(s: String): java.time.LocalDate = {
    val dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    java.time.LocalDate.parse(s, dtf)
  }

  implicit def IntToLongOption(x: Int): Option[Long] =
    Some(x.toLong)
}
