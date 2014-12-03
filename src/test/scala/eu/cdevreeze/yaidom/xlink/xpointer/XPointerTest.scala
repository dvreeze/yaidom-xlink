/*
 * Copyright 2011 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.yaidom.xlink.xpointer

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner

/**
 * XPointer test case.
 *
 * @author Chris de Vreeze
 */
@RunWith(classOf[JUnitRunner])
class XPointerTest extends Suite {

  @Test def testShorthandPointer(): Unit = {
    val xpointer = XPointer.parse("intro")

    assertResult(ShorthandPointer("intro")) {
      xpointer
    }
  }

  @Test def testIdPointer(): Unit = {
    val xpointer = XPointer.parse("element(intro)")

    assertResult(IdPointer("intro")) {
      xpointer
    }
  }

  @Test def testChildSeqPointer(): Unit = {
    val xpointer = XPointer.parse("element(/1/3/15/2)")

    assertResult(ChildSequencePointer(List(1, 3, 15, 2))) {
      xpointer
    }
  }

  @Test def testIdChildSeqPointer(): Unit = {
    val xpointer = XPointer.parse("element(intro/1/3/15/2)")

    assertResult(IdChildSequencePointer("intro", List(1, 3, 15, 2))) {
      xpointer
    }
  }

  @Test def testMultipleXPointers(): Unit = {
    val xpointers = XPointer.parseXPointers("element(intro/1/3/15/2)element(intro)element(/1/3)")

    assertResult(
      List(
        IdChildSequencePointer("intro", List(1, 3, 15, 2)),
        IdPointer("intro"),
        ChildSequencePointer(List(1, 3)))) {

        xpointers
      }
  }
}
