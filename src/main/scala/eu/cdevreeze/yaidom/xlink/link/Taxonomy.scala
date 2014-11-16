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
import scala.collection.immutable

/**
 * Taxonomy, containing a Map from document URIs to TaxonomyDoc instances.
 *
 * @author Chris de Vreeze
 */
final class Taxonomy(val docsByUri: Map[URI, TaxonomyDoc])

object Taxonomy {

  def from(docs: immutable.IndexedSeq[TaxonomyDoc]): Taxonomy = {
    new Taxonomy(docs.groupBy(_.docElem.docUri).mapValues(_.head).toMap)
  }
}
