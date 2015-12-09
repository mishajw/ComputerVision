package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.analysis.Operations._
import vision.analysis.TestResults
import vision.filters.FilterFactory
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images"

	val defaultGauss = 3
	val defaultFirstThreshold = 70
	val defaultSecondThreshold = 30

	val images = Seq("10905 JL", "43590 AM", "9343 AM")
	val thresholds = Map(0 -> 34, 1 -> 10, 2 -> 30)

	def main(args: Array[String]): Unit = {
		info("Starting...")

		noiseRemoval()
	}

	/**
		* Shows necessity of noise removal
		*/
	def noiseRemoval(): Unit = {
		var results = Seq.empty[(String, TestResults)]
		for (pair <- thresholds) {
			val (index, threshold) = pair

			val orig = getOriginalImage(index)
			val sample = getSampleImage(index).flip

			val sans = orig.applyThreshold(threshold).apply(Sobel).normalise.applyThreshold(20)
			info("Without noise removal")
			val withoutRes = sans.checkValidity(sample)
			results = results :+(images(index) + " without", withoutRes)
			info(withoutRes)

			val avec = orig.applyThreshold(threshold).apply(SimpleMean(3)).apply(Sobel).normalise.applyThreshold(20)
			val withRes = avec.checkValidity(sample)
			info("With noise removal")
			info(withRes)
			results = results :+(images(index) + " with", withRes)

			//			sans.display
			//			avec.display
		}

		Analyser.drawResults(results, title = "The effect of noise removal", limit = true, saveFile = "noise_removal_all")
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
