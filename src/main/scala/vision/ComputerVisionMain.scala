package vision

import grizzled.slf4j.Logging
import vision.filters.FilterFactory
import vision.filters.FilterFactory.Sobel
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	def main(args: Array[String]): Unit = {
		info("Starting...")

		val filter = FilterFactory.getFilter(Sobel())

		val image = new ImageWrapper("src/main/resources/homer.png")
		val convolved = filter.convolute(image)
		convolved.normalise()
		convolved.display()

		info(filter)
	}
}
