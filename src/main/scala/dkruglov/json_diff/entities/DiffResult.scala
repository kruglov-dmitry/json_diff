package dkruglov.json_diff.entities

import dkruglov.json_diff.enums.ResultType.ResultType

case class DiffResult(resultType: ResultType, sizeDiff: Long, offset: Long, msg: String)
