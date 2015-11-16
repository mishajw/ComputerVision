package util.images

object RGBToGray {
	def convert(rgb: Image) {
		var gray: Array[Int] = Array()

		for (x <- 0 to rgb.height; y <- 0 to rgb.width) {
			val pixel = rgb.getPixel(x, y)

			gray = gray :+ (pixel.sum / pixel.length)
		}
	}
}
