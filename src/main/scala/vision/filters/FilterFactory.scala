package vision.filters

//import breeze.stats.distributions.Gaussian
import grizzled.slf4j.Logging
import org.json.JSONObject
import vision.util.Matrix

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object FilterFactory extends Logging {

	type Mask = Matrix[Double]

	abstract sealed class FilterType
	case object FilterSobel extends FilterType
	case object FilterRoberts extends FilterType
	case class  FilterGaussian(size: Int) extends FilterType
	case object FilterPrewitt extends FilterType

	lazy val json = new JSONObject(Source.fromFile("src/main/resources/json/filters.json").mkString)

	/**
		* Get a filter by its type
		* @param filterType The type of filter
		* @return actual filter object
		*/
	def getFilter(filterType: FilterType) = filterType match {
		case FilterSobel => new EdgeDetectionFilter(
			getArrayByName("sobelX"),
			getArrayByName("sobelY"))
		case FilterRoberts => new EdgeDetectionFilter(
			getArrayByName("robertsX"),
			getArrayByName("robertsY"))
		case FilterGaussian(size) => new SimpleFilter(
			/*getArrayByName("gaussian")*/getGaussianImage(size))
		case FilterPrewitt => new EdgeDetectionFilter(
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

	def getGaussianImage(size: Int): Mask = {

    val gaussian = breeze.stats.distributions.Gaussian(0, 1)
    val step = 2d / size.asInstanceOf[Double]
    var filter = new ArrayBuffer[Double]()

    for (x <- -1.0 to (1.0, step); y <- -1.0 to (1.0, step)) {
      filter = filter :+ gaussian.cdf(x) * gaussian.cdf(x)
    }

    new Mask(filter, size, size)
	}
}
