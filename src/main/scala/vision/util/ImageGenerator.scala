package vision.util

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDetails, Images, PrintFrequency}
import vision.actors.MasterImageGenerator
import vision.analysis.Operations._

import scala.concurrent.Await
import scala.util.{Random, Try}

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
			images = images :+ ImageDetails(original, sample, Array(transform, nrf, edf, NormaliseOperation, fin, FlipOperation))

		}

		info(s"Total images: ${images.size}")

		info("Sending to master")
		master ! Images(images)

		var result = 0d
		implicit val timeout = Timeout(500, TimeUnit.MILLISECONDS)

		info("Printing results")
		while (result < 100) {
			val future = master ? PrintFrequency
			result = Try(Await.result(future, timeout.duration)).getOrElse(-1d).asInstanceOf[Double]
			Thread.sleep(1000)
		}

		system.shutdown()
	}
}
