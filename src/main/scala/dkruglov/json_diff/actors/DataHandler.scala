package dkruglov.json_diff.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import dkruglov.json_diff.actors.Communications.{AddNewData, DiffEntries}
import dkruglov.json_diff.entities.DataRequestApi
import dkruglov.json_diff.traits.DataStorage

import scala.concurrent.Future

class DataHandler extends DataStorage with Actor with ActorLogging {

  import context.dispatcher

  def receive = {
    case AddNewData(credentials) => AddNewDataImpl(credentials) pipeTo sender
    case DiffEntries(queryId) => DiffEntriesImpl(queryId) pipeTo sender
  }

  def AddNewDataImpl(dataToCompare: DataRequestApi) = Future { updateEntry(dataToCompare) }
  def DiffEntriesImpl(queryId: String) = Future { compareEntries(queryId) }

}
