package vision.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

class ImageWrapper extends Cloneable {

	private var image: BufferedImage = _
	private var pixels: Matrix[Int] = _

	private var _width: Int = _
	private var _height: Int = _

	def width = _width

	def height = _height

	def this(filePath: String) {
		this()

		image = {
			val file = new File(filePath)

			// validation
			if (!file.exists()) {
				Console.err.println("Input file does not exist")
				sys.exit(1)
			}

			val image = ImageIO.read(file)

			val greyImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY)
			val g = greyImage.createGraphics()
			g.drawImage(image, 0, 0, null)
			g.dispose()

			greyImage
		}

		pixels = new Matrix(image.getData.getPixels(0, 0, image.getWidth, image.getHeight, null: Array[Int]),
			image.getWidth, image.getHeight)

		_width = image.getWidth
		_height = image.getHeight
	}


	def getPixel(x: Int, y: Int, default: Int): Int = pixels.get(x, y, default)

	def getPixel(x: Int, y: Int): Int = pixels.get(x, y)

	def setPixel(x:Int, y:Int, value:Int) = {
		pixels.set(x,y,value)
		image.setRGB(x,y,new Color(value,value,value).getRGB)
	}

	def display() = JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(image)))

	override def clone(): AnyRef = {
		val newImage = new ImageWrapper

		newImage._width = _width
		newImage._height = _height
		newImage.pixels = pixels.clone().asInstanceOf[Matrix[Int]]

		val b = new BufferedImage(_width, _height, image.getType)
		val g = b.getGraphics
		g.drawImage(b, 0,0,null)
		g.dispose()
		b
	}
}
