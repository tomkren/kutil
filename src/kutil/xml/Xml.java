package kutil.xml;

/**
 * Rozhraní zaštiťující XmlElement a XmlText do jednoho typu. 
 * @author Tomáš Křen
 */
public interface Xml {

    /**
     * Vrací textovou reprezentaci daného XML elementu (nebo textu).
     * @return textovou reprezentaci daného XML elementu (nebo textu)
     */
    @Override
    public String toString();

}
