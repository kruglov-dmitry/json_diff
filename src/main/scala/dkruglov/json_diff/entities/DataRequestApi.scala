package dkruglov.json_diff.entities

import dkruglov.json_diff.enums.Side.Side

case class DataRequestApi(queryId: String, side: Side, data: String)
