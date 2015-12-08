package vision

import breeze.linalg.DenseVector
import breeze.plot._
import grizzled.slf4j.Logging
import vision.analysis.Operations.Operation
import vision.analysis.{Operations, TestResults}
import vision.util.ImageWrapper

object Analyser extends Logging {

	/**
		* @param orig Image with pre operations applied
		* @param sample The sample image to test against
		* @param operations The operations to vary eg. Threshold(10), Threshold(20) etc.
		* @param postOperations Any operations to apply after
		* @return Test results for each operation variation
		*/
	def vary(orig: ImageWrapper, sample: ImageWrapper,
	         operations: Seq[Operation], postOperations: Seq[Operation]): Seq[(Operation, TestResults)] = {

		var results = Seq.empty[(Operation, TestResults)]

		for (op <- operations) {
			info(s"Varying on $op")

			// apply all
			val newImage = postOperations.foldLeft(orig.apply(op))((im, o) => im.apply(o))

			// analyse
			val tr = newImage.checkValidity(sample)
			results = results :+(op, tr)
		}

		results
	}

	def drawResults(results: Seq[(Operation, TestResults)]) = {

		val f = Figure()
		val p = f.subplot(0)

		val x = DenseVector(results.map(_._2.fpr).toArray)
		val y = DenseVector(results.map(_._2.tpr).toArray)

		p.xlabel = "FPR (1 - specificity)"
		p.ylabel = "TPR (sensitivity)"
		p.setXAxisDecimalTickUnits()
		p.setYAxisDecimalTickUnits()

		p += plot(x, y, shapes = true, labels = (i) => Operations.prettyString(results(i)._1),
			tips = (i) => results(i)._1.toString + " with distance from optimal of " + results(i)._2.dist)

	}
}
