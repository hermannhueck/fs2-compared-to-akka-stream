package hello

import org.scalacheck.Prop._
import org.scalatest.{DiagrammedAssertions, FunSuite}

class ListSpec extends FunSuite with DiagrammedAssertions {

  test("fs2: double reversed list should be the original list") {
    forAll { list: List[String] =>
      list.reverse.reverse === list
    }
  }
}
