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
 * This package contains abstract APIs and concrete implementations of so-called "bridge elements".
 *
 * These bridge elements can play a role in supporting yaidom `SubtypeAwareElemApi`-backed XML dialects that
 * can use multiple "DOM backends". These bridges abstract over those "backends".
 *
 * Other projects can use as little or as much of these traits as desired. It would be quite natural to use
 * only one purely abstract bridge element API of this package, provide an own implementation, and extend the
 * API and implementation according to the needs of that project.
 *
 * Currently these common bridge element APIs and implementations live in the XLink project, but maybe in the
 * future they belong in the core yaidom project (so that no longer a dependency on this project is needed
 * if XLink support itself is not needed).
 *
 * @author Chris de Vreeze
 */
package object bridge
