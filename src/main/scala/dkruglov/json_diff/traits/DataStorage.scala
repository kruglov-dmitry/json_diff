package dkruglov.json_diff.traits

import dkruglov.json_diff.entities.{DataForComparison, DataRequestApi, DiffResult}
import dkruglov.json_diff.enums.ResultType._


trait DataStorage {
  protected val dataEntries = scala.collection.mutable.Map[String, DataForComparison]()

  def updateEntry(newEntry: DataRequestApi) =
    if (dataEntries.contains(newEntry.queryId))
      dataEntries(newEntry.queryId) = DataForComparison.updateSideValue(newEntry, dataEntries(newEntry.queryId))
    else
      dataEntries(newEntry.queryId) = DataForComparison.fromDataToCompare(newEntry)


  def compareEntries(queryId: String) = {
    if (dataEntries.contains(queryId)) {
      val dataForCompare = dataEntries(queryId)

      val left = dataForCompare.left
      val right = dataForCompare.right

      val lengthDiff = Math.abs(left.length - right.length)

      if (lengthDiff != 0)
        DiffResult(NOT_EQUAL_DIFFERENT_SIZE, lengthDiff, -1, "Lengths are not equal")
      else {

        val idxOfFirstDiff = left.indices.find(i => left(i) != right(i))

        if (idxOfFirstDiff.isDefined)
          DiffResult(NOT_EQUAL_AT_OFFSET, 0, idxOfFirstDiff.get, s"Files are different starting from ${idxOfFirstDiff.get} th byte")
        else
          DiffResult(EQUAL, 0, 0, "Files are Equal")

      }
    } else
      DiffResult(ERROR, -1, -1, s"Can't find matched data for this queryId: $queryId")
  }

}
