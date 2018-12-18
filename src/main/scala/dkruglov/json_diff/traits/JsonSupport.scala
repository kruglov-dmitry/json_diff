package dkruglov.json_diff.traits

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import dkruglov.json_diff.entities.{DataForComparison, DataRequestApi, DiffResult}
import dkruglov.json_diff.enums.{ResultType, Side}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, NullOptions, RootJsonFormat}

/**
  * Based on the code found: https://groups.google.com/forum/#!topic/spray-user/RkIwRIXzDDc
  */
class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
  override def write(obj: T#Value): JsValue = JsString(obj.toString)

  override def read(json: JsValue): T#Value = {
    json match {
      case JsString(txt) => enu.withName(txt)
      case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
    }
  }
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val sideEnumConverter = new EnumJsonConverter(Side)
  implicit val resultTypeEnumConverter = new EnumJsonConverter(ResultType)

  implicit val dataToCompareFormat = jsonFormat3(DataRequestApi)
  implicit val jsonDiffFormat = jsonFormat4(DiffResult)
  implicit val dataForCompareFormat = jsonFormat2(DataForComparison.apply)
}