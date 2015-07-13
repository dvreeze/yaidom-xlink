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

import java.net.URI

import scala.BigDecimal
import scala.collection.immutable
import scala.reflect.classTag
import scala.reflect.ClassTag

import eu.cdevreeze.yaidom.bridge.IndexedBridgeElem
import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.Path
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.queryapi.IsNavigable
import eu.cdevreeze.yaidom.queryapi.ScopedElemLike
import eu.cdevreeze.yaidom.queryapi.SubtypeAwareElemLike
import eu.cdevreeze.yaidom.simple
import eu.cdevreeze.yaidom.xlink.xl

/**
 * XBRL linkbase content, as sub-classes of `LinkbaseElem`, which is an arbitrary element in a linkbase.
 * See xbrl-linkbase-2003-12-31.xsd (for standard XBRL).
 *
 * The `LinkbaseElem` type hierarchy also knows about generic links and generic label and reference links in particular.
 * For other generic link content, the user may want to define own (implicit) classes for ease of use, taking the
 * data of the underlying LinkbaseElem, such as a GenericResource.
 *
 * Moreover, some `LinkbaseElem` objects such as footnotes occur in XBRL instances, not in XBRL taxonomies. The user may
 * want to make it easy to populate the specific LinkbaseElems from those elements. This should be quite easy if
 * for XBRL instances the "same elements" are used, so either they also use `IndexedBridgeElem` instances, or they use
 * backing element implementations that are compatible with the `IndexedBridgeElem` abstraction.
 *
 * @author Chris de Vreeze
 */
sealed abstract class LinkbaseElem private[link] (
  val bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends ScopedElemLike[LinkbaseElem] with SubtypeAwareElemLike[LinkbaseElem] {

  assert(childElems.map(_.bridgeElem.backingElem) == bridgeElem.findAllChildElems.map(_.backingElem))

  /**
   * Very fast implementation of findAllChildElems, for fast querying
   */
  final def findAllChildElems: immutable.IndexedSeq[LinkbaseElem] = childElems

  final def resolvedName: EName = bridgeElem.resolvedName

  final def resolvedAttributes: immutable.Iterable[(EName, String)] = bridgeElem.resolvedAttributes

  final def qname: QName = bridgeElem.qname

  final def attributes: immutable.Iterable[(QName, String)] = bridgeElem.attributes

  final def scope: Scope = bridgeElem.scope

  final def text: String = bridgeElem.text

  final override def equals(other: Any): Boolean = other match {
    case e: LinkbaseElem => bridgeElem.backingElem == e.bridgeElem.backingElem
    case _               => false
  }

  final override def hashCode: Int = bridgeElem.backingElem.hashCode

  final def toElem: simple.Elem = bridgeElem.toElem
}

final class Linkbase private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkLinkbaseEName)

  final def extendedLinks: immutable.IndexedSeq[ExtendedLink] =
    findAllChildElemsOfType(classTag[ExtendedLink])

  final def extendedLinksOfType[B <: ExtendedLink](subType: ClassTag[B]): immutable.IndexedSeq[B] =
    findAllChildElemsOfType(subType)

  // Can have documentation, roleRef, arcroleRef and any extended link children

  final def calculationLinks: immutable.IndexedSeq[CalculationLink] =
    findAllChildElemsOfType(classTag[CalculationLink])

  final def definitionLinks: immutable.IndexedSeq[DefinitionLink] =
    findAllChildElemsOfType(classTag[DefinitionLink])

  final def presentationLinks: immutable.IndexedSeq[PresentationLink] =
    findAllChildElemsOfType(classTag[PresentationLink])

  final def labelLinks: immutable.IndexedSeq[LabelLink] =
    findAllChildElemsOfType(classTag[LabelLink])

  final def referenceLinks: immutable.IndexedSeq[ReferenceLink] =
    findAllChildElemsOfType(classTag[ReferenceLink])

  final def genericLinks: immutable.IndexedSeq[GenericLink] =
    findAllChildElemsOfType(classTag[GenericLink])
}

