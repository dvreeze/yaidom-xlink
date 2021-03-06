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

package eu.cdevreeze.yaidom.xlink

import scala.collection.immutable

import org.scalatest.FunSuite

import eu.cdevreeze.yaidom.core.Declarations
import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.simple.Elem
import eu.cdevreeze.yaidom.simple.ElemBuilder
import eu.cdevreeze.yaidom.simple.NodeBuilder

/**
 * XLink test case.
 *
 * @author Chris de Vreeze
 */
class XLinkTest extends FunSuite {

  test("testRetrieval") {
    doTest(sampleXml.wrappedElem)
  }

  private def doTest(root: Elem): Unit = {
    assertResult(Set(EName("courseload"), EName("tooltip"), EName("person"), EName("course"), EName("gpa"), EName("go"))) {
      val enames = sampleXml.wrappedElem.findAllElemsOrSelf collect { case e => e.resolvedName }
      enames.toSet
    }

    assertResult(Some(sampleXml.wrappedElem)) {
      val result = sampleXml.wrappedElem.findAllElemsOrSelf collect { case e if XLink.mustBeExtendedLink(e) => e }
      result.headOption
    }

    assertResult(Set("students/patjones62.xml", "profs/jaysmith7.xml", "courses/cs101.xml")) {
      val result = sampleXml.wrappedElem.findAllElems collect { case e if XLink.mustBeLocator(e) => Locator(e).href.toString }
      result.toSet
    }

    assertResult(Set("students/patjones62.xml", "profs/jaysmith7.xml", "courses/cs101.xml")) {
      val result = sampleXml.wrappedElem filterElems { e => XLink.mustBeLocator(e) } map { e => Locator(e).href.toString }
      result.toSet
    }

    val fromToHrefPairs: immutable.IndexedSeq[(String, String)] =
      for {
        arc <- sampleXml.wrappedElem.findAllChildElems collect { case e if XLink.mustBeArc(e) && Arc(e).fromOption == Some("CS-101") && Arc(e).toOption == Some("student62") => Arc(e) }
        fromLoc <- sampleXml.wrappedElem.findAllChildElems collect { case e if XLink.mustBeLocator(e) && Locator(e).labelOption == arc.fromOption => Locator(e) }
        toLoc <- sampleXml.wrappedElem.findAllChildElems collect { case e if XLink.mustBeLocator(e) && Locator(e).labelOption == arc.toOption => Locator(e) }
      } yield (fromLoc.href.toString, toLoc.href.toString)

    assertResult(Some("courses/cs101.xml")) {
      fromToHrefPairs.headOption map { _._1 }
    }

    assertResult(Some("students/patjones62.xml")) {
      fromToHrefPairs.headOption map { _._2 }
    }

    assertResult(List("courses/cs101.xml")) {
      sampleXml.labeledLocators.getOrElse("CS-101", Vector()) map { loc => loc.href.toString }
    }
    assertResult(List("courses/cs101.xml")) {
      sampleXml.labeledXLinks.getOrElse("CS-101", Vector()) collect { case loc: Locator => loc.href.toString }
    }

    assertResult(List(QName("gpa"))) {
      sampleXml.labeledResources.getOrElse("PatJonesGPA", Vector()) map { res => res.wrappedElem.qname }
    }
    assertResult(List(QName("gpa"))) {
      sampleXml.labeledXLinks.getOrElse("PatJonesGPA", Vector()) map { link => link.wrappedElem.qname }
    }
  }

  private val sampleXml: ExtendedLink = {
    import NodeBuilder._

    // Example from http://www.w3.org/TR/xlink/ (adapted)
    // In the original example, some xlink attributes are set in the DTD but not in the XML document. That's not supported here.

    val rootBuilder: ElemBuilder =
      elem(
        qname = QName("courseload"),
        attributes = Vector(QName("xlink:type") -> "extended"),
        namespaces = Declarations.from("xlink" -> "http://www.w3.org/1999/xlink"),
        children = Vector(
          textElem(
            qname = QName("tooltip"),
            attributes = Vector(QName("xlink:type") -> "title"),
            txt = "Course Load for Pat Jones"),
          emptyElem(
            qname = QName("person"),
            attributes = Vector(
              QName("xlink:type") -> "locator",
              QName("xlink:href") -> "students/patjones62.xml",
              QName("xlink:label") -> "student62",
              QName("xlink:role") -> "http://www.example.com/linkprops/student",
              QName("xlink:title") -> "Pat Jones")),
          emptyElem(
            qname = QName("person"),
            attributes = Vector(
              QName("xlink:type") -> "locator",
              QName("xlink:href") -> "profs/jaysmith7.xml",
              QName("xlink:label") -> "prof7",
              QName("xlink:role") -> "http://www.example.com/linkprops/professor",
              QName("xlink:title") -> "Dr. Jay Smith")),
          comment(" more remote resources for professors, teaching assistants, etc. "),
          emptyElem(
            qname = QName("course"),
            attributes = Vector(
              QName("xlink:type") -> "locator",
              QName("xlink:href") -> "courses/cs101.xml",
              QName("xlink:label") -> "CS-101",
              QName("xlink:title") -> "Computer Science 101")),
          comment(" more remote resources for courses, seminars, etc. "),
          textElem(
            qname = QName("gpa"),
            attributes = Vector(
              QName("xlink:type") -> "resource",
              QName("xlink:label") -> "PatJonesGPA",
              QName("xlink:role") -> "http://www.example.com/linkprops/gpa"),
            txt = "3.5"),
          emptyElem(
            qname = QName("go"),
            attributes = Vector(
              QName("xlink:type") -> "arc",
              QName("xlink:from") -> "student62",
              QName("xlink:arcrole") -> "", // Required??
              QName("xlink:to") -> "PatJonesGPA",
              QName("xlink:show") -> "new",
              QName("xlink:actuate") -> "onRequest",
              QName("xlink:title") -> "Pat Jones's GPA")),
          emptyElem(
            qname = QName("go"),
            attributes = Vector(
              QName("xlink:type") -> "arc",
              QName("xlink:from") -> "CS-101",
              QName("xlink:arcrole") -> "http://www.example.com/linkprops/auditor",
              QName("xlink:to") -> "student62",
              QName("xlink:show") -> "replace",
              QName("xlink:actuate") -> "onRequest",
              QName("xlink:title") -> "Pat Jones, auditing the course")),
          emptyElem(
            qname = QName("go"),
            attributes = Vector(
              QName("xlink:type") -> "arc",
              QName("xlink:from") -> "student62",
              QName("xlink:arcrole") -> "http://www.example.com/linkprops/advisor",
              QName("xlink:to") -> "prof7",
              QName("xlink:show") -> "replace",
              QName("xlink:actuate") -> "onRequest",
              QName("xlink:title") -> "Dr. Jay Smith, advisor"))))

    val root: Elem = rootBuilder.build()

    ExtendedLink(root)
  }
}
