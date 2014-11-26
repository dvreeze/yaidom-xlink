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
 * future they belong to the core yaidom project (so that no longer a dependency on this project is needed
 * if XLink support itself is not needed).
 *
 * ==Design notes==
 *
 * Why do we need all this wrapping? After all, a simple element is wrapped in a more general `DefaultSimpleBridgeElem`
 * (possibly without any object creation costs), which is wrapped in a `SimpleWrapperElem` if you need the yaidom
 * query API on the "bridge element".
 *
 * Had the yaidom query API traits been implemented using abstract types instead of type parameters, we would not have
 * needed to bridge between the use of generics for query API traits and the absence of generics in the bridge and
 * wrapper elements.
 *
 * On the other hand, it turned out to be far easier and more natural to model F-bounded polymorphism in the query API
 * traits using generics than using abstract types. This is not surprising: generics introduce a family of types
 * (which is indeed what the query API traits "are"), whereas abstract types just introduce a (type) member in one type.
 * For example, it is natural to think of one "instance of type" `ScopedElemApi` as the existential type:
 * {{{
 * ScopedElemApi[E] forSome { type E }
 * }}}
 * Existential types have been avoided in yaidom and yaidom-xlink, however.
 *
 * @author Chris de Vreeze
 */
package object bridge
