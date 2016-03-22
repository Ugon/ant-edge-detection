package algorithm

import java.io.File

import com.sksamuel.scrimage.Image

/**
  * @author Wojciech Pachuta.
  */
class AntAPI(inputImageFile: File,
             alpha: Double,
             beta: Double,
             evaporationRate: Double,
             gradientThreshold: Double,
             initialPheromone: Double,
             numberOfAntsMultiplier: Double) {
  private val inputImage: Image = Image.fromFile(inputImageFile)
  private val imageData: ImageData = new ImageData(inputImage, numberOfAntsMultiplier)
  private val algorithmState: AlgorithmState = AlgorithmState.initialize(imageData, new AlgorithmParams(
    alpha, beta, evaporationRate, gradientThreshold, initialPheromone))

  def setAlpha(value: Double) = algorithmState.params.alpha = value

  def setBeta(value: Double) = algorithmState.params.beta = value

  def setEvaporationRate(value: Double) = algorithmState.params.evaporationRate = value

  def setGradientThreshold(value: Double) = algorithmState.params.gradientThreshold = value

  def setNumberOfAntsMultiplier(value: Double) = algorithmState.changeNumberOfAntsMultiplier(value)

  def getPheromone: Array[Array[Double]] = algorithmState.pheromone

  def getStrongestPheromone(pheromoneCoverage: Double): Array[(Int, Int)] =
    (for(i <- 0 until algorithmState.imageData.x; j <- 0 until algorithmState.imageData.y) yield ((i, j), algorithmState.pheromone(i)(j)))
    .sortBy(_._2).map(_._1).reverse.take((imageData.x * imageData.y * pheromoneCoverage).ceil.toInt).toArray

  def getAnts: Array[(Int, Int)] = algorithmState.ants.map(_.position).toArray

  def makeSteps(number: Int): Unit = (0 until number).foreach(_ => algorithmState.makeStep())

}