package kutil.items;

import kutil.xml.XmlElement;

/**
 * Třída představující položku obhospodařující hodnotu typu String.
 * @author Tomáš Křen
 */
public class StringItem implements Item {

    private String  tag;
    private String  val;


    public StringItem(  String tag, String val ){
        this.tag = tag ;
        this.val = val ;
    }

    public void set( String newVal ){
        val = newVal;
    }

    public String get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        xmlElement.addAtt( tag , val );
    }

}
