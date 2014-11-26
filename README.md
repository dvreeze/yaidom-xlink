============
Yaidom-xlink
============

Yaidom-based XLink and linkbase support.

Usage
=====

Yaidom-xlink versions can be found in the Maven central repository. Assuming version 1.3.1, yaidom-xlink can be added as dependency
as follows (in an SBT or Maven build):

**SBT**::

    libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom-xlink" % "1.3.1"

**Maven2**::

    <dependency>
      <groupId>eu.cdevreeze.yaidom</groupId>
      <artifactId>yaidom-xlink_2.11</artifactId>
      <version>1.3.1</version>
    </dependency>

Note that yaidom-xlink itself depends only on yaidom, and its dependencies.
Yaidom-xlink has been cross-built for several Scala versions, leading to artifactIds referring to different Scala (binary) versions.

Yaidom-xlink requires Java version 1.6 or later.

