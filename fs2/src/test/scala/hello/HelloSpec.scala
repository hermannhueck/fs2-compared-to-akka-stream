package hello

import org.scalatest.{DiagrammedAssertions, FunSuite}

class HelloSpec extends FunSuite with DiagrammedAssertions {

  test("fs2: Hello should start with H") {
    assert("Hello".startsWith("H"))
  }
}