// XLink

abstract class XLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) with xl.XLink {

  final def xlinkAttributes: immutable.IndexedSeq[(EName, String)] =
    resolvedAttributes.toVector filter { case (ename, value) => ename.namespaceUriOption == Some(xl.XLink.XLinkNamespace) }
}

class SimpleLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.SimpleLink {

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeSimple

  final def href: URI = new URI(attribute(xl.XLink.XLinkHrefEName))

  final def roleOption: Option[String] = attributeOption(xl.XLink.XLinkRoleEName)

  final def arcroleOption: Option[String] = attributeOption(xl.XLink.XLinkArcroleEName)

  final def titleOption: Option[String] = attributeOption(xl.XLink.XLinkTitleEName)

  final def showOption: Option[String] = attributeOption(xl.XLink.XLinkShowEName)

  final def actuateOption: Option[String] = attributeOption(xl.XLink.XLinkActuateEName)

  final def resolvedHref: URI = bridgeElem.baseUri.resolve(href)
}

// Extended links

/**
 * Extended link in XBRL. It stores the maps from XLink labels to resources and locators for speed. The same holds
 * for the base URI.
 */
abstract class ExtendedLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.ExtendedLink {

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeExtended

  /**
   * Returns all XLink child elements. Hence, documentation children are not returned, since they are not XLink.
   */
  final def xlinkChildren: immutable.IndexedSeq[XLink] =
    findAllChildElemsOfType(classTag[XLink])

  final val labeledResources: Map[String, immutable.IndexedSeq[Resource]] = {
    resources.groupBy(_.label)
  }

  final val labeledLocators: Map[String, immutable.IndexedSeq[Locator]] = {
    locators.groupBy(_.label)
  }

  final val labeledXLinks: Map[String, immutable.IndexedSeq[XLink with xl.LabeledXLink]] = {
    (resources ++ locators).groupBy(_.label)
  }

  final val baseUri: URI = bridgeElem.baseUri

  final def role: String = attribute(xl.XLink.XLinkRoleEName)

  final def titleOption: Option[String] = attributeOption(xl.XLink.XLinkTitleEName)

  final def titleElems: immutable.IndexedSeq[Title] =
    findAllChildElemsOfType(classTag[Title])

  final def locators: immutable.IndexedSeq[Locator] =
    findAllChildElemsOfType(classTag[Locator])

  final def arcs: immutable.IndexedSeq[Arc] =
    findAllChildElemsOfType(classTag[Arc])

  final def resources: immutable.IndexedSeq[Resource] =
    findAllChildElemsOfType(classTag[Resource])
}

abstract class StandardExtendedLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends ExtendedLink(bridgeElem, childElems) {
}

class GenericLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends ExtendedLink(bridgeElem, childElems) {
}

final class LabelLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkLabelLinkEName)

  final def labelArcs: immutable.IndexedSeq[LabelArc] =
    findAllChildElemsOfType(classTag[LabelArc])

  final def labelResources: immutable.IndexedSeq[LabelResource] =
    findAllChildElemsOfType(classTag[LabelResource])
}

final class ReferenceLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkReferenceLinkEName)

  final def referenceArcs: immutable.IndexedSeq[ReferenceArc] =
    findAllChildElemsOfType(classTag[ReferenceArc])

  final def referenceResources: immutable.IndexedSeq[ReferenceResource] =
    findAllChildElemsOfType(classTag[ReferenceResource])
}

final class CalculationLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkCalculationLinkEName)

  final def calculationArcs: immutable.IndexedSeq[CalculationArc] =
    findAllChildElemsOfType(classTag[CalculationArc])
}

final class PresentationLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkPresentationLinkEName)

  final def presentationArcs: immutable.IndexedSeq[PresentationArc] =
    findAllChildElemsOfType(classTag[PresentationArc])
}

