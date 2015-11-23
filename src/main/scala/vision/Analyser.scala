package vision

import grizzled.slf4j.Logging
import vision.util.ImageWrapper

object Analyser extends Logging {
	def analyse(startImage: ImageWrapper, sampleImage: ImageWrapper, editFunction: (ImageWrapper, Double) => ImageWrapper) = {
		var points: List[(Double, Double)] = List()

		for (i <- 0.0 to (1.0, 0.1)) {
			val editedImage = editFunction(startImage, i)
			val results = editedImage.checkValidity(sampleImage)
			points = points :+ (results.sensitivity, results.specificity)
		}

		info(points.mkString("\n"))
	}
}
