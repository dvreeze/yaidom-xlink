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

package eu.cdevreeze.yaidom.xlink.xpointer

import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.queryapi.ScopedElemApi
import XPointer.IdEName

/**
 * Shorthand and element scheme XPointers.
 *
 * @author Chris de Vreeze
 */
trait XPointer {

  def findElemOrSelf[E <: ScopedElemApi[E]](rootElem: E): Option[E]
}

/**
 * Shorthand XPointer.
 */
final case class ShorthandPointer(val id: String) extends XPointer {

  def findElemOrSelf[E <: ScopedElemApi[E]](rootElem: E): Option[E] = {
    rootElem.findElemOrSelf(e => e.attributeOption(IdEName) == Some(id))
  }
}

/**
 * Element scheme XPointer.
 */
trait ElementSchemePointer extends XPointer

/**
 * ID element scheme XPointer.
 */
final case class IdPointer(val id: String) extends ElementSchemePointer {

  def findElemOrSelf[E <: ScopedElemApi[E]](rootElem: E): Option[E] = {
    rootElem.findElemOrSelf(e => e.attributeOption(IdEName) == Some(id))
  }
}

/**
 * Child sequence element scheme XPointer.
 */
final case class ChildSequencePointer(val childSeq: List[Int]) extends ElementSchemePointer {

  def findElemOrSelf[E <: ScopedElemApi[E]](rootElem: E): Option[E] = childSeq match {
    case Nil => None
    case hd :: tl if hd == 1 => findElem(rootElem, tl)
    case _ => None
  }

  private def findElem[E <: ScopedElemApi[E]](startElem: E, childSeq: List[Int]): Option[E] = childSeq match {
    case Nil => None
    case hd :: tl =>
      val cheOption = startElem.findAllChildElems.toStream.drop(hd - 1).headOption
      // Recursive call
      cheOption.flatMap(e => findElem(e, tl))
  }
}

/**
 * ID child sequence element scheme XPointer.
 */
final case class IdChildSequencePointer(val id: String, val childSeq: List[Int]) extends ElementSchemePointer {

  def findElemOrSelf[E <: ScopedElemApi[E]](rootElem: E): Option[E] = {
    val elemWithIdOption = IdPointer(id).findElemOrSelf(rootElem)
    elemWithIdOption.flatMap(e => ChildSequencePointer(1 :: childSeq).findElemOrSelf(e))
  }
}

object XPointer {

  val IdEName = EName("id")

  def parse(s: String): XPointer = s match {
    case s if !s.trim.startsWith("element(") => ShorthandPointer(s)
    case s => ElementSchemePointer.parse(s)
  }

  def parseXPointers(s: String): List[XPointer] = s match {
    case s if s.isEmpty => Nil
    case s if !s.trim.startsWith("element(") => List(ShorthandPointer(s))
    case s =>
      val idx = s.indexOf(")")
      // Recursive call
      parse(s.substring(0, idx + 1)) :: parseXPointers(s.substring(idx + 1))
  }
}

object ElementSchemePointer {

  def parse(s: String): ElementSchemePointer = {
    require(
      s.startsWith("element(") && s.endsWith(")"),
      s"Element scheme pointers must start with 'element(' and must end with ')'")

    val data = s.substring("element(".size, s.size - 1)

    data match {
      case d if d.startsWith("/") => ChildSequencePointer(d.substring(1).split('/').toList.map(_.toInt))
      case d if !d.contains("/") => IdPointer(d)
      case d =>
        val idx = d.indexOf("/")
        val id = d.substring(0, idx)
        val childSeq = d.substring(idx + 1).split('/').toList.map(_.toInt)
        IdChildSequencePointer(id, childSeq)
    }
  }
}
