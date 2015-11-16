package vision.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

import grizzled.slf4j.Logging

import scala.collection.mutable.ArrayBuffer

class ImageWrapper extends Cloneable with Logging {

	private var pixels: Matrix[Int] = _

	def width = pixels.width

	def height = pixels.height

	def this(filePath: String) {
		this()

		val file = new File(filePath)

		// validation
		if (!file.exists()) {
			Console.err.println("Input file does not exist")
			sys.exit(1)
		}

		val image: BufferedImage = ImageIO.read(file)

		val imagePixels: Array[Int] = image.getData.getPixels(0, 0, image.getWidth, image.getHeight, null: Array[Int])

		pixels = new Matrix(imagePixels,image.getWidth, image.getHeight)

		var newArray: Array[Int] = Array()
		for (x <- 0 until image.getWidth; y <- 0 until image.getHeight) {
			val pixel: Array[Int] = image.getData.getPixel(y, x, null)
			//			val pixel = new Color(pixels.get(x,y))
			//			val rgb = Array(pixel.getRed, pixel.getGreen, pixel.getBlue)

			val grey = pixel.sum / pixel.length
			newArray = newArray :+ grey
		}

		pixels = new Matrix[Int](newArray, image.getWidth, image.getHeight)

		this.normalise()
	}
	
	def getPixel(x: Int, y: Int, default: Int): Int = pixels.get(x, y, default)

	def getPixel(x: Int, y: Int): Int = pixels.get(x, y)

	def setPixel(x: Int, y: Int, value: Int) = pixels.set(x, y, value)

	def createImage() = {
		val image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY)
//		image.getRaster.setPixels(0, 0, width, height, pixels.array.toArray)

		val g = image.getGraphics

		for (x <- 0 until width; y <- 0 until height) {
			val pixel: Int = pixels.get(x, y)
			g.setColor(new Color(pixel, pixel, pixel))
			g.drawRect(x, y, 1, 1)
		}

		ImageIO.write(image, "png", new File("output_image.png"))
		image
	}

	def display() = JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(createImage())))

	override def clone(): AnyRef = {
		val newImage = new ImageWrapper
		newImage.pixels = pixels.clone().asInstanceOf[Matrix[Int]]
		newImage
	}

	def normalise(): Unit = {
		val max: Double = pixels.array.max
		val min: Double = pixels.array.min

		info(s"$max, $min")

		val normalisedArray: ArrayBuffer[Int] = pixels.array.map(pixel => {
			(((pixel.toDouble - min) / (max - min)) * 255d).toInt
		})

		pixels = new Matrix[Int](normalisedArray, width, height)
	}
}
