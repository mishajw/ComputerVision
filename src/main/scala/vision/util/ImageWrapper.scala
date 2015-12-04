package vision.util

import java.awt.image.BufferedImage
import java.io.{File, FileNotFoundException}
import java.net.{MalformedURLException, URL}
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

import grizzled.slf4j.Logging
import vision.filters.Filter

import scala.collection.mutable.ArrayBuffer

class ImageWrapper(private val _pixels: Matrix[Int]) extends Cloneable with Logging {

	case class TestResults(var tp: Double, var tn: Double, var fp: Double, var fn: Double) {
		def sensitivity = tp / (tp + fn)
		def specificity = tn / (tn + fp)
    def tpr = sensitivity
    def fpr = 1 - specificity
	}

	def this(path: String) {
		this({
			var rgbImage: BufferedImage = null

			try
				rgbImage = ImageIO.read(new URL(path))
			catch {
				case e: MalformedURLException =>
					val f = new File(path)
					if (!f.exists())
					throw new FileNotFoundException(s"Could not find URL or file with the path '$path'")
					rgbImage = ImageIO.read(f)
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

	override def clone = new ImageWrapper(pixels.clone)

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

	def checkValidity(sample: ImageWrapper): TestResults = {
		val results = TestResults(0, 0, 0, 0)

		val maxWidth = width max sample.width
		val maxHeight = height max sample.height
		for (x <- 0 until maxWidth; y <- 0 until maxHeight) {
			val thisPositive = getPixel(x, y) > 0
			val otherPositive = sample.getPixel(x, y) > 0

			(thisPositive, otherPositive) match {
				case (true, true) => results.tp += 1
				case (true, false) => results.fp += 1
				case (false, true) => results.fn += 1
				case (false, false) => results.tn += 1
			}
		}

//		results.normalise()

		results
	}
}
