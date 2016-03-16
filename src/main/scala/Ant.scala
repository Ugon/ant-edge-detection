import scala.collection.mutable

/**
  * @author Wojciech Pachuta.
  */
class Ant(var position: (Int, Int)) {
  private val visited: mutable.HashSet[(Int, Int)] = mutable.HashSet()
  visited.add(position)

  def visited(coords: (Int, Int)): Boolean = visited.contains(coords)

  def moveTo(coords: (Int, Int)): Unit = {
    visited.add(coords)
    position = coords
  }

  def jumpTo(coords: (Int, Int)): Unit = {
    visited.clear()
    position = coords
  }

}
