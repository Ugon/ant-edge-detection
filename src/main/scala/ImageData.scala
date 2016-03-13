/**
  * @author Wojciech Pachuta.
  */

class ImageData(val x: Int, val y: Int) {
  /*                x
       ------------->
     | .............
     | .............
     | ...pixels....
     | .............
     | .............
     | .............
   y v                */

  val matrix : Array[Array[Int]] = Array.ofDim(x, y)                        //I
  val grayMax : Int = matrix.flatten.max                                    //Imax
  val grayGradient : Array[Array[Double]] = Array.ofDim(x, y)               //deltaI
  val noAnts : Int = Math.sqrt(x * y).ceil.toInt

  for(i <- 0 until x) {
    grayGradient(i)(0) = matrix(i)(0)
    grayGradient(i)(y - 1) = matrix(i)(y - 1)
  }
  for(j <- 0 until y) {
    grayGradient(0)(j) = matrix(0)(j)
    grayGradient(x - 1)(j) = matrix(x - 1)(j)
  }
  for(i <- 1 until x - 1; j <- 1 until y - 1){
    val arr : Array[Int] = Array(
      Math.abs(matrix(i    )(j - 1) - matrix(i    )(j + 1)),
      Math.abs(matrix(i - 1)(j    ) - matrix(i + 1)(j    )),
      Math.abs(matrix(i + 1)(j - 1) - matrix(i - 1)(j + 1)),
      Math.abs(matrix(i - 1)(j - 1) - matrix(i + 1)(j + 1))
    )
    grayGradient(i)(j) = arr.max.toDouble / grayMax
  }

}
