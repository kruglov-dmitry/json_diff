package dkruglov.json_diff.enums

object Side extends Enumeration {
  type Side = Value
  val LEFT, RIGHT, UNKNOWN = Value

  implicit def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(UNKNOWN)

}
