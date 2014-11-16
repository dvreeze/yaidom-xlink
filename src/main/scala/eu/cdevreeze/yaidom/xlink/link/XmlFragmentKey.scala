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

import eu.cdevreeze.yaidom.core.Path

/**
 * Unique XML fragment key, consisting of a document URI and a Path to the element within that document.
 *
 * @author Chris de Vreeze
 */
final case class XmlFragmentKey(val docUri: URI, val path: Path)
