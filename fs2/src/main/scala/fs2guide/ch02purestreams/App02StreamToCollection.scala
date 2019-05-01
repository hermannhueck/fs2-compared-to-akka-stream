package fs2guide.ch02purestreams

import fs2.{Pure, Stream}

object App02StreamToCollection extends App {

  println("\n-----")

  val stream: Stream[Pure, Int] = Stream(1, 2, 3)


  // Converting a pure stream to a List
  val list = stream.toList
  // list: List[Int] = List(1, 2, 3)
  println(list)

  // Converting a pure stream to a Vector
  val vector = stream.toVector
  // vector: Vector[Int] = Vector(1, 2, 3)
  println(vector)

  // Converting a pure stream to a fs2.Chunk
  val chunk = stream.toChunk
  // chunk: Chunk[Int] = Chunk(1, 2, 3)
  println(chunk)

  println("-----\n")
}
