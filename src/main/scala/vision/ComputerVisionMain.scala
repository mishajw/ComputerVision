package vision

import javax.swing._

import grizzled.slf4j.Logging
import vision.filters.FilterFactory
import vision.filters.FilterFactory.{FilterGaussian, FilterSobel}
import vision.util.ImageWrapper

object ComputerVisionMain extends Logging {
	def main(args: Array[String]): Unit = {
		info("Starting...")

		var image = askForImage()
		image.applyThreshold(10)
		image = FilterFactory.getFilter(FilterGaussian).convolute(image)
		image = FilterFactory.getFilter(FilterSobel).convolute(image)
		image.normalise()
		image.applyThreshold(50)
		image.flip()
		image.display()
	}

	private def askForImage() = {

		val isUrl = new JRadioButton("Is this is a URL?")
		val path = new JTextField(20)

		val panel = new JPanel
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
		panel.add(new JLabel("Enter path"))
		panel.add(path)
		panel.add(isUrl)

		JOptionPane.showMessageDialog(null, panel, "Computer Vision", JOptionPane.PLAIN_MESSAGE)

		new ImageWrapper(path.getText, isUrl.isSelected)

	}


}
