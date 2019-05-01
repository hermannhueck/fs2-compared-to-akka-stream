package fs2guide.ch05chunks

import fs2.{Chunk, Stream}

object App03MapChunks extends App {

  println("\n----- Stream#mapChunks: allows you to directly work on the underlying Array of the Chunks of a Stream")

  val source = Stream.chunk(Chunk.doubles(Array(1.0, 2.0, 3.0)))
  // stream: fs2.Stream[[x]fs2.Pure[x],Double] = Stream(..)
  println(source.toList)

  val mapped = source.mapChunks { chunk: Chunk[Double] =>
    val doubles: Chunk.Doubles = chunk.toDoubles
    /* do things unboxed (directly on the Array[Double] using doubles.{values,size} */
    val values: Array[Double] = doubles.values
    def square(x: Double): Double = x * x
    val mappedArray: Array[Double] = values.map(square)
    Chunk.doubles(mappedArray): Chunk[Double]
  }
  // mapped: fs2.Stream[[x]fs2.Pure[x],Double] = Stream(..)
  println(mapped.toList)

  println("-----\n")
}
