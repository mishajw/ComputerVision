package vision.filters

import vision.util.ImageWrapper

abstract class Filter {
	def convolute(image: ImageWrapper): ImageWrapper
}
