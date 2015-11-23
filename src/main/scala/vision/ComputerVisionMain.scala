package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.filters.FilterFactory
import vision.filters.FilterFactory.{FilterGaussian, FilterRoberts}
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images/"

	val defaultGauss = 3
	val defaultFirstThreshold = 70
	val defaultSecondThreshold = 30

	val images = Seq("10905 JL", "43590 AM", "9343 AM")

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val imageName = images(1)

		val startImage = new ImageWrapper(s"${imagesPath}orig/$imageName.bmp")
		val sampleImage = new ImageWrapper(s"${imagesPath}sample-edges/$imageName Edges.bmp").flip.normalise

		val perfectGauss = toGauss(Analyser.analyse(startImage, sampleImage, (startImage: ImageWrapper, i: Double) => {
			editImage(startImage, toGauss(i), defaultFirstThreshold, defaultSecondThreshold)
		}))

		val perfectFirstThreshold = toThreshold(Analyser.analyse(startImage, sampleImage, (startImage: ImageWrapper, i: Double) => {
			editImage(startImage, defaultGauss, toThreshold(i), defaultSecondThreshold)
		}))

		val perfectSecondThreshold = toThreshold(Analyser.analyse(startImage, sampleImage, (startImage: ImageWrapper, i: Double) => {
			editImage(startImage, defaultGauss, defaultFirstThreshold, toThreshold(i))
		}))

		info(s"Gauss: $perfectGauss, 1st: $perfectFirstThreshold, 2nd: $perfectSecondThreshold")

		val perfectImage = editImage(startImage, perfectGauss, perfectFirstThreshold, perfectSecondThreshold)
		val results = perfectImage checkValidity sampleImage

		info(s"Perfect values: $perfectGauss, $perfectFirstThreshold, $perfectSecondThreshold")

		info(s"Sensitivity: ${results.sensitivity}")
		info(s"Specificity: ${results.specificity}")

		perfectImage.display
		sampleImage.display
	}

	def editImage(startImage: ImageWrapper, gaussSize: Int, firstThreshold: Int, secondThreshold: Int): ImageWrapper = {
		val edgeDetection = FilterFactory.getFilter(FilterRoberts)
		val smoothing = FilterFactory.getFilter(FilterGaussian(gaussSize))

		startImage
			.convolute(smoothing)
			.normalise
			.applyThreshold(firstThreshold)
			.convolute(edgeDetection)
			.applyThreshold(secondThreshold)
	}

	def toGauss(i: Double) = (i * 15).asInstanceOf[Int] + 1
	
	def toThreshold(i: Double) = (i * 255).asInstanceOf[Int]

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
