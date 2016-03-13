/**
  * @author Wojciech Pachuta.
  */
object Algorithm {
  //parameters
  val alpha: Double = 0.5
  val beta: Double = 0.5
  val evaporationRate: Double = 0.5

  def step(input: AlgorithmData): AlgorithmData = {
    val output: AlgorithmData = new AlgorithmData(input.imageData)

    //ant movement
    for (antIndex <- 0 until input.imageData.noAnts) {
      def isAllowed(x: Int, y: Int): Boolean = x > 0 && y > 0 && x < input.imageData.x && y < input.imageData.y

      val (currX, currY) = input.ants(antIndex)
      val probabilities: Array[Array[Double]] = Array.ofDim(3, 3)
      var sum: Double = 0

      //movement direction probability calculation
      for (i <- -1 to 1; j <- -1 to 1; i != j) { //if i != j then ant has to move
        val newX = currX + i
        val newY = currY + j

        if (isAllowed(newX, newY)) {
          val prod = Math.pow(input.pheromone(newX)(newY), Algorithm.alpha) *
            Math.pow(input.imageData.grayGradient(newX)(newY), Algorithm.beta)
          probabilities(i + 1)(j + 1) = prod
          sum += prod
        }
        else {
          probabilities(i + 1)(j + 1) = 0
        }
      }

      //movement
      if (sum != 0) {
        val tupleSeq : Seq[((Int, Int), Double)] = for (i <- -1 to 1; j <- -1 to 1; i != j) yield ((i + 1, j + 1), probabilities(i + 1)(j + 1) / sum)
        val map : Map[(Int, Int), Double] = tupleSeq.toMap
        val nextCoords = sample(map)
        output.ants(antIndex) = nextCoords
      }
      else {
        output.ants(antIndex) = input.ants(antIndex)
      }
    }

    //pheromone calculation
    input.ants.foreach{case (antX, antY) =>
      output.pheromone(antX)(antY) += input.imageData.grayGradient(antX)(antY)
    }
    for(i <- 0 until input.imageData.x; j <- 0 until input.imageData.y){
      output.pheromone(i)(j) += (1 - Algorithm.evaporationRate) * input.pheromone(i)(j)
    }

    output
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
    sys.error(f"this should never happen")  // needed so it will compile
  }
}
