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

package eu.cdevreeze.yaidom.xlink.link

import scala.Vector
import scala.reflect.classTag

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner

import eu.cdevreeze.yaidom.bridge.DefaultIndexedBridgeElem
import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.indexed
import eu.cdevreeze.yaidom.parse.DocumentParserUsingDom
import eu.cdevreeze.yaidom.queryapi.XmlBaseSupport
import eu.cdevreeze.yaidom.resolved
import eu.cdevreeze.yaidom.simple
import eu.cdevreeze.yaidom.xlink.xl

/**
 * Linkbase test case, using data from the XBRL Conformance Suite.
 *
 * @author Chris de Vreeze
 */
@RunWith(classOf[JUnitRunner])
class LinkbaseTest extends Suite {

  private val uriResolver = XmlBaseSupport.JdkUriResolver

  @Test def testReconstructLabelLinkbase(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/202-01-HrefResolution-label.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    val labelLinks = linkbase.labelLinks

    assertResult(1) {
      labelLinks.size
    }

    val labelLink = labelLinks(0)

    assertResult(6) {
      labelLink.xlinkChildren.size
    }

    val labelArcs = labelLink.labelArcs
    val locators = labelLink.locators
    val labelResources = labelLink.labelResources

    assertResult("http://www.xbrl.org/2003/role/link") {
      labelLink.role
    }
    assertResult(Set("http://www.xbrl.org/2003/role/link")) {
      (labelArcs.map(_.elr) ++ locators.map(_.elr) ++ labelResources.map(_.elr)).toSet
    }

    import simple.Node._

    val scope = linkbase.scope ++ Scope.from("xlink" -> xl.XLink.XLinkNamespace, "link" -> LinkNamespace)

    assertResult(Set("http://www.xbrl.org/2003/linkbase")) {
      linkbase.findAllElemsOrSelf.flatMap(_.scope.defaultNamespaceOption).toSet
    }

    val locs = locators map { loc =>
      emptyElem(QName("link:loc"), scope).
        plusAttribute(QName("xlink:type"), loc.xlinkType.toString).
        plusAttribute(QName("xlink:href"), loc.href.toString).
        plusAttribute(QName("xlink:label"), loc.label).
        plusAttributeOption(QName("xlink:title"), loc.titleOption)
    }

    val arcs = labelArcs map { arc =>
      emptyElem(QName("link:labelArc"), scope).
        plusAttribute(QName("xlink:type"), arc.xlinkType.toString).
        plusAttribute(QName("xlink:from"), arc.from).
        plusAttribute(QName("xlink:to"), arc.to).
        plusAttribute(QName("xlink:arcrole"), arc.arcrole).
        plusAttributeOption(QName("xlink:show"), arc.showOption).
        plusAttributeOption(QName("xlink:actuate"), arc.actuateOption).
        plusAttributeOption(QName("xlink:title"), arc.titleOption)
    }

    val labels = labelResources map { label =>
      textElem(QName("link:label"), scope, label.text).
        plusAttribute(QName("xlink:type"), label.xlinkType.toString).
        plusAttribute(QName("xlink:label"), label.label).
        plusAttributeOption(QName("xlink:role"), label.roleOption).
        plusAttributeOption(QName("xlink:title"), label.titleOption).
        plusAttributeOption(QName("xml:lang"), label.langOption)
    }

    val lb =
      elem(
        QName("link:linkbase"),
        scope,
        Vector(
          elem(
            QName("link:labelLink"),
            Vector(QName("xlink:type") -> labelLink.xlinkType.toString, QName("xlink:role") -> labelLink.role),
            scope,
            locs ++ labels ++ arcs)))

    val original = resolved.Elem(linkbase.bridgeElem.toElem).removeAllInterElementWhitespace

    val reconstructed = resolved.Elem(lb).removeAllInterElementWhitespace

    assertResult(original.resolvedName) {
      reconstructed.resolvedName
    }
    assertResult(original.findAllElemsOrSelf.size) {
      reconstructed.findAllElemsOrSelf.size
    }
    assertResult(original.findAllChildElems.map(e => e.withChildren(Vector())).toSet) {
      reconstructed.findAllChildElems.map(e => e.withChildren(Vector())).toSet
    }
    assertResult(original.findAllChildElems.flatMap(_.findAllElems).toSet) {
      reconstructed.findAllChildElems.flatMap(_.findAllElems).toSet
    }
  }

