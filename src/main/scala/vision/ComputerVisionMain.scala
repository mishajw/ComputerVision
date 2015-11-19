package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.filters.{Filter, FilterFactory}
import vision.filters.FilterFactory._
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images/"

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val imageName = "10905 JL"

		val imageSample = new ImageWrapper(s"${imagesPath}sample-edges/$imageName Edges.bmp")

		val edgeDetection = FilterFactory.getFilter(FilterRoberts)
		val smoothing = FilterFactory.getFilter(FilterGaussian)

		new ImageWrapper(s"${imagesPath}orig/$imageName.bmp")
			.convolute(smoothing)
			.applyThreshold(40)
			.convolute(edgeDetection)
			.applyThreshold(50)
			.flip
			.display

		imageSample.display
	}

	private def askForImage() = {
		val path = new JTextField(20)

		val panel = new JPanel
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
		panel.add(new JLabel("Enter path"))
		panel.add(path)

		JOptionPane.showMessageDialog(null, panel, "Computer Vision", JOptionPane.PLAIN_MESSAGE)

		new ImageWrapper(path.getText)
	}
}
