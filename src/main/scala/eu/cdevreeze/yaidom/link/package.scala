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

package eu.cdevreeze.yaidom

/**
 * This package models XBRL linkbases.
 *
 * The model can be populated if it obeys the relevant schema: xbrl-linkbase-2003-12-31.xsd (which imports
 * xl-2003-12-31.xsd and xlink-2003-12-31.xsd).
 *
 * The "DOM backend" is pluggable as long as there is a `DocawareBridgeElem` bridge for it.
 *
 * The model is aware of xsi:nil, XML Base and XPointer (as restricted by XBRL).
 * 
 * The model is easy to use inside other projects (using yaidom) for parts of the data.
 *
 * @author Chris de Vreeze
 */
package object link