final class DefinitionLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkDefinitionLinkEName)

  final def definitionArcs: immutable.IndexedSeq[DefinitionArc] =
    findAllChildElemsOfType(classTag[DefinitionArc])
}

final class FootnoteLink private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardExtendedLink(bridgeElem, childElems) {

  require(resolvedName == LinkFootnoteLinkEName)

  final def footnoteArcs: immutable.IndexedSeq[FootnoteArc] =
    findAllChildElemsOfType(classTag[FootnoteArc])

  final def footnoteResources: immutable.IndexedSeq[FootnoteResource] =
    findAllChildElemsOfType(classTag[FootnoteResource])
}

// Arcs

/**
 * Arc in XBRL.
 */
abstract class Arc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.Arc {

  require(!bridgeElem.path.isRoot, s"Missing parent extended link of $resolvedName")

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeArc

  final def elr: String =
    bridgeElem.rootElem.getElemOrSelfByPath(bridgeElem.path.parentPath).attribute(xl.XLink.XLinkRoleEName)

  final def from: String = attribute(xl.XLink.XLinkFromEName)

  final def to: String = attribute(xl.XLink.XLinkToEName)

  final def arcrole: String = attribute(xl.XLink.XLinkArcroleEName)

  final def titleOption: Option[String] = attributeOption(xl.XLink.XLinkTitleEName)

  final def showOption: Option[String] = attributeOption(xl.XLink.XLinkShowEName)

  final def actuateOption: Option[String] = attributeOption(xl.XLink.XLinkActuateEName)

  final def titleElems: immutable.IndexedSeq[Title] =
    findAllChildElemsOfType(classTag[Title])

  final def orderOption: Option[BigDecimal] =
    attributeOption(OrderEName).map(v => BigDecimal(v))

  final def useOption: Option[xl.XLink.Use] =
    attributeOption(UseEName).map(v => xl.XLink.Use.fromString(v))

  final def priorityOption: Option[Int] = attributeOption(PriorityEName).map(_.toInt)
}

abstract class StandardArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends Arc(bridgeElem, childElems) {
}

class GenericArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends Arc(bridgeElem, childElems) {

  require(resolvedName == GenArcEName)
}

final class LabelArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkLabelArcEName)
}

final class ReferenceArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkReferenceArcEName)
}

final class CalculationArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkCalculationArcEName)

  // Must have weight attribute
}

final class PresentationArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkPresentationArcEName)

  // Can have preferredLabel attribute
}

final class DefinitionArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkDefinitionArcEName)
}

final class FootnoteArc private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardArc(bridgeElem, childElems) {

  require(resolvedName == LinkFootnoteArcEName)
}

// Locators

/**
 * Locator in XBRL.
 */
abstract class Locator private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.Locator {

  require(!bridgeElem.path.isRoot, s"Missing parent extended link of $resolvedName")

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeLocator

  final def elr: String =
    bridgeElem.rootElem.getElemOrSelfByPath(bridgeElem.path.parentPath).attribute(xl.XLink.XLinkRoleEName)

  final def label: String = attribute(xl.XLink.XLinkLabelEName)

  final def href: URI = new URI(attribute(xl.XLink.XLinkHrefEName))

  final def roleOption: Option[String] = attributeOption(xl.XLink.XLinkRoleEName)

  final def titleOption: Option[String] = attributeOption(xl.XLink.XLinkTitleEName)

  final def titleElems: immutable.IndexedSeq[Title] =
    findAllChildElemsOfType(classTag[Title])

  final def resolvedHref: URI = bridgeElem.baseUri.resolve(href)
}

final class StandardLocator private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends Locator(bridgeElem, childElems) {

  require(resolvedName == LinkLocEName)
}

// Resources

/**
 * Resource in XBRL.
 */
abstract class Resource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.Resource {

