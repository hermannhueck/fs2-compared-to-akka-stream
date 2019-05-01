package hello

import org.scalatest.{DiagrammedAssertions, FunSuite}

class HelloSpec extends FunSuite with DiagrammedAssertions {

  test("akka-stream: Hello should start with H") {
    assert("Hello".startsWith("H"))
  }
}
