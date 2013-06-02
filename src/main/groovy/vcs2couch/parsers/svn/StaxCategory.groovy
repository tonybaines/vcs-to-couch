package vcs2couch.parsers.svn

import javax.xml.stream.*

// Mixin for helping with the StaX API
class StaxCategory {
  // Get attribute values by name
  static Object get(XMLStreamReader self, String key) {
    self.getAttributeValue(null, key)
  }
  static String name(XMLStreamReader self) {
    self.name.toString()
  }
  static String text(XMLStreamReader self) {
    self.elementText
  }
  static boolean atTheEndOf(XMLStreamReader self, String name) {
    self.isEndElement() && self.name() == name
  }
}