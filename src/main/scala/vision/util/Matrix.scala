package vision.util

class Matrix[T](array: Array[T], matrixWidth: Int, matrixHeight: Int) {

	private var _width = matrixWidth
	private var _height = matrixHeight

	def this(array: Array[T]) {
		this(array, 0, 0)

		val sqrt = Math.sqrt(array.length)
		if (sqrt % 0 != 0)
			throw new IllegalArgumentException(s"Array must be square (current length is ${array.length})")
		_width = sqrt.asInstanceOf[Int]
		_height = _width
	}


	def width = _width

	def height = _height

	def get(x: Int, y: Int) = array(_width * y + x)
}
