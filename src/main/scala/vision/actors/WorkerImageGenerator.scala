package vision.actors

import java.util.concurrent.ConcurrentHashMap

import akka.actor.Actor
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDetails, ImageDone}
import vision.analysis.Operations._
import vision.filters.FilterFactory
import vision.util.{DB, ImageWrapper}

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.util.control.Breaks._

class WorkerImageGenerator extends Actor with Logging {

	override def receive = {
		case ImageDetails(original, sample, operations) =>
			testImage(original, sample, operations)
			sender() ! ImageDone
	}

	def testImage(original: ImageWrapper, sample: ImageWrapper, operations: Array[Operation]): Unit = {
		val image = generateImage(original, operations)
		val validity = image checkValidity sample
//		val fileName = "images/" + s"$t$nrf$edf$fin".hashCode + ".png"
//		ImageIO.write(image.createImage, "png", new File(fileName))
//
//		info(s"$t, $nrf, $edf, $fin")
//		info(f"${validity.sensitivity}%.2f, ${validity.specificity}%.2f")
//
		DB.insertResults(validity.tpr, validity.fpr, validity.dist, operations: _*)
	}

	def generateImage(original: ImageWrapper, operations: Array[Operation]): ImageWrapper = WorkerImageGenerator.transform(original, operations)
}

object WorkerImageGenerator extends Logging {

	val map = new TreeMap[String, ImageWrapper]()

	def transform(originalImage: ImageWrapper, operations: Array[Operation]): ImageWrapper = {
		val workingSet = new mutable.Stack[Operation]()
		val holdingSet = new mutable.Stack[Operation]()

		workingSet.pushAll(operations)

		// remove 1 by 1 until valid found
		breakable {
			while (workingSet.nonEmpty) (map.get(workingSet.mkString)) match {
				case Some(x) => break
				case None => holdingSet push (workingSet pop)
			}
		}


		// apply all
		var im = originalImage
		while (holdingSet.nonEmpty)
			im = apply(im, holdingSet pop)

		im
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