  require(!bridgeElem.path.isRoot, s"Missing parent extended link of $resolvedName")

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeResource

  final def elr: String =
    bridgeElem.rootElem.getElemOrSelfByPath(bridgeElem.path.parentPath).attribute(xl.XLink.XLinkRoleEName)

  final def label: String = attribute(xl.XLink.XLinkLabelEName)

  final def roleOption: Option[String] = attributeOption(xl.XLink.XLinkRoleEName)

  final def titleOption: Option[String] = attributeOption(xl.XLink.XLinkTitleEName)
}

abstract class StandardResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends Resource(bridgeElem, childElems) {
}

class GenericResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends Resource(bridgeElem, childElems) {
}

final class LabelResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardResource(bridgeElem, childElems) {

  require(resolvedName == LinkLabelEName)

  def langOption: Option[String] = bridgeElem.backingElem.attributeOption(XmlLangEName)
}

final class ReferenceResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardResource(bridgeElem, childElems) {

  require(resolvedName == LinkReferenceEName)
}

final class FootnoteResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends StandardResource(bridgeElem, childElems) {

  require(resolvedName == LinkFootnoteEName)
}

final class GenericLabelResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends GenericResource(bridgeElem, childElems) {

  require(resolvedName == LabelLabelEName)
}

final class GenericReferenceResource private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends GenericResource(bridgeElem, childElems) {

  require(resolvedName == ReferenceReferenceEName)
}

// Title

class Title private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends XLink(bridgeElem, childElems) with xl.Title {

  final def xlinkType: xl.XLink.XLinkType = xl.XLink.XLinkTypeTitle
}

// Miscellaneous

final class Documentation private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkDocumentationEName)
}

final class LinkbaseRef private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends SimpleLink(bridgeElem, childElems) {

  require(resolvedName == LinkLinkbaseRefEName)

  // Must have arcrole attribute
}

final class SchemaRef private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends SimpleLink(bridgeElem, childElems) {

  require(resolvedName == LinkSchemaRefEName)
}

final class RoleRef private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends SimpleLink(bridgeElem, childElems) {

  require(resolvedName == LinkRoleRefEName)

  // Must have roleURI attribute

  def arcroleUri: String = attribute(RoleUriEName)
}

final class ArcroleRef private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends SimpleLink(bridgeElem, childElems) {

  require(resolvedName == LinkArcroleRefEName)

  // Must have arcroleURI attribute

  def arcroleUri: String = attribute(ArcroleUriEName)
}

final class Definition private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkDefinitionEName)
}

final class UsedOn private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkUsedOnEName)
}

final class RoleType private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkRoleTypeEName)

  // Has at most one definition and at least one unsedOn. Has roleURI attribute.
}

final class ArcroleType private[link] (
  bridgeElem: IndexedBridgeElem,
  childElems: immutable.IndexedSeq[LinkbaseElem]) extends LinkbaseElem(bridgeElem, childElems) {

  require(resolvedName == LinkArcroleTypeEName)

  // Has at most one definition and at least one usedOn. Has arcroleURI and cyclesAllowed attributes.
}

// Factories.

object LinkbaseElem {

  /** Creates a LinkbaseElem. This method is rather expensive. */
  def apply(elem: IndexedBridgeElem): LinkbaseElem = {
    // Recursive calls
    val childElems = elem.findAllChildElems.map(e => apply(e))
    apply(elem, childElems)
  }

