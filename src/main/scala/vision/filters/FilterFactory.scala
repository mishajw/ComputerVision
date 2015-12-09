package vision.filters

//import breeze.stats.distributions.Gaussian
import grizzled.slf4j.Logging
import org.json.JSONObject
import vision.analysis.Operations._
import vision.util.Matrix

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object FilterFactory extends Logging {

	lazy val json = new JSONObject(Source.fromFile("src/main/resources/json/filters.json").mkString)

	/**
		* Get a filter by its type
		* @param filterType The type of filter
		* @return actual filter object
		*/
	def getFilter(filterType: FilterOperation) = filterType match {
		case SimpleGradient => new XYFilter(
				getArrayByName("simpleX"),
				getArrayByName("simpleY"))
		case Sobel => new XYFilter(
			getArrayByName("sobelX"),
			getArrayByName("sobelY"))
		case Roberts => new XYFilter(
			getArrayByName("robertsX"),
			getArrayByName("robertsY"))
		case Prewitt => new XYFilter(
			getArrayByName("prewittX"),
			getArrayByName("prewittY"))
		case Laplacian => new SimpleFilter(getArrayByName("laplacian"))
		case SimpleMean(size) => new SimpleFilter(getMeanFilter(size))
		case Gaussian(size, sd) => new SimpleFilter(getGaussianImage(size, sd))
	}

	/**
		* Get an array by it's string name
		* @param s name
		* @return Matrix from JSON
		*/
	def getArrayByName(s: String): Matrix = {
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

		new Matrix(filter.toArray, width, height)
	}

	/**
		* Get a gaussian filter
		* @param size Size of the filter
		* @param sd Standard deviation of the Gaussian call
		* @return Gaussian filter
		*/
	def getGaussianImage(size: Int, sd: Double): Matrix = {
		val k = (size / 2) - 1

		new Matrix((for (x <- 0 until size; y <- 0 until size)
			yield (1 / (2 * Math.PI * sd * sd)) *
				Math.exp(
					-(Math.pow(x - k - 1, 2) + Math.pow(y - k - 1, 2)) / (2 * sd * sd))).toArray,
		size, size)




//		val gaussian = breeze.stats.distributions.Gaussian(0, sd)
//		val step = 2d / size.asInstanceOf[Double]
//		var filter = new ArrayBuffer[Double]()
//
//		for (x <- -1.0 to (1.0, step); y <- -1.0 to (1.0, step)) {
//			filter = filter :+ gaussian.cdf(x) * gaussian.cdf(y)
//		}
//
//		new Matrix(filter.toArray, size, size)
	}

	/**
		* Get a filter for simple smoothing
		* @param size
		* @return
		*/
	def getMeanFilter(size: Int): Matrix = {
		new Matrix(Array.fill(size * size)(1 / (size.toDouble * size.toDouble)), size, size)
	}
}
