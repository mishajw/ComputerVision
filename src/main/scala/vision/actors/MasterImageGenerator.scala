package vision.actors

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinRouter
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication.{Images, ImageDetails, ImageDone, PrintFrequency}

class MasterImageGenerator extends Actor with Logging {

	val router = context.actorOf(Props[WorkerImageGenerator].withRouter(RoundRobinRouter(nrOfInstances = 5)), "router")
//	val router = context.actorOf(BalancingPool(5).props(Props[WorkerImageGenerator]), "router")

	lazy val startTime = System.currentTimeMillis()
	var amountDone = 0
	var total = 0

	override def receive = {
		case ImageDetails(original, sample, t, nrf, edf, fin) =>
			debug("Got image details")
			router ! ImageDetails(original, sample, t, nrf, edf, fin)
		case Images(details) =>
			details.foreach(router !)
			total += details.size
		case ImageDone =>
			debug("Image done")
			amountDone += 1
		case PrintFrequency =>
			amountDone match {
				case 0 =>
					info("Not done work yet")
				case n =>
					val timePassed = (System.currentTimeMillis() - startTime).toDouble / 1000d

					info("=== Stats ===")
					info(f"Time passed: $timePassed")
					info(f"Amount: $amountDone/$total (${amountDone.toDouble / total * 100}%.2f%%)")
					info(f"Frequency: ${amountDone.toDouble / timePassed.toDouble}%.3f per sec")
					info("")
			}
	}
}
