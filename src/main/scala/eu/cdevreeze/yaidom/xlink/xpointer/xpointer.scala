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
import eu.cdevreeze.yaidom.queryapi.DocumentApi
import eu.cdevreeze.yaidom.queryapi.ScopedElemApi
import XPointer.IdEName

/**
 * Shorthand and element scheme XPointers.
 *
 * @author Chris de Vreeze
 */
sealed trait XPointer {

  private[xpointer] def findElem[E <: ScopedElemApi[E]](doc: DocumentApi[E]): Option[E]

  /**
   * Finds the optional descendant-or-self element by this XPointer, with the given element as root.
   * For child sequence pointers, the first position refers to a child element, and not to this element itself!
   */
  private[xpointer] def findElemOrSelf[E <: ScopedElemApi[E]](elem: E): Option[E]
}

/**
 * Shorthand XPointer.
 */
final case class ShorthandPointer(val id: String) extends XPointer {

  private[xpointer] def findElem[E <: ScopedElemApi[E]](doc: DocumentApi[E]): Option[E] = {
    findElemOrSelf(doc.documentElement)
  }

  private[xpointer] def findElemOrSelf[E <: ScopedElemApi[E]](elem: E): Option[E] = {
    elem.findElemOrSelf(e => e.attributeOption(IdEName) == Some(id))
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

  private[xpointer] def findElem[E <: ScopedElemApi[E]](doc: DocumentApi[E]): Option[E] = {
    findElemOrSelf(doc.documentElement)
  }

  private[xpointer] def findElemOrSelf[E <: ScopedElemApi[E]](elem: E): Option[E] = {
    elem.findElemOrSelf(e => e.attributeOption(IdEName) == Some(id))
  }
}

/**
 * Child sequence element scheme XPointer. The child sequence numbers are 1-based!
 */
final case class ChildSequencePointer(val childSeq: List[Int]) extends ElementSchemePointer {
  require(!childSeq.isEmpty, s"The child sequence must not be empty")

  def tailOption: Option[ChildSequencePointer] = {
    val tl = childSeq.tail
    if (tl.isEmpty) None else Some(ChildSequencePointer(tl))
  }

  private[xpointer] def findElem[E <: ScopedElemApi[E]](doc: DocumentApi[E]): Option[E] = childSeq match {
    case hd :: tl if hd == 1 =>
      tailOption.map(xp => xp.findElemOrSelf(doc.documentElement)).getOrElse(Some(doc.documentElement))
    case _ => None
  }

  private[xpointer] def findElemOrSelf[E <: ScopedElemApi[E]](elem: E): Option[E] = childSeq match {
    case hd :: tl =>
      val cheOption = elem.findAllChildElems.toStream.drop(hd - 1).headOption

      if (tl.isEmpty) cheOption else {
        // Recursive call
        cheOption.flatMap(e => tailOption.get.findElemOrSelf(e))
      }
    case _ => None
  }
}

/**
 * ID child sequence element scheme XPointer. The child sequence numbers are 1-based!
 */
final case class IdChildSequencePointer(val id: String, val childSeq: List[Int]) extends ElementSchemePointer {
  require(!childSeq.isEmpty, s"The child sequence must not be empty")

  private[xpointer] def findElem[E <: ScopedElemApi[E]](doc: DocumentApi[E]): Option[E] = {
    findElemOrSelf(doc.documentElement)
  }

  private[xpointer] def findElemOrSelf[E <: ScopedElemApi[E]](elem: E): Option[E] = {
    val firstElemOption = IdPointer(id).findElemOrSelf(elem)

    firstElemOption flatMap { e =>
      val xp = ChildSequencePointer(childSeq)
      xp.findElemOrSelf(e)
    }
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

  /**
   * Adds XPointer-awareness to a documents that offers the ScopedElemApi query API for its elements.
   * That is, adds functions findElemByXPointer and findElemByXPointers.
   */
  implicit final class XPointerAwareDocument[E <: ScopedElemApi[E]](val doc: DocumentApi[E]) {

    def findElemByXPointer(xpointer: XPointer): Option[E] = xpointer.findElem(doc)

    def findElemByXPointers(xpointers: Seq[XPointer]): Option[E] = {
      xpointers.toStream.flatMap(xpointer => doc.findElemByXPointer(xpointer)).headOption
    }
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
