import java.io.File

import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.{Pixel, Image}

/**
  * @author Wojciech Pachuta.
  */
object Launcher {

  val inputFileName = "/home/ugon/development/projects/bo/ant-edge-detection/src/main/scala/test.png"
  val outputFileName = "/home/ugon/development/projects/bo/ant-edge-detection/src/main/scala/output.png"

  def main(args: Array[String]): Unit = {
    println("Hello, world!")

    val image: Image = Image.fromFile(new File(inputFileName))

    val (x, y) = image.dimensions

    val imageData: ImageData = new ImageData(image)

    val state: AlgorithmState = AlgorithmState.initialize(imageData)

    for (i <- 0 until 1000) {
      state.makeStep()
      //progress visualisation possible each step
    }

    val pixelsWithMostPheromone = (for(i <- 0 until state.imageData.x; j <- 0 until state.imageData.y) yield ((i, j), state.pheromone(i)(j)))
      .sortBy(_._2).reverse.take(x*y/20).toMap

    val result = imageData.grayImage

    pixelsWithMostPheromone.foreach{case ((x1,y1), value) => result.setPixel(x1, y1, Pixel(255, 0, 0, 255))}

    result.output(new File(outputFileName))(PngWriter())

  }
}