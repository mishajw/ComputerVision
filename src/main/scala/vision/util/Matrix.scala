package vision.util

class Matrix(matrixArray: Array[Double], matrixWidth: Int, matrixHeight: Int) {
	private var _width = matrixWidth

	private var _height = matrixHeight

	val _array = matrixArray

	def array = _array

	//	def this(array: Array[Double]) {
	//		this(array, 0, 0)
	//
	//		val sqrt = Math.sqrt(array.length)
	//		if (sqrt % 0 != 0)
	//			throw new IllegalArgumentException(s"Array must be square (current length is ${array.length})")
	//		_width = sqrt.asInstanceOf[Int]
	//		_height = _width
	//	}
	//
	//	def this(array: Array[Double]) {
	//		this(new Array[Double](array.length))
	//		array.copyToArray(this.array)
	//	}
	//
	//	def this(array: Array[Double], width: Int, height: Int) {
	//		this(new Array[Double](array.length), width, height)
	//		array.copyToArray(this.array)
	//	}

	def width = _width

	def height = _height

	def get(x: Int, y: Int, default: Double): Double = {
		if (x < 0 || x >= _width || y < 0 || y >= _height)
			return default

		get(x, y)
	}

	def get(x: Int, y: Int) = matrixArray(_width * y + x)

	def set(x: Int, y: Int, value: Double) = matrixArray(_width * y + x) = value

	override def clone: Matrix = {
		val newArray = new Array[Double](matrixArray.length)
		array.copyToArray(newArray)

		new Matrix(newArray, _width, _height)
	}

	override def toString: String = {
    "\n" + array
			.grouped(width)
			.map(_.map(e => f"$e%.3f")
						.mkString(", "))
			.mkString("\n")
  }
}
