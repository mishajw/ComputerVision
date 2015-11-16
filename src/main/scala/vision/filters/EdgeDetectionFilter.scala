package vision.filters

import grizzled.slf4j.Logging
import vision.filters.FilterFactory.Mask
import vision.util.{Matrix, ImageWrapper}

class EdgeDetectionFilter(xMask: Mask, yMask: Mask) extends Filter with Logging {

	override def convolute(image: ImageWrapper): ImageWrapper = {
		val convX = convoluteSingle(image, xMask)
		debug("Convoluted in X direction")
		val convY = convoluteSingle(image, yMask)
		debug("Convoluted in Y direction")

		combineFast(convX, convY)
	}

	private def convoluteSingle(image: ImageWrapper, mask: Mask): ImageWrapper = {

		val newImage = image.clone().asInstanceOf[ImageWrapper]

		for (x <- 0 until image.width; y <- 0 until image.height) {

			var total:Double = 0
			val size = Math.floor(mask.width / 2).asInstanceOf[Int]

			for (dx <- 0 until mask.width; dy <- 0 until mask.height){
				val nx: Int = x + dx - size
				val ny: Int = y + dy - size
				val maskVal: Double = mask.get(dx, dy)
				val imageVal: Int = image.getPixel(nx, ny, 0)
				val v: Double = maskVal * imageVal
				total += v
			}

			newImage.setPixel(x, y, total.asInstanceOf[Int])
		}

		newImage
	}

	override def toString: String = "X: " + xMask + "\nY: " + yMask
}
