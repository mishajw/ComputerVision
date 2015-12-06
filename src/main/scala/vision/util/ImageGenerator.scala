package vision.util

import java.io
import java.nio.file.{Files, Paths}
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.mongodb.BasicDBList
import com.mongodb.casbah.commons.MongoDBObject
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{ImageDetails, Images, PrintFrequency}
import vision.actors.MasterImageGenerator
import vision.analysis.Operations
import vision.analysis.Operations._

import scala.concurrent.Await
import scala.reflect.io.File
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

	def regenerateFromDB(image: String, saveDirPath: String, deleteAll: Boolean): Unit = {
		val saveDir = new io.File(saveDirPath)
		if (deleteAll) {
			val files = saveDir.listFiles
			info(s"Deleting all ${files.length} old images")
			files.foreach(_.delete)
		}

		if (!saveDir.exists)
			saveDir.mkdir()

		val best = DB.getBestDistances(10)
		best foreach (dbo => {
			val dist = dbo.get("dist")
			val operations = dbo.get("operations").asInstanceOf[BasicDBList].toArray.toList
					.asInstanceOf[Seq[String]] map Operations.parse // lord forgive me

			val im = operations
					.foldLeft(new ImageWrapper(image))(
						(im, op) => im.apply(op))

			val fileName = dist.formatted("%.2f" + operations.mkString("_", "-", ""))
			ImageIO.write(im.createImage, "bmp", Paths.get(saveDir.getPath, fileName).toFile)
			info(s"Saved $fileName")
		})


	}
}
