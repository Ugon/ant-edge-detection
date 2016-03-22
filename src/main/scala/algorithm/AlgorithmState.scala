package algorithm

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
  * @author Wojciech Pachuta.
  */
class AlgorithmState private(val imageData: ImageData, val params: AlgorithmParams) {
  val ants: ListBuffer[Ant] = ListBuffer()
  private var pixelsOccupied: mutable.HashSet[(Int, Int)] = mutable.HashSet()
  val pheromone: Array[Array[Double]] = Array.ofDim(imageData.x, imageData.y)

  def makeStep(): Unit = {
    val newPixelsOccupied: mutable.HashSet[(Int, Int)] = mutable.HashSet()

    //ant movement
    ants.foreach { ant =>
      val (currX, currY) = ant.position

      val possibilities: ListBuffer[(Int, Int)] = ListBuffer()
      val probabilities: Array[Array[Double]] = Array.ofDim(3, 3)
      var sum: Double = 0

      //movement direction probability calculation
      for (i <- -1 to 1; j <- -1 to 1; if i != j) {
        val newCoords@(newX, newY) = (currX + i, currY + j)
        if (imageData.isPixelWithin(newCoords) && !ant.visited(newCoords) && !newPixelsOccupied.contains(newCoords)) {
          //if pixel allowed
          val prod = Math.pow(pheromone(newX)(newY), params.alpha) *
            Math.pow(imageData.grayGradient(newX)(newY), params.beta)
          probabilities(i + 1)(j + 1) = prod
          sum += prod
          possibilities.append((i + 1, j + 1))
        }
        else {
          //if pixel not allowed
          probabilities(i + 1)(j + 1) = 0
        }
      }


      //movement
      var antMoved = false
      if (sum > 0) {
        val tupleSeq: Seq[((Int, Int), Double)] =
          for (i <- -1 to 1; j <- -1 to 1; if probabilities(i + 1)(j + 1) > 0)
            yield ((i + 1, j + 1), probabilities(i + 1)(j + 1) / sum)

        val coordPossibMap: Map[(Int, Int), Double] = tupleSeq.toMap
        if (coordPossibMap.nonEmpty) {
          //if movement possible and pheromone nearby go to pheromone pixel
          val (dx, dy) = sample(coordPossibMap)
          ant.moveTo((currX + dx - 1, currY + dy - 1))
          newPixelsOccupied.add((currX + dx - 1, currY + dy - 1))
          antMoved = true
        }
        else if (possibilities.nonEmpty) {
          //if movement possible but no pheromone nearby go to random pixel
          val (dx, dy) = Random.shuffle(possibilities).head
          ant.moveTo((currX + dx - 1, currY + dy - 1))
          newPixelsOccupied.add((currX + dx - 1, currY + dy - 1))
          antMoved = true
        }
      }

      if (!antMoved) {
        var chosenCoords: (Int, Int) = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
        while (newPixelsOccupied.contains(chosenCoords)) {
          chosenCoords = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
        }
        ant.jumpTo(chosenCoords)
        newPixelsOccupied.add(chosenCoords)
      }
    }

    //pheromone calculation
    for (i <- 0 until imageData.x; j <- 0 until imageData.y) {
      pheromone(i)(j) *= (1 - params.evaporationRate)
    }
    newPixelsOccupied.foreach { case (x, y) =>
      val grad: Double = imageData.grayGradient(x)(y)
      pheromone(x)(y) += (if (grad > params.gradientThreshold) grad else 0)
    }

    pixelsOccupied = newPixelsOccupied
  }

  private def sample[A](dist: Map[A, Double]): A = {
    val p = scala.util.Random.nextDouble
    val it = dist.iterator
    var accum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      accum += itemProb
      if (accum >= p)
        return item
    }
    Random.shuffle(dist.keys).head // needed so it will compile
  }

  def changeNumberOfAntsMultiplier(value: Double) = {
    if (value != imageData.numberOfAntsMultiplier) {
      imageData.numberOfAntsMultiplier = value
      if (ants.length > imageData.noAnts) {
        ants.remove(0, ants.length - imageData.noAnts)
      }
      else {
        val len = ants.length
        for (i <- len until imageData.noAnts) {
          var newCoords: (Int, Int) = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
          while (pixelsOccupied.contains(newCoords)) {
            newCoords = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
          }
          ants.append(new Ant(newCoords))
        }
      }

    }
  }
}

object AlgorithmState {
  def initialize(imageData: ImageData, params: AlgorithmParams): AlgorithmState = {
    val algorithmState = new AlgorithmState(imageData, params)

    //select random positions for ants
    for (i <- 0 until imageData.noAnts) {
      var newCoords: (Int, Int) = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
      while (algorithmState.pixelsOccupied.contains(newCoords)) {
        newCoords = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
      }
      algorithmState.ants.append(new Ant(newCoords))
    }

    //initialize pheromone
    for (i <- 0 until imageData.x; j <- 0 until imageData.y) {
      algorithmState.pheromone(i)(j) = params.initialPheromone
    }

    algorithmState
  }

}
