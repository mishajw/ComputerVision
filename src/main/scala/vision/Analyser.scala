package vision

import breeze.linalg._
import breeze.plot._
import grizzled.slf4j.Logging
import vision.util.ImageWrapper

object Analyser extends Logging {
	def analyse(startImage: ImageWrapper, sampleImage: ImageWrapper, editFunction: (ImageWrapper, Double) => ImageWrapper) = {
		var points: List[(Double, Double, Double)] = List()

		for (i <- 0.0 to (1.0, 0.05)) {
			val editedImage = editFunction(startImage, i)
			val results = editedImage.checkValidity(sampleImage)
			points = points :+ (i, results.sensitivity, results.specificity)
		}

		info(points)

		drawResults(points)

		points.sortBy(p => Math.sqrt(
				Math.pow(1 - p._2, 2) +
				Math.pow(1 - p._3, 2)
		)).head._1
	}

	def drawResults(points: List[(Double, Double, Double)]) = {
		val f = Figure()
		val p = f.subplot(0)
		val x = DenseVector(points.map(_._2).toArray)
		val y = DenseVector(points.map(_._3).toArray)
		info(x)
		p += plot(x, y)
		p.xlabel = "Sensitivity"
		p.ylabel = "Specificity"
		f.saveas("lines.png")
	}
}
