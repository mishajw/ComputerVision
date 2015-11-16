package vision.filters

import grizzled.slf4j.Logging
import org.json.JSONObject
import vision.util.Matrix

import scala.io.Source

object FilterFactory extends Logging {

	type Mask = Matrix[Double]

	abstract class FilterType
	case class Sobel() extends FilterType
	case class Roberts() extends FilterType
	case class Gaussian() extends FilterType

	lazy val json = new JSONObject(Source.fromFile("src/main/resources/json/filters.json").mkString)

	/**
	 * Get a filter by it's type
	 * @param filterType
	 * @return
	 */
	def getFilter(filterType: FilterType) = filterType match {
		case Sobel() => new EdgeDetectionFilter(
			getArrayByName("sobelX"),
			getArrayByName("sobelY"))
		case Roberts() => new EdgeDetectionFilter(
			getArrayByName("robertsX"),
			getArrayByName("robertsY"))
			case Gaussian() => new SimpleFilter(
				getArrayByName("gaussian"))
	}

	/**
	 * Get an array by it's string name
	 * @param s name
	 * @return Mask from JSON
	 */
	def getArrayByName(s: String): Mask = {
		var filter: Array[Double] = Array()
		var width = 0
		var height = 0

		val jsonMatrix = json.getJSONArray(s)
		width = jsonMatrix.length()

		for (i <- 0 until width) {
			val jsonRow = jsonMatrix.getJSONArray(i)
			height = jsonRow.length()

			for (j <- 0 until height) {
				filter = filter :+ jsonRow.getDouble(j)
			}
		}

		new Mask(filter, width, height)
	}
}
