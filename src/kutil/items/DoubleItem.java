package kutil.items;

import kutil.xml.XmlElement;

/**
 * Třída představující položku obhospodařující hodnotu typu double.
 * @author Tomáš Křen
 */
public class DoubleItem implements Item {

    private String tag;
    private Double val;

    public DoubleItem(  String tag, Double val ){
        this.tag = tag ;
        this.val = val ;
    }

    public void set( Double newVal ){
        val = newVal;
    }

    public Double get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        xmlElement.addAtt( tag , val );
    }
}