/**
  * @author Wojciech Pachuta.
  */
class AlgorithmData(val imageData : ImageData) {
  val ants : Array[(Int, Int)] = Array.ofDim(imageData.noAnts)
  val pheromone : Array[Array[Double]] = Array.ofDim(imageData.x, imageData.y)               //deltaI

}
