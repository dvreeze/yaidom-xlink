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

import eu.cdevreeze.yaidom.docaware
import eu.cdevreeze.yaidom.bridge.DocawareBridgeElem
import eu.cdevreeze.yaidom.bridge.DefaultDocawareBridgeElem

/**
 * Taxonomy document, in which XML fragments can quickly be found using IDs as keys.
 *
 * @author Chris de Vreeze
 */
final class TaxonomyDoc(val docElem: DocawareBridgeElem.WithQueryApi) {

  val xmlFragmentKeysById: Map[String, XmlFragmentKey] = {
    val result =
      docElem.filterElemsOrSelf(_.attributeOption(IdEName).isDefined) map { elem =>
        elem.attribute(IdEName) -> XmlFragmentKey(elem.docUri, elem.path)
      }
    result.toMap
  }
}

object TaxonomyDoc {

  def fromDocawareElem(elem: docaware.Elem): TaxonomyDoc = {
    val docElem =
      new DocawareBridgeElem.WithQueryApi(
        new DefaultDocawareBridgeElem(elem))
    new TaxonomyDoc(docElem)
  }
}