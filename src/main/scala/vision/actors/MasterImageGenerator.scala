package vision.actors

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinRouter
import grizzled.slf4j.Logging
import vision.actors.ActorCommunication._

class MasterImageGenerator extends Actor with Logging {

	info("Starting masters")

	val router = context.actorOf(Props[WorkerImageGenerator].withRouter(RoundRobinRouter(nrOfInstances = 5)), "router")
//	val router = context.actorOf(BalancingPool(5).props(Props[WorkerImageGenerator]), "router")

	lazy val startTime = System.currentTimeMillis()
	var amountDone = 0
	var total = 0

	override def receive = {
		case ImageDetails(original, sample, operations) =>
			debug("Got image details")
			router ! ImageDetails(original, sample, operations)
			total += 1
		case Images(details) =>
			debug("Got multiple image details")
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
					val percent = amountDone.toDouble / total * 100
					info(f"Amount: $amountDone/$total ($percent%.2f%%)")
					info(f"Frequency: ${amountDone.toDouble / timePassed.toDouble}%.3f per sec")
					info("")
					sender ! percent
			}
	}
}
