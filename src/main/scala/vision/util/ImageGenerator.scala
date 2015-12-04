package vision.util

import akka.actor.{ActorSystem, Props}
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{Images, ImageDetails, PrintFrequency}
import vision.actors.MasterImageGenerator
import vision.analysis.Operations._

import scala.util.Random

object ImageGenerator extends Logging {
	def generateAll(original: ImageWrapper, sample: ImageWrapper): Unit = {
		info("Initialising image generation")
		DB.reset()
		var count = 0
		var times: List[Long] = List()

		val system = ActorSystem("vision")
		val master = system.actorOf(Props[MasterImageGenerator])

		info("Collecting images")

		var images: List[ImageDetails] = List()
		for (
			transform <- Random.shuffle(TRANSFORMATIONS);
			nrf <- Random.shuffle(NOISE_REMOVAL); //.map(FilterFactory.getFilter);
			edf <- Random.shuffle(EDGE_DETECTORS); //.map(FilterFactory.getFilter);
			fin <- Random.shuffle(FINAL_THRESHOLDS)) {
			images = images :+ ImageDetails(original, sample, Seq(transform, nrf, edf, NormaliseOperation, fin, FlipOperation))

		}

		info(s"Total images: ${images.size}")

		info("Sending to master")
		master ! Images(images)

		info("Printing results")
		while (true) {
			master ! PrintFrequency
			Thread.sleep(1000)
		}
	}
}
