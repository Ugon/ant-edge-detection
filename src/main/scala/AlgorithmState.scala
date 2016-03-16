import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.collection.mutable

/**
  * @author Wojciech Pachuta.
  */
class AlgorithmState private(val imageData: ImageData) {
  val ants: Array[Ant] = Array.ofDim(imageData.noAnts)
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
          val a = Math.pow(pheromone(newX)(newY), AlgorithmParams.alpha)
          val b = Math.pow(imageData.grayGradient(newX)(newY), AlgorithmParams.beta)
          val prod = Math.pow(pheromone(newX)(newY), AlgorithmParams.alpha) *
            Math.pow(imageData.grayGradient(newX)(newY), AlgorithmParams.beta)
          probabilities(i + 1)(j + 1) = prod
          sum += prod
          possibilities.append((i + 1, j + 1))
        }
        else {
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
          //if movement possible and pheromone nearby
          val (dx, dy) = sample(coordPossibMap)
          ant.moveTo((currX + dx - 1, currY + dy - 1))
          newPixelsOccupied.add((currX + dx - 1, currY + dy - 1))
          antMoved = true
        }
        else if (possibilities.nonEmpty) {
          //if movement possible but no pheromone nearby
          val (dx, dy) = Random.shuffle(possibilities).head
          ant.moveTo((currX + dx - 1, currY + dy - 1))
          newPixelsOccupied.add((currX + dx - 1, currY + dy - 1))
          antMoved = true
        }
      }

      if (!antMoved){
        //if movement impossible
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
      pheromone(i)(j) *= (1 - AlgorithmParams.evaporationRate)
    }
    newPixelsOccupied.foreach { case (x, y) =>
      val grad: Double = imageData.grayGradient(x)(y)
      pheromone(x)(y) += (if (grad > AlgorithmParams.minGradientForPheromone) grad else 0)
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
}

object AlgorithmState {
  def initialize(imageData: ImageData): AlgorithmState = {
    val algorithmState = new AlgorithmState(imageData)

    //select random positions for ants
    for (i <- 0 until imageData.noAnts) {
      var newCoords: (Int, Int) = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
      while (algorithmState.pixelsOccupied.contains(newCoords)) {
        newCoords = (Random.nextInt(imageData.x), Random.nextInt(imageData.y))
      }
      algorithmState.ants(i) = new Ant(newCoords)
    }

    //initialize pheromone
    for (i <- 0 until imageData.x; j <- 0 until imageData.y) {
      algorithmState.pheromone(i)(j) = AlgorithmParams.initialPheromone
    }

    algorithmState
  }


}
