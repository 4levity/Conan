Conan
=====

conan:
* the app (command line test/debug features for now, swing or html GUI later?)

propertytree:
* tree structure template class
* each node has a "property" describing what it means and what data type (class) it is
* an abstract class PropertyTree is provided with no value member
* an instantiatable class PropertyTreeObjNode is provided 
* the latter has an Object value; property is used to validate assignment type
* query functions provide property-to-valueset maps at/below a given node

mediatree:
* library using propertytree to represent media files, tags, images etc
* has node classes for various items

specialtypes:
* many mediatree nodes are valued with native Java types
* some special types are here

stringutils:
* stuff for strings and bytebuffers

Building
========

build from Eclipse, or use ant

build.xml is generated from Eclipse
build-custom.xml adds building runnable JAR.

"ant jar" -> creates bin/conan.jar
