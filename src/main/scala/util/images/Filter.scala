package util.filters

import util.images.ImageWrapper

abstract class Filter {
	def convolute(image: ImageWrapper): ImageWrapper
}
