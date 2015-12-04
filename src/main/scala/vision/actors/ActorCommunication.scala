package vision.actors

import vision.analysis.Operations._
import vision.util.ImageWrapper

object ActorCommunication {

	sealed trait Message

	case class ImageDetails(original: ImageWrapper, sample: ImageWrapper, operations: Array[Operation]) extends Message

	case class Images(details: List[ImageDetails]) extends Message

	case object ImageDone extends Message

	case object PrintFrequency extends Message

}