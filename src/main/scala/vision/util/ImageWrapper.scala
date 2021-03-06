package vision.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.{File, FileNotFoundException}
import java.net.{MalformedURLException, URL}
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

import grizzled.slf4j.Logging
import vision.analysis.Operations._
import vision.analysis.TestResults
import vision.filters.{Filter, FilterFactory}

class ImageWrapper(private val _pixels: Matrix) extends Cloneable with Logging {

	def this(path: String, greenElement: Boolean = false) {
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

			if (greenElement) {
				val rgbPixels = rgbImage.getRaster.getPixels(0, 0, rgbImage.getWidth, rgbImage.getHeight, null: Array[Double])
				val greens = (rgbPixels grouped 3).map(e => e(1))

				val singleByteImage = new BufferedImage(rgbImage.getWidth, rgbImage.getHeight, BufferedImage.TYPE_BYTE_GRAY)
				val singleRaster = singleByteImage.getRaster
				singleRaster.setPixels(0, 0, rgbImage.getWidth, rgbImage.getHeight, greens.toArray)

				new Matrix(singleRaster.getPixels(0, 0, rgbImage.getWidth, rgbImage.getHeight, null: Array[Double]),
					rgbImage.getWidth, rgbImage.getHeight)
			}
			else {
				val greyImage = new BufferedImage(rgbImage.getWidth, rgbImage.getHeight, BufferedImage.TYPE_BYTE_GRAY)
				greyImage.getGraphics.drawImage(rgbImage, 0, 0, null)

				val greyPixels = greyImage.getRaster.getPixels(0, 0, greyImage.getWidth, greyImage.getHeight, null: Array[Double])
				new Matrix(greyPixels, greyImage.getWidth, greyImage.getHeight)
			}
		})
	}

	private val pixels: Matrix = _pixels

	def width = pixels.width

	def height = pixels.height

	def getPixel(x: Int, y: Int, default: Int): Int = pixels.get(x, y, default).toInt

	def getPixel(x: Int, y: Int): Int = pixels.get(x, y).toInt

	def setPixel(x: Int, y: Int, value: Int) = pixels.set(x, y, value)

	def createImage = {
		debug("Creating image to display")
		val image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
		image.getRaster.setPixels(0, 0, width, height, pixels.array)
		image
	}

	def display: ImageWrapper = {
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(createImage)))
		this
	}

	def save(directory: File, name: String): ImageWrapper = save(directory, "bmp", name)

	def save(directory: File, ext: String, name: String):ImageWrapper = {
		if (!directory.exists())
			directory.mkdir()
		
		ImageIO.write(createImage, ext, Paths.get(directory.getAbsolutePath, name).toFile)
		debug(s"Saved file '$name'")
		this
	}

	override def clone = new ImageWrapper(pixels.clone)

	def normalise = new ImageWrapper({
		val max: Double = pixels.array.max
		val min: Double = pixels.array.min

		debug("Normalising")

		val normalisedArray: Array[Double] = pixels.array.map(pixel => {
			((pixel - min) / (max - min)) * 255d
		})

		new Matrix(normalisedArray, width, height)
	})

	def threshold(threshold: Int) = new ImageWrapper({
		if (threshold < 0 || threshold > 255)
		throw new IllegalArgumentException("Invalid threshold")

		debug(s"Applying threshold of $threshold")
		new Matrix(pixels.array map (x => if (x >= threshold) 255d else 0d), width, height)
	})

	def flip = new ImageWrapper({
		new Matrix(pixels.array map (x => 255 - x), width, height)
	})

	def convolute(filter: Filter) = filter convolute this

	def doRocAnalysis(sample: ImageWrapper): TestResults = {
		val results = new TestResults(0, 0, 0, 0)

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

		results
	}

	def apply(operation: Operation): ImageWrapper = {
		operation match {
			case filter: FilterOperation =>
				convolute(FilterFactory.getFilter(filter))
			case ThresholdOperation(n) => threshold(n)
			case FlipOperation => flip
			case NormaliseOperation => normalise
			case _ => this
		}
	}

	def apply(operations: Seq[Operation]): ImageWrapper = operations.foldLeft(this)((im, op) => im.apply(op))
}
