package vision.analysis

object Operations {

	// thresholds of 20, 40, 60, 80, 100
	val TRANSFORMATIONS = Seq(TransformationIntensity) ++
		(20 to 100 by 20 map TransformationBinary)

	val EDGE_DETECTORS = Seq(SimpleGradient, Sobel, Roberts, Prewitt)

	// todo noise removals

	abstract sealed class Operation


	abstract sealed class ImageTransformation extends Operation

	case object TransformationIntensity extends ImageTransformation

	case class TransformationBinary(threshold: Int) extends ImageTransformation


	abstract sealed class NoiseRemoval extends Operation

	case object SimpleMean extends NoiseRemoval

	case class Gaussian(size: Int, standardDeviation: Int) extends NoiseRemoval


	abstract sealed class EdgeDetection extends Operation

	case object SimpleGradient extends EdgeDetection

	case object Sobel extends EdgeDetection

	case object Roberts extends EdgeDetection

	case object Prewitt extends EdgeDetection

}
