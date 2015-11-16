package util.images

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JLabel, JOptionPane}

class ImageWrapper(path: String) {

	private val image: BufferedImage = {
		val file = new File(path)

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

	private val pixels = image.getData.getPixels(0, 0, image.getWidth, image.getHeight, null: Array[Int])

	val width = image.getWidth
	val height = image.getHeight

	def getPixel(x: Int, y: Int): Int = image.getColorModel.getRed(pixels(width * x + y))

	def display() = JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(image)))
}