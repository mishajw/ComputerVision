package vision.actors

import java.io.File
import javax.imageio.ImageIO

import akka.actor.Actor
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDone, ImageDetails}
import vision.analysis.Operations.{FinalThreshold, EdgeDetection, NoiseRemoval, ImageTransformation}
import vision.filters.FilterFactory
import vision.util.{ImageWrapper, DB}
import vision.util.ImageGenerator._

class WorkerImageGenerator extends Actor with Logging {

	override def receive = {
		case ImageDetails(original, sample, t, nrf, edf, fin) =>
			createImage(original, sample, t, nrf, edf, fin)
			sender() ! ImageDone
	}

	def createImage(original: ImageWrapper, sample: ImageWrapper, t: ImageTransformation, nrf: NoiseRemoval, edf: EdgeDetection, fin: FinalThreshold): Unit = {
		val startTime = System.currentTimeMillis()

		val image = generateImage(original, t, FilterFactory.getFilter(nrf), FilterFactory.getFilter(edf), fin)
		val validity = image checkValidity sample
		val fileName = "images/" + s"$t$nrf$edf$fin".hashCode + ".png"
		ImageIO.write(image.createImage, "png", new File(fileName))

		info(s"$t, $nrf, $edf, $fin")
		info(f"${validity.sensitivity}%.2f, ${validity.specificity}%.2f")

		DB.insertResults(fileName, t, nrf, edf, fin, validity.tpr, validity.fpr, validity.dist)
	}
}
