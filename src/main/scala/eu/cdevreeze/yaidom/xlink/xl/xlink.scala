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

package eu.cdevreeze.yaidom.xlink.xl

import java.net.URI

import scala.collection.immutable

import eu.cdevreeze.yaidom.core.EName

/**
 * XLink in an XBRL context, as purely abstract traits. See http://www.w3.org/TR/xlink11/ and the schema documents
 * xl-2003-12-31.xsd and xlink-2003-12-31.xsd. Foremost, schema document xl-2003-12-31.xsd defines this model.
 *
 * In an XBRL context, there are additional restrictions on XLink (see xl-2003-12-31.xsd). For example, the href
 * attribute in a simple link is mandatory, whereas according to the XLink specification it is optional.
 * Moreover, the label attribute is mandatory for locators and resources, as are the from and to attributes of an arc,
 * and the role of an extended link.
 *
 * It is also assumed that arcs, locators and resources always have a parent extended link element.
 *
 * @author Chris de Vreeze
 */
trait XLink {

  def xlinkType: XLink.XLinkType

  def xlinkAttributes: immutable.IndexedSeq[(EName, String)]
}

/** Simple or extended link */
trait Link extends XLink

/** Simple link */
trait SimpleLink extends Link {

  def href: URI
  def roleOption: Option[String]
  def arcroleOption: Option[String]
  def titleOption: Option[String]
  def showOption: Option[String]
  def actuateOption: Option[String]
}

/** Extended link */
trait ExtendedLink extends Link {

  /** The XLink children */
  def xlinkChildren: immutable.IndexedSeq[XLink]

  /**
   * Returns the XLink resources, grouped by xlink:label attribute.
   *
   * That is, returns:
   * {{{
   * resourceXLinks groupBy (_.label)
   * }}}
   */
  def labeledResources: Map[String, immutable.IndexedSeq[Resource]]

  /**
   * Returns the XLink locators, grouped by xlink:label attribute
   *
   * That is, returns:
   * {{{
   * locatorXLinks groupBy (_.label)
   * }}}
   */
  def labeledLocators: Map[String, immutable.IndexedSeq[Locator]]

  /**
   * Returns the XLink resources and locators, grouped by xlink:label attribute
   *
   * That is, returns:
   * {{{
   * (resourceXLinks ++ locatorXLinks) groupBy (_.label)
   * }}}
   */
  def labeledXLinks: Map[String, immutable.IndexedSeq[LabeledXLink]]

  def role: String
  def titleOption: Option[String]

  // There can also be Documentation children, but these are not XLinks themselves

  def titleXLinks: immutable.IndexedSeq[Title]
  def locatorXLinks: immutable.IndexedSeq[Locator]
  def arcXLinks: immutable.IndexedSeq[Arc]
  def resourceXLinks: immutable.IndexedSeq[Resource]
}

/** Arc */
trait Arc extends XLink {

  /** The extended link role of the surrounding (!) extended link */
  def elr: String

  def from: String
  def to: String
  def arcrole: String
  def titleOption: Option[String]
  def showOption: Option[String]
  def actuateOption: Option[String]

  def titleXLinks: immutable.IndexedSeq[Title]

  def orderOption: Option[BigDecimal]
  def useOption: Option[XLink.Use]
  def priorityOption: Option[Int]
}

/** Locator or resource */
trait LabeledXLink extends XLink {

  /** The extended link role of the surrounding (!) extended link */
  def elr: String

  def label: String
}

/** Locator */
trait Locator extends LabeledXLink {

  def href: URI
  def roleOption: Option[String]
  def titleOption: Option[String]

  def titleXLinks: immutable.IndexedSeq[Title]
}

/** Resource */
trait Resource extends LabeledXLink {

  def roleOption: Option[String]
  def titleOption: Option[String]
}

/** Title (as element, not the attribute) */
trait Title extends XLink {
}

object XLink {

  val XLinkNamespace = URI.create("http://www.w3.org/1999/xlink").toString

  val XLinkTypeEName = EName(XLinkNamespace, "type")
  val XLinkHrefEName = EName(XLinkNamespace, "href")
  val XLinkArcroleEName = EName(XLinkNamespace, "arcrole")
  val XLinkRoleEName = EName(XLinkNamespace, "role")
  val XLinkTitleEName = EName(XLinkNamespace, "title")
  val XLinkShowEName = EName(XLinkNamespace, "show")
  val XLinkActuateEName = EName(XLinkNamespace, "actuate")
  val XLinkFromEName = EName(XLinkNamespace, "from")
  val XLinkToEName = EName(XLinkNamespace, "to")
  val XLinkLabelEName = EName(XLinkNamespace, "label")
  val XLinkOrderEName = EName(XLinkNamespace, "order")
  val XLinkUseEName = EName(XLinkNamespace, "use")
  val XLinkPriorityEName = EName(XLinkNamespace, "priority")

  sealed trait XLinkType
  case object XLinkTypeSimple extends XLinkType { override def toString: String = "simple" }
  case object XLinkTypeExtended extends XLinkType { override def toString: String = "extended" }
  case object XLinkTypeLocator extends XLinkType { override def toString: String = "locator" }
  case object XLinkTypeArc extends XLinkType { override def toString: String = "arc" }
  case object XLinkTypeResource extends XLinkType { override def toString: String = "resource" }
  case object XLinkTypeTitle extends XLinkType { override def toString: String = "title" }

  object XLinkType {

    def fromString(s: String): XLinkType = s match {
      case "simple" => XLinkTypeSimple
      case "extended" => XLinkTypeExtended
      case "locator" => XLinkTypeLocator
      case "arc" => XLinkTypeArc
      case "resource" => XLinkTypeResource
      case "title" => XLinkTypeTitle
      case _ => sys.error(s"Invalid use: $s")
    }
  }

  sealed trait Use
  case object UseOptional extends Use { override def toString: String = "optional" }
  case object UseProhibited extends Use { override def toString: String = "prohibited" }

  object Use {

    def fromString(s: String): Use = s match {
      case "optional" => UseOptional
      case "prohibited" => UseProhibited
      case _ => sys.error(s"Invalid use: $s")
    }
  }
}
