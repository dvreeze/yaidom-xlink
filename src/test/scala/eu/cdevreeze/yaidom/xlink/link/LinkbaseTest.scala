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

import java.io.File
import java.net.URI

import scala.Vector
import scala.reflect.classTag

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.Suite
import org.scalatest.junit.JUnitRunner

import eu.cdevreeze.yaidom.core.EName
import eu.cdevreeze.yaidom.docaware
import eu.cdevreeze.yaidom.parse.DocumentParserUsingDom

/**
 * Linkbase test case, using data from the XBRL Conformance Suite.
 *
 * @author Chris de Vreeze
 */
@RunWith(classOf[JUnitRunner])
class LinkbaseTest extends Suite {

  @Test def testResolve(): Unit = {
    // Test case 202-01

    val pathPrefix = "/XBRL-CONF-CR5-2012-01-24/Common/200-linkbase"
    val docUris =
      Vector("202-01-HrefResolution.xsd", "202-01-HrefResolution-label.xml").map(v => classOf[LinkbaseTest].getResource(s"$pathPrefix/$v").toURI)

    val files = findFiles(taxoRootDir) ++ findFiles(confSuiteRootDir)

    implicit val taxo = parseTaxonomy(files.map(_.toURI))

    println(s"Number of taxonomy files: ${taxo.docsByUri.size}")

    val linkbase = taxo.docsByUri.filterKeys(_.toString.contains("202-01-HrefResolution-label.xml")).values.head
    val linkbaseElem = Linkbase(linkbase.docElem)

    val allLinkbasesByUri = taxo.docsByUri flatMap {
      case (uri, doc) =>
        if (uri.toString.contains(".xml")) Vector(Linkbase(doc.docElem))
        else Vector()
    }

    println(s"Number of linkbase files: ${allLinkbasesByUri.size}")

    val schema = taxo.docsByUri.filterKeys(_.toString.contains("202-01-HrefResolution.xsd")).values.head
    val schemaElem = schema.docElem

    val locators = linkbaseElem.findAllElemsOfType(classTag[Locator])

    assertResult(2) {
      locators.size
    }
    assertResult(true) {
      locators.forall(_.href.toString.contains("202-01-HrefResolution.xsd"))
    }

    val fragmentKeys = locators.map(loc => loc.resolveHref)

    assertResult(true) {
      fragmentKeys.forall(key => key.docUri.toString.contains("202-01-HrefResolution.xsd"))
    }
    assertResult(taxo.docsByUri.keys.filter(u => Set(".xsd", "200-linkbase").forall(s => u.toString.contains(s)))) {
      fragmentKeys.map(_.docUri).toSet
    }

    val elems = fragmentKeys map (key => schemaElem.getElemOrSelfByPath(key.path))

    assertResult(Set("changeInRetainedEarnings", "fixedAssets")) {
      elems.flatMap(_.attributeOption(EName("name"))).toSet
    }
  }

  private def parseTaxonomy(docUris: Vector[URI]): Taxonomy = {
    val docParser = DocumentParserUsingDom.newInstance
    val docs =
      docUris.map(uri => docParser.parse(uri).withUriOption(Some(convertUriToOriginalUri(uri))))
    val resultDocs = docs.map(doc => docaware.Document(doc.uriOption.get, doc))

    Taxonomy.from(resultDocs.map(doc => TaxonomyDoc.fromDocawareElem(doc.documentElement)))
  }

  private def convertUriToOriginalUri(uri: URI): URI = {
    if (uri.getScheme != "file") uri
    else {
      val prefixToStrip =
        classOf[LinkbaseTest].getResource("/taxonomyrootdir").toURI.getPath.dropWhile(_ == '/')
      val pathWithoutPrefix =
        uri.getPath.dropWhile(_ == '/').stripPrefix(prefixToStrip).dropWhile(_ == '/')

      if (pathWithoutPrefix.size == uri.getPath.dropWhile(_ == '/').size) uri
      else {
        val (host, path) = pathWithoutPrefix.span(_ != '/')

        val newUri = new URI(s"http://${host}/${path.dropWhile(_ == '/')}")
        newUri
      }
    }
  }

  private def findFiles(rootDir: File): Vector[File] = {
    require(rootDir.isDirectory)
    rootDir.listFiles.toVector flatMap {
      case f: File if f.isFile => Vector(f)
      case d: File if d.isDirectory =>
        // Recursive call
        findFiles(d)
      case _ => Vector()
    }
  }

  private val taxoRootDir = new File(classOf[LinkbaseTest].getResource("/taxonomyrootdir").toURI)

  private val confSuiteRootDir = new File(classOf[LinkbaseTest].getResource("/XBRL-CONF-CR5-2012-01-24").toURI)
}
