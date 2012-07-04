package kutil.items;

import kutil.xml.XmlElement;

/**
 * Rozhraní které musí implementovat každá položka.
 * @author Tomáš Křen
 */
public interface Item {
    public void addAttToXmlElement( XmlElement xmlElement );
}
