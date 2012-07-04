package kutil.items;

import java.util.LinkedList;
import kutil.kobjects.KObject;
import kutil.xml.XmlElement;

/**
 * Třída představující položku obhospodařující hodnotu seznam KObjectů.
 * @author Tomáš Křen
 */
public class ListItem implements Item {

    private String              tag;
    private LinkedList<KObject> val;

    public ListItem(  String tag, LinkedList<KObject> val ){
        this.tag = tag ;
        this.val = val ;
    }

    public void set( LinkedList<KObject> newVal ){
        val = newVal;
    }

    public LinkedList<KObject> get(){
        return val;
    }

    public void addAttToXmlElement( XmlElement xmlElement ){
        xmlElement.addKObjectList( tag , val );
    }

}
