package dkruglov.json_diff.utils

import java.nio.file.{Files, Paths}
import java.util.Base64

object Utils {
  def loadFile(fileName: String): Array[Byte] = Files.readAllBytes(Paths.get(fileName))
  def toBase64(byteArray: Array[Byte]): String = Base64.getEncoder.encodeToString(byteArray)
  def fromBase64(stringRepr: String): Array[Byte] = Base64.getDecoder.decode(stringRepr)
}
