package compvis

import _root_.util.filters.FilterFactory
import _root_.util.filters.FilterFactory.Sobel
import _root_.util.images.ImageWrapper
import grizzled.slf4j.Logging

import scala.io.Source

object ComputerVisionMain extends Logging {
	def main(args: Array[String]) = {
		info("Starting...")

//		val image = new ImageWrapper("src/main/resources/house.jpg")
//		image.display()

		FilterFactory.getFilter(Sobel())
	}
}
