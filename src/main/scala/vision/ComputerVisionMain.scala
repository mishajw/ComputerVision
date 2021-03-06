package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.analysis.Operations._
import vision.analysis.TestResults
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	private val imagesPath = "src/main/resources/images"

	val defaultGauss = 3
	val defaultFirstThreshold = 70
	val defaultSecondThreshold = 30

	val images = Seq("10905 JL", "43590 AM", "9343 AM")
	val thresholds = Seq(34, 10, 30)
	val finalThreshold = 15 // reasonable


	def main(args: Array[String]): Unit = {
		info("Starting...")

//		noiseRemovalVariation(Seq(0.5d, 1d, 4d) map (Gaussian(3, _)))
	}

	def noiseRemovalVariation(noiseRemovalVariations: Seq[NoiseRemoval], edgeDetector: EdgeDetection = Sobel): Unit = {

		images.zipWithIndex foreach (pair => {
			val (name, i) = pair

			val image = getOriginalImage(i)
			val sample = getSampleImage(i)

			println(name)
			noiseRemovalVariations foreach (nr => {
				print(nr + " -> ")
				println(image.
						apply(nr)
						.apply(edgeDetector)
						.apply(ThresholdOperation(finalThreshold))
						.flip
						.doRocAnalysis(sample))
			})


		})


	}

	/**
		* Shows necessity of noise removal
		*/
	def noiseRemoval(): Unit = {
		var results = Seq.empty[(String, TestResults)]
		for ((threshold, index) <- thresholds.zipWithIndex) {
			val orig = getOriginalImage(index)
			val sample = getSampleImage(index).flip

			val sans = orig.threshold(threshold).apply(Sobel).normalise.threshold(20)
			info("Without noise removal")
			val withoutRes = sans.doRocAnalysis(sample)
			results = results :+(images(index) + " without", withoutRes)
			info(withoutRes)

			val avec = orig.threshold(threshold).apply(SimpleMean(3)).apply(Sobel).normalise.threshold(20)
			val withRes = avec.doRocAnalysis(sample)
			info("With noise removal")
			info(withRes)
			results = results :+(images(index) + " with", withRes)
		}

		Analyser.drawResults(results, title = "The effect of noise removal", limit = true/*, saveFile = "noise_removal_all"*/)
	}

	def simpleEdgeDetectionFilterTests(): Unit = {
		for (i <- 0 until 3) {
			val image = getOriginalImage(i)
				.threshold(thresholds(i))
				.apply(Gaussian(5, 1))

			val sample = getSampleImage(i).flip

			val results = for (edf <- EDGE_DETECTORS)
				yield (edf.toString, image.apply(edf).threshold(10).doRocAnalysis(sample))

			Analyser.drawResults(results, title = "The effects of different Edge Detection filters"/*, saveFile = s"$i-edge_detect_all"*/)
		}
	}

	def thresholdRanges(i: Int): Unit = {
		val image = getOriginalImage(i)
			.threshold(thresholds(i))
			.apply(Gaussian(5, 1))
			.apply(Roberts)

		val sample = getSampleImage(i).flip

		val results = (1 to 30) map (thr => (thr.toString, image.threshold(thr).doRocAnalysis(sample)))

		Analyser.drawJoinedResults(results, title = "The effects of different final thresholds", saveFile = s"threshold_example")
	}

	def getOriginalPath(index: Int) = s"$imagesPath/orig/${images(index)}.bmp"

	def getSamplePath(index: Int) = s"$imagesPath/sample-edges/${images(index)} Edges.bmp"

	def getOriginalImage(index: Int, greenElement: Boolean = false, threshold: Boolean = true) = {
		val im = new ImageWrapper(getOriginalPath(index), greenElement)
		if (threshold)
			im.apply(ThresholdOperation(thresholds(index)))
		else
			im
	}

	def getSampleImage(index: Int, greenElement: Boolean = false) = new ImageWrapper(getSamplePath(index), greenElement)

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
