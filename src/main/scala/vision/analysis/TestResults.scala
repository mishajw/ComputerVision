package vision.analysis

class TestResults(var tp: Double, var tn: Double, var fp: Double, var fn: Double) {
	def sensitivity = tp / (tp + fn)

	def specificity = tn / (tn + fp)

	def tpr = sensitivity

	def fpr = 1 - specificity

	def dist = Math.sqrt(
		Math.pow(1 - sensitivity, 2) +
			Math.pow(1 - specificity, 2))


	override def toString = f"TestResults(sensitivity=$sensitivity%.3f, " +
			f"specificity=$specificity%.3f, " +
			f"tpr=$tpr%.3f, " +
			f"fpr=$fpr%.3f, " +
			f"dist=$dist%.3f)"
}
