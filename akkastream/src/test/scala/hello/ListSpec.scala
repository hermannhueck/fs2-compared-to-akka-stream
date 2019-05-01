package hello

import org.scalacheck.Prop._
import org.scalatest.{DiagrammedAssertions, FunSuite}

class ListSpec extends FunSuite with DiagrammedAssertions {

  test("akka-stream: double reversed list should be the original list") {
    forAll { list: List[String] =>
      list.reverse.reverse === list
    }
  }
}
