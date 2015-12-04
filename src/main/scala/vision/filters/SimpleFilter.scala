package vision.filters


import vision.util.{ImageWrapper, Matrix}

class SimpleFilter(mask: Matrix) extends Filter {
	override def convolute(image: ImageWrapper): ImageWrapper = convoluteSingle(image, mask)

  override def toString: String = s"SimpleFilter($mask)"
}
