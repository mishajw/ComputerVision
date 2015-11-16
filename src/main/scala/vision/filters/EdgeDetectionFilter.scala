package vision.filters

import grizzled.slf4j.Logging
import vision.filters.FilterFactory.Mask
import vision.util.{Matrix, ImageWrapper}

class EdgeDetectionFilter(xMask: Mask, yMask: Mask) extends Filter with Logging {

	override def convolute(image: ImageWrapper): ImageWrapper = {
		val convX = convoluteSingle(image, xMask)
		debug("Convoluted in X direction")
		val convY = convoluteSingle(image, yMask)
		debug("Convoluted in Y direction")

		combineFast(convX, convY)
	}

	override def toString: String = "X: " + xMask + "\nY: " + yMask
}