  private[link] def apply(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): LinkbaseElem = {
    elem.backingElem.attributeOption(xl.XLink.XLinkTypeEName) match {
      case Some("extended") => applyForExtendedLink(elem, childElems)
      case Some("simple")   => applyForSimpleLink(elem, childElems)
      case Some("arc")      => applyForArc(elem, childElems)
      case Some("locator")  => applyForLocator(elem, childElems)
      case Some("resource") => applyForResource(elem, childElems)
      case Some("title")    => new Title(elem, childElems)
      case Some(_)          => sys.error(s"Not an XLink. Element name ${elem.resolvedName}")
      case None => elem.resolvedName match {
        case LinkLinkbaseEName      => new Linkbase(elem, childElems)
        case LinkRoleTypeEName      => new RoleType(elem, childElems)
        case LinkArcroleTypeEName   => new ArcroleType(elem, childElems)
        case LinkDefinitionEName    => new Definition(elem, childElems)
        case LinkDocumentationEName => new Documentation(elem, childElems)
        case LinkUsedOnEName        => new UsedOn(elem, childElems)
        case _                      => new LinkbaseElem(elem, childElems) {}
      }
    }
  }

  private[link] def applyForExtendedLink(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): ExtendedLink = {
    elem.resolvedName match {
      case LinkLabelLinkEName        => new LabelLink(elem, childElems)
      case LinkReferenceLinkEName    => new ReferenceLink(elem, childElems)
      case LinkCalculationLinkEName  => new CalculationLink(elem, childElems)
      case LinkPresentationLinkEName => new PresentationLink(elem, childElems)
      case LinkDefinitionLinkEName   => new DefinitionLink(elem, childElems)
      case LinkFootnoteLinkEName     => new FootnoteLink(elem, childElems)
      case _                         => new GenericLink(elem, childElems)
    }
  }

  private[link] def applyForSimpleLink(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): SimpleLink = {
    elem.resolvedName match {
      case LinkSchemaRefEName   => new SchemaRef(elem, childElems)
      case LinkLinkbaseRefEName => new LinkbaseRef(elem, childElems)
      case LinkRoleRefEName     => new RoleRef(elem, childElems)
      case LinkArcroleRefEName  => new ArcroleRef(elem, childElems)
      case _                    => new SimpleLink(elem, childElems)
    }
  }

  private[link] def applyForArc(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): Arc = {
    elem.resolvedName match {
      case LinkLabelArcEName        => new LabelArc(elem, childElems)
      case LinkReferenceArcEName    => new ReferenceArc(elem, childElems)
      case LinkCalculationArcEName  => new CalculationArc(elem, childElems)
      case LinkPresentationArcEName => new PresentationArc(elem, childElems)
      case LinkDefinitionArcEName   => new DefinitionArc(elem, childElems)
      case LinkFootnoteArcEName     => new FootnoteArc(elem, childElems)
      case GenArcEName              => new GenericArc(elem, childElems)
      case _                        => sys.error(s"Not recognized as arc: ${elem.resolvedName}")
    }
  }

  private[link] def applyForLocator(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): Locator = {
    elem.resolvedName match {
      case LinkLocEName => new StandardLocator(elem, childElems)
      case _            => sys.error(s"Not recognized as locator: ${elem.resolvedName}")
    }
  }

  private[link] def applyForResource(elem: IndexedBridgeElem, childElems: immutable.IndexedSeq[LinkbaseElem]): Resource = {
    elem.resolvedName match {
      case LinkLabelEName          => new LabelResource(elem, childElems)
      case LinkReferenceEName      => new ReferenceResource(elem, childElems)
      case LinkFootnoteEName       => new FootnoteResource(elem, childElems)
      case LabelLabelEName         => new GenericLabelResource(elem, childElems)
      case ReferenceReferenceEName => new GenericReferenceResource(elem, childElems)
      case _                       => new GenericResource(elem, childElems)
    }
  }
}

object Linkbase {

  /** Creates a Linkbase. This method is rather expensive. */
  def apply(elem: IndexedBridgeElem): Linkbase = {
    new Linkbase(elem, elem.findAllChildElems.map(e => LinkbaseElem(e)))
  }

  /** Returns true if the element must be a linkbase (looking at the element resolved name) */
  def accepts(elem: IndexedBridgeElem): Boolean = {
    elem.resolvedName == LinkLinkbaseEName
  }
}
