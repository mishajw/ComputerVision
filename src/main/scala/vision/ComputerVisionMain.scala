package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.analysis.Operations.{Gaussian, Roberts}
import vision.filters.FilterFactory
import vision.util.{ImageGenerator, ImageWrapper}

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images/"

	val defaultGauss = 3
	val defaultFirstThreshold = 70
	val defaultSecondThreshold = 30

	val images = Seq("10905 JL", "43590 AM", "9343 AM")

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val imageName = images(0)

		val startImage = new ImageWrapper(s"${imagesPath}orig/$imageName.bmp")
		val sampleImage = new ImageWrapper(s"${imagesPath}sample-edges/$imageName Edges.bmp").flip.normalise

//    val editedImage = startImage
//      .applyThreshold(30)
//      .convolute(FilterFactory.getFilter(SimpleMean(1)/*Gaussian(5, 1)*/))
//      .convolute(FilterFactory.getFilter(Laplacian))
//      .applyThreshold(50)
//      .normalise
//
//    val results = editedImage.checkValidity(sampleImage)
//    info("Hello??")
//    info(s"${results.tpr}, ${results.fpr}")

//		startImage
//			.applyThreshold(35)
//			.display

		ImageGenerator.generateAll(startImage, sampleImage)
	}

	def runTests(startImage: ImageWrapper, sampleImage: ImageWrapper) {

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
		val edgeDetection = FilterFactory.getFilter(Roberts)
		val smoothing = FilterFactory.getFilter(Gaussian(gaussSize, 1))

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
