package dkruglov.json_diff.enums

object ResultType extends Enumeration {
  type ResultType = Value
  val EQUAL, NOT_EQUAL_DIFFERENT_SIZE, NOT_EQUAL_AT_OFFSET, ERROR, UNKNOWN = Value

  implicit def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(UNKNOWN)

}
