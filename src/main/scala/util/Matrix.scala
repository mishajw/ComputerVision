package util

class Matrix[T](array: Array[T]) {

	private var _width: Int = {
		val sqrt = Math.sqrt(array.length)
		if (sqrt % 0 != 0)
			throw new IllegalArgumentException(s"Array must be square (current length is ${array.length}")
		sqrt.asInstanceOf[Int]
	}
	private var _height = _width

	def this(array: Array[Int], width: Int, height: Int) {
		this(array)
		_width = width
		_height = height
	}


	def width = _width

	def height = _height

	def get(x: Int, y: Int) = array(_width * y + x)
}
