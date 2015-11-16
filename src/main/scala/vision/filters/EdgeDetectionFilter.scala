package vision.filters

import vision.util.{Matrix, ImageWrapper}

class EdgeDetectionFilter(xMask: Matrix[Double], yMask: Matrix[Double]) extends Filter {


	override def convolute(image: ImageWrapper): ImageWrapper = ???


}
