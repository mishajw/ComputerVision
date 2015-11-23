package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.filters.FilterFactory
import vision.filters.FilterFactory.{FilterRoberts, FilterGaussian}
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images/"

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val imageName = Seq("10905 JL", "43590 AM", "9343 AM")(0)


		val sampleImage = new ImageWrapper(s"${imagesPath}sample-edges/$imageName Edges.bmp").flip.normalise
		val startImage = new ImageWrapper(s"${imagesPath}orig/$imageName.bmp")

		Analyser.analyse(startImage, sampleImage, (startImage: ImageWrapper, i: Double) => {
			val edgeDetection = FilterFactory.getFilter(FilterRoberts)
			val smoothing = FilterFactory.getFilter(FilterGaussian((i * 15).asInstanceOf[Int] + 1))

			startImage
				.convolute(smoothing)
				.normalise
				.applyThreshold(70)
				.convolute(edgeDetection)
				.applyThreshold(30)
		})


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