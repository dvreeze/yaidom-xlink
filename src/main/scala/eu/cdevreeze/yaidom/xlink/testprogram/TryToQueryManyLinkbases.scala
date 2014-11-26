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

package eu.cdevreeze.yaidom.xlink.testprogram

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

import scala.Vector
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import eu.cdevreeze.yaidom.bridge.DefaultDocawareBridgeElem
import eu.cdevreeze.yaidom.docaware
import eu.cdevreeze.yaidom.parse.DocumentParserUsingSax
import eu.cdevreeze.yaidom.xlink.link.LinkLinkbaseEName
import eu.cdevreeze.yaidom.xlink.link.Linkbase

/**
 * Tries to load many linkbase documents, and query them.
 *
 * @author Chris de Vreeze
 */
object TryToQueryManyLinkbases {

  def main(args: Array[String]): Unit = {
    require(args.size == 1, s"Usage: TryToQueryManyLinkbases <root directory>")
    val rootDir = new File(args(0))

    require(rootDir.isDirectory)

    val docParser = DocumentParserUsingSax.newInstance()

    val files = findFiles(rootDir, acceptFile)

    println(s"Found ${files.size} files")
    println("Parsing all files ...")

    val idx = new AtomicInteger(0)

    val docs = (files.par flatMap { f =>
      val currIdx = idx.getAndIncrement()
      if (currIdx % 1000 == 0 && currIdx > 0) println(s"Parsed ${currIdx} documents")

      val docOption = Try(docParser.parse(f).withUriOption(Some(f.toURI))).toOption
      docOption
    }).seq.toVector

    println(s"Parsed ${docs.size} XML files. Now instantiating linkbase documents from them ...")

    val linkbaseTries: Vector[Try[Linkbase]] = docs collect {
      case doc if doc.documentElement.resolvedName == LinkLinkbaseEName =>
        Try(Linkbase(DefaultDocawareBridgeElem.wrap(docaware.Elem(doc.uriOption.get, doc.documentElement))))
    }
    val linkbases: Vector[Linkbase] = linkbaseTries flatMap {
      case Success(linkbase) => Some(linkbase)
      case Failure(t) => println(s"Could not instantiate a linkbase. Exception: $t"); None
    }

    println(s"Instantiated ${linkbases.size} linkbase documents.")

    println("Starting querying (linkbases) ...")
    println()

    linkbases.foreach(performLinkbaseQueries)

    println("Querying again (linkbases) ...")
    println()

    linkbases.foreach(performLinkbaseQueries)

    println("Ready!")
  }

  private def acceptFile(f: File): Boolean =
    f.isFile && Set(".xml").exists(s => f.getName.endsWith(s))

  private def findFiles(dir: File, p: File => Boolean): Vector[File] = {
    require(dir.isDirectory)
    val files = dir.listFiles.toVector

    // Recursive calls
    files.flatMap(f => if (f.isFile) Vector(f).filter(p) else findFiles(f, p))
  }

  private def performLinkbaseQueries(linkbase: Linkbase): Unit = {
    val extendedLinks = linkbase.extendedLinks

    println(s"Linkbase ${linkbase.bridgeElem.docUri} has ${extendedLinks.size} extended links")

    for (extendedLink <- extendedLinks) {
      val elr = extendedLink.role

      val arcFromTos = extendedLink.arcs.flatMap(arc => List(arc.from, arc.to)).toSet

      val brokenXLinkLabels = arcFromTos.diff(extendedLink.labeledXLinks.keySet)

      if (!brokenXLinkLabels.isEmpty) {
        println(s"Linkbase ${linkbase.bridgeElem.docUri} (ELR $elr) has broken XLink labels: ${brokenXLinkLabels.toSeq.sorted}")
      }

      val unusedXLinkLabels = extendedLink.labeledXLinks.keySet.diff(arcFromTos)

      if (!unusedXLinkLabels.isEmpty) {
        println(s"Linkbase ${linkbase.bridgeElem.docUri} (ELR $elr) has unused XLink labels: ${unusedXLinkLabels.toSeq.sorted}")
      }

      val nonUniqueXLinkLabels = extendedLink.labeledXLinks.filter(_._2.size >= 2).keySet

      if (!nonUniqueXLinkLabels.isEmpty) {
        println(s"Linkbase ${linkbase.bridgeElem.docUri} (ELR $elr) has non-unique XLink labels: ${nonUniqueXLinkLabels.toSeq.sorted}")
      }
    }

    println()
  }
}