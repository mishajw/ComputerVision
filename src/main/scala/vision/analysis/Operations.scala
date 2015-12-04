package vision.analysis

object Operations {

	private val STANDARD_THRESHOLDS = 20 to 100 by 10

	// thresholds of 20, 40, 60, 80, 100
	val TRANSFORMATIONS = Seq(TransformationIntensity, TransformationBinary(35))/* ++
		(STANDARD_THRESHOLDS map TransformationBinary)*/

	val NOISE_REMOVAL = {
		var x: List[NoiseRemoval] = List()

		for (size <- 2 to 7; sd <- 1d to 4d by 1d)
			x = x :+ Gaussian(size, sd)
		for (size <- 1 to 7 by 2)
			x = x :+ SimpleMean(size)

		x
	}

	val EDGE_DETECTORS = Seq(SimpleGradient, Sobel, Roberts, /*Prewitt, */Laplacian)

	val FINAL_THRESHOLDS = STANDARD_THRESHOLDS map FinalThreshold

	// todo noise removals

	abstract sealed class Operation


	abstract sealed class ImageTransformation extends Operation

	case object TransformationIntensity extends ImageTransformation

	case class TransformationBinary(threshold: Int) extends ImageTransformation


	abstract sealed class FilterOperation extends Operation


	abstract sealed class NoiseRemoval extends FilterOperation

	case class SimpleMean(size: Int) extends NoiseRemoval

	case class Gaussian(size: Int, standardDeviation: Double) extends NoiseRemoval


	abstract sealed class EdgeDetection extends FilterOperation

	case object SimpleGradient extends EdgeDetection

	case object Sobel extends EdgeDetection

	case object Roberts extends EdgeDetection

	case object Prewitt extends EdgeDetection

	case object Laplacian extends EdgeDetection


	case class FinalThreshold(threshold: Int) extends Operation

}
