package util.images

import java.io.File
import javax.imageio.ImageIO

class Image(path: String) {

	private val image = ImageIO.read(new File(path))
	private val pixels = image.getData.getPixels(0, 0, image.getWidth, image.getHeight, null: Array[Int])

	val width = image.getWidth
	val height = image.getHeight

	/**
	  * @return (r, g, b, a)
	  */
	def getPixel(x: Int, y: Int): (Int, Int, Int, Int) = {
		val rgb = image.getColorModel.getRGB(pixels(width * x + y))

		val a = (rgb >> 24) & 0xff
		val r = (rgb >> 16) & 0xff
		val g = (rgb >> 8) & 0xff
		val b = rgb & 0xff

		(r, g, b, a)
	}
}
