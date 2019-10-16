============
Yaidom-xlink
============

Yaidom-based XLink support. This project is being phased out, and kept around only for legacy code that
still uses it. It has been stripped to its bare essentials, containing only the ``XLink`` type and its
sub-types. This way the legacy code using this project can keep using it, while the yaidom library can
be upgraded to a newer version and still be used in combination with this release of yaidom-xlink. 

Usage
=====

Yaidom-xlink versions can be found in the Maven central repository. Assuming version 1.8.0, yaidom-xlink can be added as dependency
as follows (in an SBT or Maven build):

**SBT**::

    libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom-xlink" % "1.8.0"

**Maven2**::

    <dependency>
      <groupId>eu.cdevreeze.yaidom</groupId>
      <artifactId>yaidom-xlink_2.13</artifactId>
      <version>1.8.0</version>
    </dependency>

Note that yaidom-xlink itself depends only on yaidom, and its dependencies.
Yaidom-xlink has been cross-built for several Scala versions, leading to artifactIds referring to different Scala (binary) versions.

Yaidom-xlink requires Java version 8 or later.