  @Test def testReconstructReferenceLinkbase(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/291-11-ArcOverrideReferenceLinkbases-2-reference.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    val referenceLinks = linkbase.referenceLinks

    assertResult(1) {
      referenceLinks.size
    }

    val referenceLink = referenceLinks(0)

    assertResult(5) {
      referenceLink.xlinkChildren.size
    }

    val referenceArcs = referenceLink.referenceArcs
    val locators = referenceLink.locators
    val referenceResources = referenceLink.referenceResources

    assertResult("http://www.xbrl.org/2003/role/link") {
      referenceLink.role
    }
    assertResult(Set("http://www.xbrl.org/2003/role/link")) {
      (referenceArcs.map(_.elr) ++ locators.map(_.elr) ++ referenceResources.map(_.elr)).toSet
    }

    import simple.Node._

    val scope = linkbase.scope ++ Scope.from(
      "xlink" -> xl.XLink.XLinkNamespace,
      "link" -> LinkNamespace,
      "ref" -> "http://mycompany.com/xbrl/taxonomy")

    assertResult(Set("http://www.xbrl.org/2003/linkbase")) {
      linkbase.findAllElemsOrSelf.flatMap(_.scope.defaultNamespaceOption).toSet
    }

    val locs = locators map { loc =>
      emptyElem(QName("link:loc"), scope).
        plusAttribute(QName("xlink:type"), loc.xlinkType.toString).
        plusAttribute(QName("xlink:href"), loc.href.toString).
        plusAttribute(QName("xlink:label"), loc.label).
        plusAttributeOption(QName("xlink:title"), loc.titleOption)
    }

    val arcs = referenceArcs map { arc =>
      emptyElem(QName("link:referenceArc"), scope).
        plusAttribute(QName("xlink:type"), arc.xlinkType.toString).
        plusAttribute(QName("xlink:from"), arc.from).
        plusAttribute(QName("xlink:to"), arc.to).
        plusAttribute(QName("xlink:arcrole"), arc.arcrole).
        plusAttributeOption(QName("xlink:show"), arc.showOption).
        plusAttributeOption(QName("xlink:actuate"), arc.actuateOption).
        plusAttributeOption(QName("xlink:title"), arc.titleOption).
        plusAttributeOption(QName("use"), arc.attributeOption(UseEName)).
        plusAttributeOption(QName("priority"), arc.attributeOption(PriorityEName))
    }

    val references = referenceResources map { ref =>
      emptyElem(QName("link:reference"), scope).
        plusAttribute(QName("xlink:type"), ref.xlinkType.toString).
        plusAttribute(QName("xlink:label"), ref.label).
        plusAttributeOption(QName("xlink:role"), ref.roleOption).
        withChildren(ref.findAllChildElems.map(_.bridgeElem.toElem))
    }

    val lb =
      elem(
        QName("link:linkbase"),
        scope,
        Vector(
          elem(
            QName("link:referenceLink"),
            Vector(QName("xlink:type") -> referenceLink.xlinkType.toString, QName("xlink:role") -> referenceLink.role),
            scope,
            locs ++ references ++ arcs)))

    val original = resolved.Elem(linkbase.bridgeElem.toElem).removeAllInterElementWhitespace

    val reconstructed = resolved.Elem(lb).removeAllInterElementWhitespace

    assertResult(original.resolvedName) {
      reconstructed.resolvedName
    }
    assertResult(original.findAllElemsOrSelf.size) {
      reconstructed.findAllElemsOrSelf.size
    }
    assertResult(original.findAllChildElems.map(e => e.withChildren(Vector())).toSet) {
      reconstructed.findAllChildElems.map(e => e.withChildren(Vector())).toSet
    }
    assertResult(original.findAllChildElems.flatMap(_.findAllElems).toSet) {
      reconstructed.findAllChildElems.flatMap(_.findAllElems).toSet
    }
  }

