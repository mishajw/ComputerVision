package vision.actors

import vision.analysis.Operations.{FinalThreshold, EdgeDetection, NoiseRemoval, ImageTransformation}
import vision.util.ImageWrapper

object ActorCommunication {

	sealed trait Message

	case class ImageDetails(original: ImageWrapper, sample: ImageWrapper,
													t: ImageTransformation, nrf: NoiseRemoval, edf: EdgeDetection, fin: FinalThreshold) extends Message

	case class Images(details: List[ImageDetails]) extends Message

	case object ImageDone extends Message

	case object PrintFrequency extends Message

}