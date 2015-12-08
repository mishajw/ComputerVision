package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.analysis.Operations._
import vision.filters.FilterFactory
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images"

	val defaultGauss = 3
	val defaultFirstThreshold = 70
	val defaultSecondThreshold = 30

	val images = Seq("10905 JL", "43590 AM", "9343 AM")

	def main(args: Array[String]): Unit = {
		info("Starting...")

		val operations = Seq(ThresholdOperation(35), Gaussian(3, 1d), Sobel, NormaliseOperation, ThresholdOperation(100), FlipOperation)

		val index = 0
		val sample = getSampleImage(index)

		// pre operations
		val preops = Seq()

		// vary operation
		val thresholds = 0 to 100 by 5
		val varyOps = thresholds map ThresholdOperation

		// post operations
		val postOps = operations.slice(1, operations.size)

		// compare and display
		val image = getOriginalImage(index).apply(preops)
		Analyser.drawResults(Analyser.vary(image, sample, varyOps, postOps))
	}

	def getOriginalPath(index: Int) = s"$imagesPath/orig/${images(index)}.bmp"

	def getSamplePath(index: Int) = s"$imagesPath/sample-edges/${images(index)} Edges.bmp"

	def getOriginalImage(index: Int) = new ImageWrapper(getOriginalPath(index))

	def getSampleImage(index: Int) = new ImageWrapper(getSamplePath(index))


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