  @Test def testMultipleDefinitionLinks(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/DecArcCyclesUD_definition.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    val extendedLinks = linkbase.extendedLinks

    val definitionLinks = linkbase.definitionLinks

    assertResult(12) {
      extendedLinks.size
    }
    assertResult(12) {
      definitionLinks.size
    }
    assertResult(extendedLinks) {
      definitionLinks
    }

    assertResult(true) {
      definitionLinks.forall(_.role == "http://www.xbrl.org/2003/role/link")
    }
    assertResult(true) {
      definitionLinks.flatMap(_.locators).forall(_.elr == "http://www.xbrl.org/2003/role/link")
    }
    assertResult(true) {
      definitionLinks.flatMap(_.arcs).forall(_.elr == "http://www.xbrl.org/2003/role/link")
    }

    assertResult(true) {
      definitionLinks.map(_.labeledResources).flatMap(_.keySet).isEmpty
    }
    assertResult(true) {
      Set("conceptA", "conceptB", "conceptH").subsetOf(definitionLinks.map(_.labeledLocators).flatMap(_.keySet).toSet)
    }

    assertResult(Set("http://mycompany.com/xbrl/decArcCyclesUD/undirected")) {
      definitionLinks.flatMap(_.arcs).map(_.arcrole).toSet
    }
  }

  @Test def testMultiplePresentationLinks(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/ArcRoleDR_presentation.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    assertResult(2) {
      linkbase.findAllChildElemsOfType(classTag[ArcroleRef]).size
    }

    def presentationLinks = linkbase.presentationLinks

    assertResult(2) {
      presentationLinks.size
    }

    assertResult(linkbase.findAllChildElemsOfType(classTag[ArcroleRef]).map(_.arcroleUri).toSet) {
      presentationLinks.flatMap(_.arcs).map(_.arcrole).toSet
    }

    assertResult(true) {
      presentationLinks.forall(lnk => (lnk.arcs.flatMap(arc => Vector(arc.from, arc.to))).toSet == lnk.locators.map(_.label).toSet)
    }
  }

  @Test def testMultipleCalculationLinks(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/ArcCyclesSIUC_calculation.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    assertResult(0) {
      linkbase.findAllChildElemsOfType(classTag[ArcroleRef]).size
    }

    def calculationLinks = linkbase.calculationLinks

    assertResult(11) {
      calculationLinks.size
    }

    assertResult(Set("http://www.xbrl.org/2003/arcrole/summation-item")) {
      calculationLinks.flatMap(_.arcs).map(_.arcrole).toSet
    }

    assertResult(true) {
      calculationLinks.forall(lnk => (lnk.arcs.flatMap(arc => Vector(arc.from, arc.to))).toSet == lnk.locators.map(_.label).toSet)
    }

    assertResult(Set(BigDecimal("0.4"), BigDecimal("0.6"), BigDecimal("1"))) {
      calculationLinks.flatMap(_.arcs).map(e => BigDecimal(e.attribute(EName("weight")))).toSet
    }
  }

  @Test def testGenericLink(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val docUri = classOf[LinkbaseTest].getResource("/sample-generic-linkbase.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem =
      new DefaultIndexedBridgeElem(indexed.Document.from(doc.withUriOption(Some(docUri)), uriResolver).documentElement)

    val linkbase = Linkbase(bridgeElem)

    val extendedLinks = linkbase.extendedLinks
    val genericLinks = linkbase.genericLinks

    assertResult(1) {
      extendedLinks.size
    }
    assertResult(extendedLinks) {
      genericLinks
    }

    assertResult(Set("opinion")) {
      genericLinks.head.resources.map(_.localName).toSet
    }
    assertResult(Set("arc")) {
      genericLinks.head.arcs.map(_.localName).toSet
    }
    assertResult(genericLinks.head.arcs) {
      genericLinks.head.findAllChildElemsOfType(classTag[GenericArc])
    }
    assertResult(List("loc", "loc", "loc")) {
      genericLinks.head.locators.map(_.localName)
    }
  }
}
