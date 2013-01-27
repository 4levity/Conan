Conan
=====

propertytree:
* tree structure template class
* each node has a "property" describing what it means and what data type (class) it is
* an abstract class PropertyTree is provided with no value member
* an instantiatable class PropertyTreeObjNode is provided 
* the latter has an Object value; property is used to validate assignment type
* search functions provide the value of a property at/below a given node

conanapp - program to manage media files and tags/metadata
2. mediatree - library for media file metadata

Building
========

build from Eclipse, or use ant

build.xml is generated from Eclipse
build-custom.xml adds building runnable JAR.

"ant jar" -> creates bin/conan.jar
