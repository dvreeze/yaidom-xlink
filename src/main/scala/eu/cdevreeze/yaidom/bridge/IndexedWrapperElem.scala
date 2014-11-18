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

import scala.collection.immutable

import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.core.Path
import eu.cdevreeze.yaidom.core.QName
import eu.cdevreeze.yaidom.core.Scope
import eu.cdevreeze.yaidom.queryapi.IsNavigable
import eu.cdevreeze.yaidom.queryapi.ScopedElemLike

/**
 * This object is like a `IndexedBridgeElem`, but offering the `ScopedElemLike` and `IsNavigable` query API.
 * It is itself also a `IndexedBridgeElem`, and wraps a `IndexedBridgeElem`.
 *
 * Use this object when you want a `IndexedBridgeElem`, but at the same time want to use the yaidom query API.
 *
 * @author Chris de Vreeze
 */
final class IndexedWrapperElem(val bridge: IndexedBridgeElem) extends IndexedBridgeElem with ScopedElemLike[IndexedWrapperElem] with IsNavigable[IndexedWrapperElem] { self: IndexedWrapperElem =>

  final override type BackingElem = bridge.BackingElem

  final override type SelfType = IndexedWrapperElem

  final override type UnwrappedBackingElem = bridge.UnwrappedBackingElem

  final def backingElem: BackingElem = bridge.backingElem

  final def findAllChildElems: immutable.IndexedSeq[IndexedWrapperElem] = {
    bridge.findAllChildElems.map(e => new IndexedWrapperElem(e))
  }

  final def resolvedName: EName = bridge.resolvedName

  final def resolvedAttributes: immutable.Iterable[(EName, String)] = bridge.resolvedAttributes

  final def qname: QName = bridge.qname

  final def attributes: immutable.Iterable[(QName, String)] = bridge.attributes

  final def scope: Scope = bridge.scope

  final def text: String = bridge.text

  final def findChildElemByPathEntry(entry: Path.Entry): Option[IndexedWrapperElem] = {
    bridge.findChildElemByPathEntry(entry).map(e => new IndexedWrapperElem(e))
  }

  final def toElem: eu.cdevreeze.yaidom.simple.Elem = bridge.toElem

  final def rootElem: UnwrappedBackingElem = bridge.rootElem

  final def path: Path = bridge.path

  final def unwrappedBackingElem: UnwrappedBackingElem = bridge.unwrappedBackingElem
}
