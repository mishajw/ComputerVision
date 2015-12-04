package vision.util

import akka.actor.{Props, ActorSystem}
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{PrintFrequency, ImageDetails}
import vision.actors.MasterImageGenerator
import vision.analysis.Operations._
import vision.filters.Filter

import scala.util.Random

object ImageGenerator extends Logging {
	def generateAll(original: ImageWrapper, sample: ImageWrapper): Unit = {
		info("Initialising image generation")
		DB.reset()
		var count = 0
		var times: List[Long] = List()

		val system = ActorSystem("vision")
		val master = system.actorOf(Props[MasterImageGenerator])

		info("Starting image generation")
		for (
			t <- Random.shuffle(TRANSFORMATIONS);
			nrf <- Random.shuffle(NOISE_REMOVAL); //.map(FilterFactory.getFilter);
			edf <- Random.shuffle(EDGE_DETECTORS); //.map(FilterFactory.getFilter);
			fin <- Random.shuffle(FINAL_THRESHOLDS)) {
			master ! ImageDetails(original, sample, t, nrf, edf, fin)
		}

		info("Printing results")
		while (true) {
			master ! PrintFrequency
			Thread.sleep(1000)
		}
	}

	def generateImage(original: ImageWrapper, transformation: ImageTransformation, nrf: Filter,
										edf: Filter, fin: FinalThreshold): ImageWrapper = {
		(transformation match {
			case TransformationIntensity => original
			case TransformationBinary(n) => original.applyThreshold(n);
		}).convolute(nrf)
			.convolute(edf)
			.applyThreshold(fin.threshold)
	}
}
