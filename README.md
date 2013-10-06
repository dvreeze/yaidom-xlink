============
Yaidom-xlink
============

Yaidom-based XLink support. This library offers thin XLink wrappers around yaidom Elems that correspond to XLink content.

Usage
=====

Yaidom-xlink versions can be found in the Maven central repository. Assuming version 0.7.0, yaidom-xlink can be added as dependency
as follows (in an SBT or Maven build):

**SBT**::

    libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom-xlink" % "0.7.0"

**Maven2**::

    <dependency>
      <groupId>eu.cdevreeze.yaidom</groupId>
      <artifactId>yaidom-xlink_2.10</artifactId>
      <version>0.7.0</version>
    </dependency>

Note that yaidom-xlink itself depends only on yaidom, and its dependencies.
Yaidom-xlink has been cross-built for several Scala versions, leading to artifactIds yaidom-xlink_2.9.1, yaidom-xlink_2.9.2, etc.

Yaidom-xlink requires Java version 1.6 or later.

