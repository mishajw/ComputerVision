package util.filters

import grizzled.slf4j.Logging
import org.json.JSONObject
import util.Matrix

import scala.io.Source

object FilterFactory extends Logging{
	abstract class FilterType
	case class Sobel() extends FilterType
	case class Roberts() extends FilterType

	lazy val json = new JSONObject(Source.fromFile("src/main/resources/json/filters.json").mkString)

	def getFilter(filterType: FilterType) = filterType match {
		case Sobel() => new EdgeDetectionFilter(
			getArrayByName("sobelX"),
			getArrayByName("sobelY"))
		case Roberts() => new EdgeDetectionFilter(
			getArrayByName("robertsX"),
			getArrayByName("robertsY"))
	}

	def getArrayByName(s: String): Matrix = {
		var filter: Array[Double] = Array()
		var width: Int = _
		var height: Int = _

		val jsonMatrix = json.getJSONArray(s)
		width = jsonMatrix.length()

		for (i <- 0 until width) {
			val jsonRow = jsonMatrix.getJSONArray(i)
			height=  jsonRow.length()

			for (j <- 0 until height) {
				filter = filter :+ jsonRow.getInt(j)
			}
		}

		new Matrix(width, height, filter)
	}
}
