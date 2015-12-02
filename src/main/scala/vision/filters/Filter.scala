package vision.filters

import vision.filters.FilterFactory._
import vision.util.ImageWrapper

abstract class Filter {
	def convolute(image: ImageWrapper): ImageWrapper

	protected def convoluteSingle(image: ImageWrapper, mask: Mask): ImageWrapper = {

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

	protected def combineFast(convX: ImageWrapper, convY: ImageWrapper): ImageWrapper = {
		val combined = convX.clone().asInstanceOf[ImageWrapper]

		for (x <- 0 until combined.width; y <- 0 until combined.height) {
			combined.setPixel(x, y, (
				Math.abs(convX.getPixel(x, y)) +
					Math.abs(convY.getPixel(x, y))
				) / 2
			)
		}

		combined
	}

	protected def combineSlow(convX: ImageWrapper, convY: ImageWrapper): ImageWrapper = {
		val combined = convX.clone().asInstanceOf[ImageWrapper]

		for (x <- 0 until combined.width; y <- 0 until combined.height) {
			combined.setPixel(x, y, Math.sqrt(
				Math.pow(convX.getPixel(x, y), 2) +
					Math.pow(convY.getPixel(x, y), 2)
			).toInt
			)
		}

		combined
	}
}
