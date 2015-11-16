package vision

import grizzled.slf4j.Logging
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	def main(args: Array[String]): Unit = {
		info("Starting...")

		val image = new ImageWrapper("src/main/resources/house.jpg")
		image.display()
	}
}
