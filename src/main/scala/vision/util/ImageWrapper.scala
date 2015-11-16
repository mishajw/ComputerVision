package vision.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

class ImageWrapper extends Cloneable {

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

		val rgbImage: BufferedImage = ImageIO.read(file)
		val greyImage = new BufferedImage(rgbImage.getWidth, rgbImage.getHeight, BufferedImage.TYPE_BYTE_GRAY)
		greyImage.getGraphics.drawImage(rgbImage, 0, 0, null)


		val huePixels = greyImage.getRaster.getPixels(0,0,greyImage.getWidth, greyImage.getHeight, null: Array[Int])
		pixels = new Matrix[Int](huePixels, greyImage.getWidth(), greyImage.getHeight())
	}


	def getPixel(x: Int, y: Int, default: Int): Int = pixels.get(x, y, default)

	def getPixel(x: Int, y: Int): Int = pixels.get(x, y)

	def setPixel(x: Int, y: Int, value: Int) = pixels.set(x, y, value)

	def createImage() = {
		val image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
		image.getRaster.setPixels(0, 0, width, height, pixels.array.toArray)
		image
	}


	def display() = JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(createImage())))

	override def clone(): AnyRef = {
		val newImage = new ImageWrapper
		newImage.pixels = pixels.clone().asInstanceOf[Matrix[Int]]
		newImage
	}
}
