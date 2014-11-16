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
 * XLinks, wrapping a yaidom "simple element". The XLink support is without any support for XPointer.
 *
 * This package will be phased out. It only wraps simple elements, and is less useful directly in an XBRL context,
 * because of many optional attributes that are mandatory in an XBRL context.
 *
 * @author Chris de Vreeze
 */
package object xlink
