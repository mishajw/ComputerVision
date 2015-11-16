package util

class Matrix(matrixWidth: Int, matrixHeight: Int, array: Array[Int]) {

	def width = matrixWidth
	def height = matrixHeight

	def get(x: Int, y: Int) = array(matrixWidth * y + x)
}
