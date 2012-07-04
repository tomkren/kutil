package kutil.items;

import kutil.xml.XmlElement;



/**
 * Třída představující položku obhospodařující hodnotu typu boolean.
 * @author Tomáš Křen
 */
public class BooleanItem implements Item {

    private String  tag;
    private boolean val;
    private boolean defaultVal;

    public BooleanItem(  String tag, boolean val , boolean defaultVal ){
        this.tag = tag ;
        this.val = val ;
        this.defaultVal = defaultVal ;
    }

    public void set( boolean newVal ){
        val = newVal;
    }

    public boolean get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        if( val != defaultVal ){
            xmlElement.addAtt( tag , val );
        }
    }
}
