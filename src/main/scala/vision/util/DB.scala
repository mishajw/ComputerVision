package vision.util

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import vision.analysis.Operations._


object DB {

	val connection = MongoConnection()
	val db = connection("vision")
	val resultsCollection = db("results")

	def insertResults(tpr: Double, fpr: Double, dist: Double, operations: Operation*) {
		resultsCollection.save(MongoDBObject(
			"tpr" -> tpr,
			"fpr" -> fpr,
			"dist" -> dist,
			"operations" -> operations.map(_.toString)
		))
	}

	def reset() = resultsCollection.remove("_id" $exists true)
}
