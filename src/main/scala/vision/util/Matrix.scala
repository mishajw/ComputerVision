package vision.util

import scala.collection.mutable.ArrayBuffer

class Matrix[T](matrixArray: ArrayBuffer[T], matrixWidth: Int, matrixHeight: Int) {
	private var _width = matrixWidth

	private var _height = matrixHeight

	val _array = matrixArray

	def array = _array

	def this(array: ArrayBuffer[T]) {
		this(array, 0, 0)

		val sqrt = Math.sqrt(array.length)
		if (sqrt % 0 != 0)
			throw new IllegalArgumentException(s"Array must be square (current length is ${array.length})")
		_width = sqrt.asInstanceOf[Int]
		_height = _width
	}

	def this(array: Array[T]) {
		this(new ArrayBuffer[T](array.length))
		array.copyToBuffer(this.array)
	}

	def this(array:Array[T], width:Int, height:Int) {
		this(new ArrayBuffer[T](array.length), width, height)
		array.copyToBuffer(this.array)
	}

	def width = _width

	def height = _height

	def get(x: Int, y: Int, default: T): T = {
		if (x < 0 || x >= _width || y < 0 || y >= _height)
			return default

		get(x, y)
	}

	def get(x: Int, y: Int) = matrixArray(_width * y + x)

	def set(x: Int, y: Int, value: T) = matrixArray(_width * y + x) = value

	override def clone: Matrix[T] = {
		val newArray = new ArrayBuffer[T](matrixArray.length)
		array.copyToBuffer(newArray)

		new Matrix[T](newArray, _width, _height)
	}

	override def toString: String = {
    "\n" + array.indices.map(i => f"${array(i).asInstanceOf[Double]}%.3f" + {
      if (i % height == 0) "]\n[" else ", "
    }).mkString
  }
}
