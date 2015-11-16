package vision.filters

import vision.filters.FilterFactory.Mask
import vision.util.{Matrix, ImageWrapper}

class EdgeDetectionFilter(xMask: Mask, yMask: Mask) extends Filter {

	override def convolute(image: ImageWrapper): ImageWrapper = {
		convoluteSingle(image, xMask)
	}

	private def convoluteSingle(image: ImageWrapper, mask: Mask): ImageWrapper = {

		val newImage = image.clone().asInstanceOf[ImageWrapper]

		for (x <- 0 until image.width; y <- 0 until image.height) {

			var total:Double = 0
			val size = Math.floor(mask.width / 2).asInstanceOf[Int]

			for (dx <- 0 until mask.width; dy <- 0 until mask.height)
				total += mask.get(dx, dy) * newImage.getPixel(x + dx - size, y + dy - size, 0)

			newImage.setPixel(x,y,Math.max(0, Math.min(255, total)).asInstanceOf[Int])

		}

		newImage
	}

	override def toString: String = "X: " + xMask + "\nY: " + yMask
}
