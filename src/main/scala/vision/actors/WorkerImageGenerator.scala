package vision.actors

import akka.actor.Actor
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDetails, ImageDone}
import vision.analysis.Operations._
import vision.filters.{Filter, FilterFactory}
import vision.util.{DB, ImageWrapper}

class WorkerImageGenerator extends Actor with Logging {

	override def receive = {
		case ImageDetails(original, sample, t, nrf, edf, fin) =>
			testImage(original, sample, t, nrf, edf, fin)
			sender() ! ImageDone
	}

	def testImage(original: ImageWrapper, sample: ImageWrapper, t: ImageTransformation, nrf: NoiseRemoval, edf: EdgeDetection, fin: FinalThreshold): Unit = {
		val image = generateImage(original, t, FilterFactory.getFilter(nrf), FilterFactory.getFilter(edf), fin)
		val validity = image checkValidity sample
//		val fileName = "images/" + s"$t$nrf$edf$fin".hashCode + ".png"
//		ImageIO.write(image.createImage, "png", new File(fileName))
//
//		info(s"$t, $nrf, $edf, $fin")
//		info(f"${validity.sensitivity}%.2f, ${validity.specificity}%.2f")
//
		DB.insertResults(t, nrf, edf, fin, validity.tpr, validity.fpr, validity.dist)
	}
	def generateImage(original: ImageWrapper, transformation: ImageTransformation, nrf: Filter,
										edf: Filter, fin: FinalThreshold): ImageWrapper = {
		(transformation match {
			case TransformationIntensity => throw new IllegalArgumentException("neh")
			case TransformationBinary(n) => original.applyThreshold(n);
		}).convolute(nrf)
			.convolute(edf)
				.normalise
			.applyThreshold(fin.threshold)
				.flip
	}
}
