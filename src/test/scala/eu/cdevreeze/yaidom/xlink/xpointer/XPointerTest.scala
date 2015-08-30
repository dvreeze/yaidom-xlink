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
import eu.cdevreeze.yaidom.simple.Document
import eu.cdevreeze.yaidom.parse.DocumentParserUsingSax
import eu.cdevreeze.yaidom.indexed
import XPointer.XPointerAwareDocument
import eu.cdevreeze.yaidom.core.PathBuilder
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Path
import eu.cdevreeze.yaidom.queryapi.XmlBaseSupport

/**
 * XPointer test case.
 *
 * @author Chris de Vreeze
 */
@RunWith(classOf[JUnitRunner])
class XPointerTest extends Suite {

  private val XsNamespace = "http://www.w3.org/2001/XMLSchema"
  private val LinkNamespace = "http://www.xbrl.org/2003/linkbase"

  private val uriResolver = XmlBaseSupport.JdkUriResolver

  private val doc: indexed.Document = {
    val uri = classOf[XPointerTest].getResource("/taxonomyrootdir/www.xbrl.org/2005/xbrldt-2005.xsd").toURI
    val docParser = DocumentParserUsingSax.newInstance
    val doc = docParser.parse(uri)
    indexed.Document.from(doc, uriResolver)
  }

  @Test def testParseShorthandPointer(): Unit = {
    val xpointer = XPointer.parse("intro")

    assertResult(ShorthandPointer("intro")) {
      xpointer
    }
  }

  @Test def testParseIdPointer(): Unit = {
    val xpointer = XPointer.parse("element(intro)")

    assertResult(IdPointer("intro")) {
      xpointer
    }
  }

  @Test def testParseChildSeqPointer(): Unit = {
    val xpointer = XPointer.parse("element(/1/3/15/2)")

    assertResult(ChildSequencePointer(List(1, 3, 15, 2))) {
      xpointer
    }
  }

  @Test def testParseIdChildSeqPointer(): Unit = {
    val xpointer = XPointer.parse("element(intro/1/3/15/2)")

    assertResult(IdChildSequencePointer("intro", List(1, 3, 15, 2))) {
      xpointer
    }
  }

  @Test def testParseMultipleXPointers(): Unit = {
    val xpointers = XPointer.parseXPointers("element(intro/1/3/15/2)element(intro)element(/1/3)")

    assertResult(
      List(
        IdChildSequencePointer("intro", List(1, 3, 15, 2)),
        IdPointer("intro"),
        ChildSequencePointer(List(1, 3)))) {

        xpointers
      }
  }

  @Test def testUseShorthandPointer(): Unit = {
    val xpointer = XPointer.parse("all")

    assertResult(ShorthandPointer("all")) {
      xpointer
    }

    val elemOption = doc.findElemByXPointer(xpointer)

    val scope = Scope.from("xs" -> XsNamespace, "link" -> LinkNamespace)

    assertResult(true) {
      elemOption.isDefined
    }

    assertResult(PathBuilder.from(QName("xs:annotation") -> 0, QName("xs:appinfo") -> 0, QName("link:arcroleType") -> 3).build(scope)) {
      elemOption.get.path
    }
  }

  @Test def testUseIdPointer(): Unit = {
    val xpointer = XPointer.parse("element(notAll)")

    assertResult(IdPointer("notAll")) {
      xpointer
    }

    val elemOption = doc.findElemByXPointer(xpointer)

    val scope = Scope.from("xs" -> XsNamespace, "link" -> LinkNamespace)

    assertResult(true) {
      elemOption.isDefined
    }

    assertResult(PathBuilder.from(
      QName("xs:annotation") -> 0,
      QName("xs:appinfo") -> 0,
      QName("link:arcroleType") -> 4).build(scope)) {

      elemOption.get.path
    }
  }

  @Test def testUseChildSequencePointer(): Unit = {
    val xpointer = XPointer.parse("element(/1/1/1/4)")

    assertResult(ChildSequencePointer(List(1, 1, 1, 4))) {
      xpointer
    }

    val elemOption = doc.findElemByXPointer(xpointer)

    val scope = Scope.from("xs" -> XsNamespace, "link" -> LinkNamespace)

    assertResult(true) {
      elemOption.isDefined
    }

    assertResult(PathBuilder.from(
      QName("xs:annotation") -> 0,
      QName("xs:appinfo") -> 0,
      QName("link:arcroleType") -> 3).build(scope)) {

      elemOption.get.path
    }
  }

  @Test def testUseIdChildSequencePointer(): Unit = {
    val xpointer = XPointer.parse("element(all/2)")

    assertResult(IdChildSequencePointer("all", List(2))) {
      xpointer
    }

    val elemOption = doc.findElemByXPointer(xpointer)

    val scope = Scope.from("xs" -> XsNamespace, "link" -> LinkNamespace)

    assertResult(true) {
      elemOption.isDefined
    }

    assertResult(PathBuilder.from(
      QName("xs:annotation") -> 0,
      QName("xs:appinfo") -> 0,
      QName("link:arcroleType") -> 3,
      QName("link:usedOn") -> 0).build(scope)) {

      elemOption.get.path
    }
  }

  @Test def testUseMultipleXPointers(): Unit = {
    val xpointers = XPointer.parseXPointers("element(intro/1/3/15/2)element(intro)element(all/2)element(/1/10000)")

    assertResult(
      List(
        IdChildSequencePointer("intro", List(1, 3, 15, 2)),
        IdPointer("intro"),
        IdChildSequencePointer("all", List(2)),
        ChildSequencePointer(List(1, 10000)))) {

        xpointers
      }

    val elemOption = doc.findElemByXPointers(xpointers)

    val scope = Scope.from("xs" -> XsNamespace, "link" -> LinkNamespace)

    assertResult(true) {
      elemOption.isDefined
    }

    assertResult(PathBuilder.from(
      QName("xs:annotation") -> 0,
      QName("xs:appinfo") -> 0,
      QName("link:arcroleType") -> 3,
      QName("link:usedOn") -> 0).build(scope)) {

      elemOption.get.path
    }
  }
}
