package vision.util

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

import grizzled.slf4j.Logging
import vision.filters.Filter

import scala.collection.mutable.ArrayBuffer

class ImageWrapper(private val _pixels: Matrix[Int]) extends Cloneable with Logging {
	def this(path: String, isUrl: Boolean) {
		this({
			var rgbImage: BufferedImage = null

			if (!isUrl) {
				val file = new File(path)

				// validation
				if (!file.exists()) {
					Console.err.println("Input file does not exist")
					sys.exit(1)
				}

				rgbImage = ImageIO.read(file)
			} else {
				rgbImage = ImageIO.read(new URL(path))
			}


			val greyImage = new BufferedImage(rgbImage.getWidth, rgbImage.getHeight, BufferedImage.TYPE_BYTE_GRAY)
			greyImage.getGraphics.drawImage(rgbImage, 0, 0, null)

			val greyPixels = greyImage.getRaster.getPixels(0, 0, greyImage.getWidth, greyImage.getHeight, null: Array[Int])
			new Matrix[Int](greyPixels, greyImage.getWidth, greyImage.getHeight)
		})
	}

	private val pixels: Matrix[Int] = _pixels

	def width = pixels.width

	def height = pixels.height

	def getPixel(x: Int, y: Int, default: Int): Int = pixels.get(x, y, default)

	def getPixel(x: Int, y: Int): Int = pixels.get(x, y)

	def setPixel(x: Int, y: Int, value: Int) = pixels.set(x, y, value)

	def createImage = {
		debug("Creating image to display")
		val image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
		image.getRaster.setPixels(0, 0, width, height, pixels.array.toArray)
		image
	}

	def display: ImageWrapper = {
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(createImage)))
		this
	}

	override def clone =
		new ImageWrapper(pixels.clone.asInstanceOf[Matrix[Int]])

	def normalise = new ImageWrapper({
		val max: Double = pixels.array.max
		val min: Double = pixels.array.min

		debug("Normalising")

		val normalisedArray: ArrayBuffer[Int] = pixels.array.map(pixel => {
			(((pixel.toDouble - min) / (max - min)) * 255d).toInt
		})

		new Matrix[Int](normalisedArray, width, height)
	})

	def applyThreshold(threshold: Int) = new ImageWrapper({
			if (threshold < 0 || threshold > 255)
				throw new IllegalArgumentException("Invalid threshold")

			debug(s"Applying threshold of $threshold")
			new Matrix[Int](pixels.array map (x => if (x >= threshold) 255 else 0), width, height)
		})

	def flip = new ImageWrapper({
		new Matrix[Int](pixels.array map (x => 255 - x), width, height)
	})

	def convolute(filter: Filter) = filter convolute this
}
