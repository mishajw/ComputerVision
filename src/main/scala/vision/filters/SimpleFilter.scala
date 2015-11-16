package vision.filters

import vision.filters.FilterFactory.Mask
import vision.util.ImageWrapper

class SimpleFilter(mask: Mask) extends Filter {
	override def convolute(image: ImageWrapper): ImageWrapper = convoluteSingle(image, mask)
}
