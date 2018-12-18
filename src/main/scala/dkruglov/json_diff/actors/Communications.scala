package dkruglov.json_diff.actors

import dkruglov.json_diff.entities.DataRequestApi


object Communications {
  case class AddNewData(dataToCompare: DataRequestApi)
  case class DiffEntries(queryId: String)
}
