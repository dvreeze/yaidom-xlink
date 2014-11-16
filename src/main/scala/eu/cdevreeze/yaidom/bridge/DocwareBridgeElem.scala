/*
 * Copyright 2014 Chris de Vreeze
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

package eu.cdevreeze.yaidom.bridge

import java.net.URI

import scala.collection.immutable

import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.Path
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.queryapi.IsNavigable
import eu.cdevreeze.yaidom.queryapi.ScopedElemLike

/**
 * Bridge element that extends `IndexedBridgeElem` with "document-awareness".
 *
 * It offers pluggable DOM-like element implementations, without any "type gymnastics" and without paying any
 * "cake pattern tax".
 *
 * Note that in yaidom, generics have been used extensively for composable pieces of the query API, in order to
 * assemble these into concrete element implementations. Here we use abstract types, in order to make concrete element
 * implementations pluggable as "XML back-ends". The goal is different, and so is the mechanism (abstract types
 * instead of type parameters).
 *
 * This is a purely abstract universal trait, allowing for allocation-free value objects.
 *
 * @author Chris de Vreeze
 */
trait DocawareBridgeElem extends Any with IndexedBridgeElem {

  /**
   * The type of this bridge element itself
   */
  override type SelfType <: DocawareBridgeElem

  // Extra methods

  /**
   * Returns the base URI of the element, by XML Base processing starting with the document URI
   */
  def baseUri: URI

  /**
   * Returns the document URI
   */
  def docUri: URI
}

object DocawareBridgeElem {

  /**
   * Wrapper for the bridge element that itself offers the query API. Of course this is not a value class instance.
   */
  final class WithQueryApi(val bridge: DocawareBridgeElem) extends DocawareBridgeElem with ScopedElemLike[WithQueryApi] with IsNavigable[WithQueryApi] { self: WithQueryApi =>

    final override type BackingElem = bridge.BackingElem

    final override type SelfType = WithQueryApi

    final override type UnwrappedBackingElem = bridge.UnwrappedBackingElem

    final def backingElem: BackingElem = bridge.backingElem

    final def findAllChildElems: immutable.IndexedSeq[WithQueryApi] = {
      bridge.findAllChildElems.map(e => new WithQueryApi(e))
    }

    final def resolvedName: EName = bridge.resolvedName

    final def resolvedAttributes: immutable.Iterable[(EName, String)] = bridge.resolvedAttributes

    final def qname: QName = bridge.qname

    final def attributes: immutable.Iterable[(QName, String)] = bridge.attributes

    final def scope: Scope = bridge.scope

    final def text: String = bridge.text

    final def findChildElemByPathEntry(entry: Path.Entry): Option[WithQueryApi] = {
      bridge.findChildElemByPathEntry(entry).map(e => new WithQueryApi(e))
    }

    final def toElem: eu.cdevreeze.yaidom.simple.Elem = bridge.toElem

    final def rootElem: UnwrappedBackingElem = bridge.rootElem

    final def path: Path = bridge.path

    final def unwrappedBackingElem: UnwrappedBackingElem = bridge.unwrappedBackingElem

    final def baseUri: URI = bridge.baseUri

    final def docUri: URI = bridge.docUri
  }
}
