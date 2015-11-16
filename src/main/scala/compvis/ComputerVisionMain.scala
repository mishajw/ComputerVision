package compvis

import _root_.util.images.ImageWrapper
import grizzled.slf4j.Logging

object ComputerVisionMain extends Logging {
	def main(args: Array[String]) = {
		info("Starting...")

		val image = new ImageWrapper("src/main/resources/house.jpg")
	}
}
