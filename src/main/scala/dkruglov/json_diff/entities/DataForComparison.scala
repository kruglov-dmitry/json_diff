package dkruglov.json_diff.entities

import dkruglov.json_diff.enums.Side

case class DataForComparison(left: String, right: String)

object DataForComparison {

  implicit def fromDataToCompare(d: DataRequestApi): DataForComparison = d.side match {
    case Side.LEFT => DataForComparison(d.data, "")
    case Side.RIGHT => DataForComparison("", d.data)
  }

  implicit def updateSideValue(d: DataRequestApi, otherValue: DataForComparison): DataForComparison = d.side match {
    case Side.LEFT => DataForComparison(d.data, otherValue.right)
    case Side.RIGHT => DataForComparison(otherValue.left, d.data)
  }
}
