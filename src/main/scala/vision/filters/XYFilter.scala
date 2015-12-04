package vision.filters

import grizzled.slf4j.Logging
import vision.util.{ImageWrapper, Matrix}

class XYFilter(xMask: Matrix, yMask: Matrix) extends Filter with Logging {

	override def convolute(image: ImageWrapper): ImageWrapper = {
		debug("Convoluting in X direction")
		val convX = convoluteSingle(image, xMask)

		debug("Convoluting in Y direction")
		val convY = convoluteSingle(image, yMask)

		combineFast(convX, convY)
	}

	override def toString: String = "X: " + xMask + "\nY: " + yMask
}
