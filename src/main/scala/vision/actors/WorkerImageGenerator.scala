package vision.actors

import java.util.concurrent.ConcurrentHashMap

import akka.actor.Actor
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDetails, ImageDone}
import vision.analysis.Operations._
import vision.filters.{Filter, FilterFactory}
import vision.util.{DB, ImageWrapper}

import scala.collection.mutable.ListBuffer

class WorkerImageGenerator extends Actor with Logging {

	override def receive = {
		case ImageDetails(original, sample, operations) =>
			testImage(original, sample, operations: _*)
			sender() ! ImageDone
	}

	def testImage(original: ImageWrapper, sample: ImageWrapper, operations: Operation*): Unit = {
		val image = generateImage(original, operations: _*)
		val validity = image checkValidity sample
//		val fileName = "images/" + s"$t$nrf$edf$fin".hashCode + ".png"
//		ImageIO.write(image.createImage, "png", new File(fileName))
//
//		info(s"$t, $nrf, $edf, $fin")
//		info(f"${validity.sensitivity}%.2f, ${validity.specificity}%.2f")
//
		DB.insertResults(validity.tpr, validity.fpr, validity.dist, operations: _*)
	}

	def generateImage(original: ImageWrapper, operations: Operation*): ImageWrapper = WorkerImageGenerator.transform(original, operations)
}

object WorkerImageGenerator {

	val map = new ConcurrentHashMap[ListBuffer[Operation], ImageWrapper]()

	def transform(originalImage: ImageWrapper, operations: Seq[Operation]): ImageWrapper = {
		var currentOperations = new ListBuffer[Operation]()

		operations.foldLeft(originalImage)((im, o) => {
			// add new operation
			currentOperations += o

			// find existing operated image
			var newIm = map.get(currentOperations)

			if (newIm == null) {
				newIm = apply(im, o)
				map.put(currentOperations, newIm)
			}

			newIm
		})


	}

	def apply(im: ImageWrapper, operation: Operation): ImageWrapper = {

		operation match {
			case filter: FilterOperation =>
				im.convolute(FilterFactory.getFilter(filter))
			case ThresholdOperation(n) => im.applyThreshold(n)
			case FlipOperation => im.flip
			case NormaliseOperation => im.normalise
			case _ => im
		}


	}


}
