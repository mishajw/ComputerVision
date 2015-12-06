package vision.analysis

import java.util

import scala.util.Try

object Operations {

	private val STANDARD_THRESHOLDS = 20 to 100 by 10

	val TRANSFORMATIONS = Seq(ThresholdOperation(35))
	/* ++
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

	val FINAL_THRESHOLDS = STANDARD_THRESHOLDS map ThresholdOperation

	private val OPERATIONS_REGEX = """(\w+)(?:\((.+)\))?""".r
	private val ARGS_REGEX = """[0-9\.]+""".r


	abstract sealed class Operation


	abstract sealed class ImageTransformation extends Operation

	case class ThresholdOperation(threshold: Int) extends ImageTransformation

	case object NormaliseOperation extends Operation

	case object FlipOperation extends Operation


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

	def parse(s: String): Operation = {
		val OPERATIONS_REGEX(name, args) = s

		// no parameters
		if (args == null) {
			name match {
				case "NormaliseOperation" => return NormaliseOperation
				case "FlipOperation" => return FlipOperation
				case "SimpleGradient" => return SimpleGradient
				case "Sobel" => return Sobel
				case "Roberts" => return Roberts
				case "Prewitt" => return Prewitt
				case "Laplacian" => return Laplacian
			}
		}

		// parse params
		val allArgs = ARGS_REGEX.findAllMatchIn(s)
				.toList
				.map(_.group(0))

		name match {
			case "ThresholdOperation" => return ThresholdOperation(allArgs.head.toInt)
			case "SimpleMean" => return SimpleMean(allArgs.head.toInt)
			case "Gaussian" => return Gaussian(allArgs.head.toInt, allArgs(1).toDouble)
		}

		throw new IllegalArgumentException( s"""Invalid operation "$s"""")
	}
}
