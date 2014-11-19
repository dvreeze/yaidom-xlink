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

import eu.cdevreeze.yaidom.bridge.DefaultDocawareBridgeElem
import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.docaware
import eu.cdevreeze.yaidom.parse.DocumentParserUsingDom
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

  @Test def testReconstructLabelLinkbase(): Unit = {
    val docParser = DocumentParserUsingDom.newInstance

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUri = classOf[LinkbaseTest].getResource(s"$pathPrefix/202-01-HrefResolution-label.xml").toURI

    val doc = docParser.parse(docUri)
    val bridgeElem = new DefaultDocawareBridgeElem(docaware.Document(docUri, doc).documentElement)

    val linkbase = Linkbase(bridgeElem)

    val labelLinks = linkbase.extendedLinksOfType(classTag[LabelLink])

    assertResult(1) {
      labelLinks.size
    }

    val labelLink = labelLinks(0)

    assertResult(6) {
      labelLink.xlinkChildren.size
    }

    val labelArcs = labelLink.labelArcs
    val locators = labelLink.locatorXLinks
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
        plusAttributeOption(QName("xml:lang"), label.attributeOption(EName("http://www.w3.org/XML/1998/namespace", "lang")))
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
}
