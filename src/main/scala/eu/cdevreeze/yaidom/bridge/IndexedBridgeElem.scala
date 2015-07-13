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

import eu.cdevreeze.yaidom.core.Path
import eu.cdevreeze.yaidom.queryapi.ScopedElemApi

/**
 * Bridge element that extends `SimpleBridgeElem` with "indexing".
 *
 * It offers pluggable DOM-like element implementations, without any "type gymnastics" and without paying any
 * "cake pattern tax".
 *
 * This is a purely abstract universal trait, allowing for allocation-free value objects.
 *
 * @author Chris de Vreeze
 */
trait IndexedBridgeElem extends Any with SimpleBridgeElem {

  /**
   * The type of this bridge element itself
   */
  override type SelfType <: IndexedBridgeElem

  /**
   * The unwrapped backing element type, for example `simple.Elem`
   */
  type UnwrappedBackingElem <: ScopedElemApi[UnwrappedBackingElem]

  // Extra methods

  def rootElem: UnwrappedBackingElem

  def path: Path

  def unwrappedBackingElem: UnwrappedBackingElem

  /**
   * Returns the base URI of the element, by XML Base processing starting with the document URI
   */
  def baseUri: URI

  /**
   * Returns the document URI
   */
  def docUri: URI
}
