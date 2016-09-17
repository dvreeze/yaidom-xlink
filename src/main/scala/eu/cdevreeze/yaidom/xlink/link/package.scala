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

import java.net.URI

import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.queryapi.BackingElemApi

/**
 * This package models XBRL linkbases.
 *
 * The model can be populated if it obeys the relevant schema: xbrl-linkbase-2003-12-31.xsd (which imports
 * xl-2003-12-31.xsd and xlink-2003-12-31.xsd).
 *
 * The "DOM backend" is pluggable as long as it is a BackingElemApi.
 *
 * @author Chris de Vreeze
 */
package object link {

  type BackingElem = BackingElemApi

  val LinkNamespace = URI.create("http://www.xbrl.org/2003/linkbase").toString
  val GenNamespace = URI.create("http://xbrl.org/2008/generic").toString
  val LabelNamespace = URI.create("http://xbrl.org/2008/label").toString
  val ReferenceNamespace = URI.create("http://xbrl.org/2008/reference").toString
  val XmlNamespace = URI.create("http://www.w3.org/XML/1998/namespace").toString

  val LinkLinkbaseEName = EName(LinkNamespace, "linkbase")

  val LinkDocumentationEName = EName(LinkNamespace, "documentation")

  val LinkLocEName = EName(LinkNamespace, "loc")

  val LinkLabelArcEName = EName(LinkNamespace, "labelArc")
  val LinkReferenceArcEName = EName(LinkNamespace, "referenceArc")
  val LinkCalculationArcEName = EName(LinkNamespace, "calculationArc")
  val LinkPresentationArcEName = EName(LinkNamespace, "presentationArc")
  val LinkDefinitionArcEName = EName(LinkNamespace, "definitionArc")

  val LinkFootnoteArcEName = EName(LinkNamespace, "footnoteArc")

  val LinkLabelEName = EName(LinkNamespace, "label")
  val LinkReferenceEName = EName(LinkNamespace, "reference")
  val LinkFootnoteEName = EName(LinkNamespace, "footnote")

  val LinkLabelLinkEName = EName(LinkNamespace, "labelLink")
  val LinkReferenceLinkEName = EName(LinkNamespace, "referenceLink")
  val LinkCalculationLinkEName = EName(LinkNamespace, "calculationLink")
  val LinkPresentationLinkEName = EName(LinkNamespace, "presentationLink")
  val LinkDefinitionLinkEName = EName(LinkNamespace, "definitionLink")

  val LinkFootnoteLinkEName = EName(LinkNamespace, "footnoteLink")

  val LinkLinkbaseRefEName = EName(LinkNamespace, "linkbaseRef")
  val LinkSchemaRefEName = EName(LinkNamespace, "schemaRef")
  val LinkRoleRefEName = EName(LinkNamespace, "roleRef")
  val LinkArcroleRefEName = EName(LinkNamespace, "arcroleRef")

  val LinkDefinitionEName = EName(LinkNamespace, "definition")
  val LinkUsedOnEName = EName(LinkNamespace, "usedOn")
  val LinkRoleTypeEName = EName(LinkNamespace, "roleType")
  val LinkArcroleTypeEName = EName(LinkNamespace, "arcroleType")

  val GenArcEName = EName(GenNamespace, "arc")

  val LabelLabelEName = EName(LabelNamespace, "label")
  val ReferenceReferenceEName = EName(ReferenceNamespace, "reference")

  val XmlLangEName = EName(XmlNamespace, "lang")
  val XmlBaseEName = EName(XmlNamespace, "base")

  val OrderEName = EName("order")
  val UseEName = EName("use")
  val PriorityEName = EName("priority")

  val RoleUriEName = EName("roleURI")
  val ArcroleUriEName = EName("arcroleURI")

  val IdEName = EName("id")
}
