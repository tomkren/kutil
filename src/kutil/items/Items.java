package kutil.items;

import java.util.LinkedList;
import kutil.kobjects.KObject;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.xml.XmlElement;

/**
 * Třída představující balíček všech položek nějakého KObjectu.
 * @author Tomáš Křen
 */
public class Items {

    private LinkedList< Item > itemList ;

    public Items(){
        itemList = new LinkedList< Item >();
    }


    public IntegerItem addInteger( KAtts kAtts , String tag , Integer defaultVal  ){
        IntegerItem item = new IntegerItem( tag , kAtts.getInteger( tag , defaultVal ) );
        itemList.add( item );
        return item;
    }
    public IntegerItem addInteger( String tag , Integer val  ){
        IntegerItem item = new IntegerItem( tag , val );
        itemList.add( item );
        return item;
    }

    public DoubleItem addDouble( KAtts kAtts , String tag , Double defaultVal  ){
        DoubleItem item = new DoubleItem( tag , kAtts.getDouble( tag , defaultVal ) );
        itemList.add( item );
        return item;
    }

    public Int2DItem addInt2D( KAtts kAtts , String tag , Int2D defaultVal  ){
        Int2DItem item = new Int2DItem( tag , kAtts.getInt2D( tag , defaultVal ) );
        itemList.add( item );
        return item;
    }
    public Int2DItem addInt2D( String tag , Int2D val  ){
        Int2DItem item = new Int2DItem( tag , val );
        itemList.add( item );
        return item;
    }

    public StringItem addString( KAtts kAtts , String tag , String defaultVal  ){
        StringItem item = new StringItem( tag , kAtts.getString( tag , defaultVal ) );
        itemList.add( item );
        return item;
    }
    public StringItem addString( String tag , String val  ){
        StringItem item = new StringItem( tag , val );
        itemList.add( item );
        return item;
    }

    public BooleanItem addBoolean( KAtts kAtts , String tag , boolean defaultVal  ){
        BooleanItem item = new BooleanItem( tag , kAtts.getBoolean( tag , defaultVal ) , defaultVal );
        itemList.add( item );
        return item;
    }
    public BooleanItem addBoolean( String tag , boolean val , boolean defaultVal ){
        BooleanItem item = new BooleanItem( tag ,val , defaultVal );
        itemList.add( item );
        return item;
    }


    public ListItem addList( KAtts kAtts , String tag ){
        ListItem item = new ListItem( tag , kAtts.getList(tag) );
        itemList.add( item );
        return item;
    }
    public ListItem addEmptyList( String tag ){
        ListItem item = new ListItem( tag , new LinkedList<KObject>() );
        itemList.add( item );
        return item;
    }

    public void addAttsToXmlElement( XmlElement xmlElement ){

        for( Item item : itemList ){
            item.addAttToXmlElement(xmlElement);
        }

    }

}