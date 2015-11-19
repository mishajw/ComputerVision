package vision

import grizzled.slf4j.Logging
import vision.filters.{Filter, FilterFactory}
import vision.filters.FilterFactory.{FilterGaussian, FilterSobel}
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images/"

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val imageName = "10905 JL"

		val imageSample = new ImageWrapper(s"${imagesPath}sample-edges/$imageName Edges.bmp", false)

		val gauss = FilterFactory.getFilter(FilterGaussian)
		val sobel = FilterFactory.getFilter(FilterSobel)

		new ImageWrapper(s"${imagesPath}orig/$imageName.bmp", false)
			.convolute(gauss)
			.applyThreshold(40)
			.convolute(sobel)
			.applyThreshold(50)
			.flip
			.display

		imageSample.display
	}
}
