package vision.filters

//import breeze.stats.distributions.Gaussian
import grizzled.slf4j.Logging
import org.json.JSONObject
import vision.analysis.Operations._
import vision.util.Matrix

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object FilterFactory extends Logging {

	type Mask = Matrix[Double]

	lazy val json = new JSONObject(Source.fromFile("src/main/resources/json/filters.json").mkString)

	/**
		* Get a filter by its type
		* @param filterType The type of filter
		* @return actual filter object
		*/
	def getFilter(filterType: FilterOperation) = filterType match {
		case Sobel => new EdgeDetectionFilter(
			getArrayByName("sobelX"),
			getArrayByName("sobelY"))
		case Roberts => new EdgeDetectionFilter(
			getArrayByName("robertsX"),
			getArrayByName("robertsY"))
		case Gaussian(size, sd) => new SimpleFilter(
			/*getArrayByName("gaussian")*/getGaussianImage(size, sd))
		case Prewitt => new EdgeDetectionFilter(
			getArrayByName("prewittX"),
			getArrayByName("prewittY"))
	}

	/**
		* Get an array by it's string name
		* @param s name
		* @return Mask from JSON
		*/
	def getArrayByName(s: String): Mask = {
		var filter: ArrayBuffer[Double] = new ArrayBuffer[Double]()
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

	def getGaussianImage(size: Int, sd: Double): Mask = {

    val gaussian = breeze.stats.distributions.Gaussian(0, sd)
    val step = 2d / size.asInstanceOf[Double]
    var filter = new ArrayBuffer[Double]()

    for (x <- -1.0 to (1.0, step); y <- -1.0 to (1.0, step)) {
      filter = filter :+ gaussian.cdf(x) * gaussian.cdf(x)
    }

    new Mask(filter, size, size)
	}
}
