package kutil.items;

import kutil.core.Int2D;
import kutil.xml.XmlElement;

/**
 * Třída představující položku obhospodařující hodnotu typu Int2D.
 * @author Tomáš Křen
 */
public class Int2DItem implements Item {

    private String  tag;
    private Int2D   val;


    public Int2DItem(  String tag, Int2D val ){
        this.tag = tag ;
        this.val = val ;
    }

    public void set( Int2D newVal ){
        val = newVal;
    }

    public Int2D get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        xmlElement.addAtt( tag , val );
    }
}
