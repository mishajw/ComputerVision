package vision.util

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import vision.analysis.Operations._


object DB {

	val connection = MongoConnection()
	val db = connection("vision")
	val resultsCollection = db("results")

	def insertResults(t: ImageTransformation, nrf: NoiseRemoval, edf: EdgeDetection, fin: FinalThreshold, tpr: Double, fpr: Double, dist: Double) {
		resultsCollection.save(MongoDBObject(
			"transformation" -> t.toString,
			"nrf" -> nrf.toString,
			"edf" -> edf.toString,
			"fin" -> fin.toString,
			"tpr" -> tpr,
			"fpr" -> fpr,
			"dist" -> dist
		))
	}

	def reset() = resultsCollection.remove("_id" $exists true)
}
