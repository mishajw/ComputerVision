package util.filters

/**
 * Created by misha on 16/11/15.
 */
object FilterFactory {
	abstract class FilterType
	case class Sobel() extends FilterType
	case class Roberts() extends FilterType

	lazy val json = ???

	def getFilter(filterType: FilterType) = filterType match {
		case Sobel() => getFilterByName("sobel")
		case Roberts() => getFilterByName("roberts")
	}

	def getFilterByName(s: String) = {
		
	}
}
