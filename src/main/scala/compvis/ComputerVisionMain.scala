package compvis

import _root_.util.images.Image
import grizzled.slf4j.Logging

object ComputerVisionMain extends Logging{
	def main(args: Array[String]) = {
		info("Starting...")

		val image = new Image("src/main/resources/red.jpg")

	}
}
