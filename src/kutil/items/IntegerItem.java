package kutil.items;

import kutil.xml.XmlElement;

/**
 * Třída představující položku obhospodařující hodnotu typu Integer.
 * @author Tomáš Křen
 */
public class IntegerItem implements Item {

    private String  tag;
    private Integer val;

    public IntegerItem(  String tag, Integer val ){
        this.tag = tag ;
        this.val = val ;
    }

    public void set( Integer newVal ){
        val = newVal;
    }

    public Integer get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        xmlElement.addAtt( tag , val );
    }
}
