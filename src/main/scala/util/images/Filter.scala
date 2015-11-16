package util.images

abstract class Filter {
	def convolute(image: ImageWrapper): ImageWrapper
}
