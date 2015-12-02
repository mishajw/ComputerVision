package vision.util

import grizzled.slf4j.Logging
import vision.analysis.Operations._
import vision.filters.{Filter, FilterFactory}

object ImageGenerator extends Logging {
  def generateAll(original: ImageWrapper, sample: ImageWrapper): Unit = {
    for (
      t <- TRANSFORMATIONS;
      nrf <- NOISE_REMOVAL.map(FilterFactory.getFilter);
      edf <- EDGE_DETECTORS.map(FilterFactory.getFilter);
      fin <- FINAL_THRESHOLDS) {
      val image = generateImage(original, t, nrf, edf, fin)

      image.createImage
    }
  }

  def generateImage(original: ImageWrapper, transformation: ImageTransformation, nrf: Filter,
                    edf: Filter, fin: FinalThreshold): ImageWrapper = {
    (transformation match {
      case TransformationIntensity => original
      case TransformationBinary(n) => original.applyThreshold(n);
    }).convolute(nrf)
      .convolute(edf)
      .applyThreshold(fin.threshold)
  }
}
