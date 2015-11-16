package vision

import grizzled.slf4j.Logging
import vision.filters.FilterFactory
import vision.filters.FilterFactory.{FilterGaussian, FilterSobel}
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	def main(args: Array[String]): Unit = {
		info("Starting...")

		var image = new ImageWrapper("src/main/resources/images/orig/9343 AM.bmp")
		image = FilterFactory.getFilter(FilterGaussian).convolute(image)
		image = FilterFactory.getFilter(FilterSobel).convolute(image)
		image.normalise()
		image.applyThreshold(70)
		image.display()
	}
}
