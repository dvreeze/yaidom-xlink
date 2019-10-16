=========
CHANGELOG
=========


1.8.0
=====

This version depends on yaidom 1.10.1 and supports Scala 2.13. It is probably the last release, kept around only for legacy code
that uses yaidom-xlink. This version contains only the ``XLink`` class and its sub-types. It will likely
still work with upcoming newer releases of the yaidom library.

Due to the support for Scala 2.13 (whereas support for Scala 2.11 has been dropped), marker trait ``Immutable`` is no longer
available. This leads to the following breaking changes compared to version 1.7.0 (in SBT, run: *:mimaReportBinaryIssues):

* the type hierarchy of class eu.cdevreeze.yaidom.xlink.XLink is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.XLink")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.ExtendedLink is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.ExtendedLink")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.Resource is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.Resource")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.Arc is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.Arc")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.LabeledXLink is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.LabeledXLink")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.SimpleLink is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.SimpleLink")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.Title is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.Title")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.Locator is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.Locator")
* the type hierarchy of class eu.cdevreeze.yaidom.xlink.Link is different in current version. Missing types {scala.Immutable}
  filter with: ProblemFilters.exclude[MissingTypesProblem]("eu.cdevreeze.yaidom.xlink.Link")


1.7.0
=====

This version depends on yaidom 1.7.0. It is probably the last release, kept around only for legacy code
that uses yaidom-xlink. This version contains only the ``XLink`` class and its sub-types. It will likely
still work with upcoming newer releases of the yaidom library.


1.6.0
=====

This version depends on yaidom 1.6.0.


1.6.0-M7
========

This version depends on yaidom 1.6.0-M7.


1.6.0-M5
========

This version depends on yaidom 1.6.0-M5.


1.6.0-M4
========

This version depends on yaidom 1.6.0-M4.


1.6.0-M1
========

This version depends on yaidom 1.6.0-M1.


1.5.0
=====

This version depends on yaidom 1.5.0.


1.5.0-M2
========

This version depends on yaidom 1.5.0-M2.


1.5.0-M1
========

This version depends on yaidom 1.5.0-M1.


1.4.2
=====

This version depends on yaidom 1.4.2.


1.4.1
=====

This version depends on yaidom 1.4.1.


1.4.0
=====

This version depends on yaidom 1.4.0.


1.4.0-M3
========

This version depends on yaidom 1.4.0-M3.


1.4.0-M2
========

This version depends on yaidom 1.4.0-M2.


1.4.0-M1
========

This version depends on yaidom 1.4.0-M1.


1.3.6
=====

This version depends on yaidom 1.3.6.


1.3.5
=====

This version depends on yaidom 1.3.5.


1.3.4
=====

This version depends on yaidom 1.3.4.


1.3.3
=====

This version depends on yaidom 1.3.3.

Moreover, added some XPointer support (in XBRL context).


1.3.2
=====

This version depends on yaidom 1.3.2.


1.3.1
=====

This version depends on yaidom 1.3.1.


1.3
===

This version depends on yaidom 1.3.

Moreover, support has been added for so-called "bridge elements" (useful in several projects that model an XML dialect),
and XBRL linkbases have been modeled (and XLink as restricted by XBRL has been modeled as well).

In time, the old xlink package content will be phased out.


1.2
===

This version depends on yaidom 1.2.


1.1
===

This version depends on yaidom 1.1.


1.0
===

This version depends on yaidom 1.0.


0.8.2
=====

This version depends on yaidom 0.8.2, and defaults to Scala 2.11.


0.8.1
=====

This version depends on yaidom 0.8.1, and therefore adds support for Scala 2.11.X.
It also made ``labeledResources`` and ``labeledLocators`` in extended links "vals", to speed up lookups within extended links,
at the expense of more expensive creation of extended links.


0.8.0
=====

This version depends on yaidom 0.8.0, and therefore drops support for Scala 2.9.X.


0.7.0
=====

Version 0.7.0, copied from yaidom 0.6.14. Note that starting with yaidom 0.7.0, XLink support has been moved to this separate project.
