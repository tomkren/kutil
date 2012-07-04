package kutil.xml;

import java.util.LinkedList;
import kutil.kobjects.KObject;
import kutil.core.Int2D;

/**
 * Reprezentuje XML element i se svým vnitřkem,
 * tak aby mohl být následně jednoduše převeden do textové podoby.
 * @author Tomáš Křen
 */
public class XmlElement implements Xml {

    private String tag;
    private LinkedList<Xml> inside;
    private LinkedList<StringString> atts;

    /**
     * dvojice stringů
     */
    private class StringString {

        public String s1;
        public String s2;

        public StringString( String att , String val ){
            s1 = att;
            s2 = val;
        }

        @Override
        public String toString() {
            return s1+"=\""+s2+"\"";
        }
    }

    /**
     * Vytvoří nový element jménem tag.
     * @param tag tag
     */
    public XmlElement( String tag ){
        this.tag = tag;
        inside   = new LinkedList<Xml>();
        atts     = new LinkedList<StringString>();
    }

    /**
     * Vlož vnitřní prvek
     * @param xml vnitřní prvek
     */
    public void add( Xml xml ){
        inside.add(xml);
    }

    /**
     * Přidej k elemetu atribut.
     * @param att klíč
     * @param val hodnota
     */
    public void addAtt( String att, String val ){
        if( val == null ) return;
        atts.add(new StringString(att, val) );
    }

    /**
     * Přidej k elemetu atribut.
     * @param att klíč
     * @param val hodnota
     */
    public void addAtt( String att, Integer val ){
        if( val == null ) return;
        atts.add(new StringString(att, val.toString() ) );
    }
    /**
     * Přidej k elemetu atribut.
     * @param att klíč
     * @param val hodnota
     */
    public void addAtt( String att, Boolean val ){
        if( val == null ) return;
        atts.add(new StringString(att, val.toString() ) );
    }
    /**
     * Přidej k elemetu atribut.
     * @param att klíč
     * @param val hodnota
     */
    public void addAtt( String att, Int2D val ){
        if( val == null ) return;
        atts.add(new StringString(att, val.toString() ) );
    }
    /**
     * Přidej k elemetu atribut.
     * @param att klíč
     * @param val hodnota
     */
    public void addAtt( String att , Double val ){
        if( val == null ) return;
        atts.add(new StringString(att, val.toString() ) );
    }
    /**
     * Tmění hodnotu atributu s klíčem "type".
     * @param type jméno typu
     */
    public void changeType( String type ){

        for( StringString ss : atts ){
            if( "type".equals(ss.s1) ){
                ss.s2 = type;
                return;
            }
        }

        for( Xml x : inside ){

            if( x instanceof XmlElement ){
                XmlElement elem = (XmlElement) x;
                if( elem.tag.equals("type") ){
                    elem.inside.clear();
                    elem.inside.add( new XmlText( type ) );
                    return;
                }
            }
        }

        //pokud jsme dospěli až sem, znamená to že musíme vytvořit nov atribut
        atts.add( new StringString("type", type ) );
    }
    /**
     * Přidej do vnitřku elemetu "[tag]text...[/tag]".
     * @param tag tag
     * @param text  text...
     */
    public void addText( String tag , String text ){

        if( text == null ) return;

        XmlElement elem = new XmlElement( tag );
        elem.add( new XmlText( text ) );
        inside.add( elem );
    }
    /**
     * Přidej do vnitřku elemetu "[tag]číslo[/tag]".
     * @param tag tag
     * @param i číslo
     */
    public void addText( String tag , Integer i ){

        if( i == null ) return;

        XmlElement elem = new XmlElement( tag );
        elem.add( new XmlText( i.toString() ) );
        inside.add( elem );
    }
    /**
     * Přidej do vnitřku elementu element se jménem tag a obsahující
     * XML reprezentaci následujících KObjektů.
     * @param tag tag
     * @param list KObjekty které budou v daném tagu
     */
    public void addKObjectList( String tag, LinkedList<KObject> list  ){

        if( list == null   ) return;
        if( list.isEmpty() ) return;

        XmlElement el = new XmlElement( tag );

        for( KObject o : list ){
            el.add( o.toXml() );
        }

        inside.add(el);
    }


    private String ods( int ods ){

        StringBuilder ret = new StringBuilder();

        for( int i=0 ; i<ods; i++ ){
            ret.append("    ");
        }

        return ret.toString();

    }

    private String toStringOdsad( int ods , boolean vypisTag ){

        StringBuilder sb = new StringBuilder();

        if( vypisTag ){

            sb.append(ods(ods)).append("<").append( tag);
            for( StringString ss : atts ){
                sb.append(" ").append( ss.toString());
            }
        }

        if( ! inside.isEmpty() ){

            if(vypisTag) sb.append( ">\n" );

            for( Xml xml : inside ){

                if( xml instanceof XmlText ){
                    sb.append(ods(ods + 1)).append( xml.toString());
                }
                else{

                    XmlElement elem = (XmlElement)xml;

                    if( !false && elem.tag.equals("inside") ){
                        sb.append( elem.toStringOdsad( ods, false ) );
                    }
                    else{
                        sb.append( elem.toStringOdsad( ods+1, true ) );
                    }
                }

            }

            if(vypisTag){
                sb.append(ods(ods)).append("</").append(tag).append( ">");
            }
        }
        else if(vypisTag){
            sb.append("/>");
        }

        if( vypisTag )sb.append("\n");

        return sb.toString();
    }

    /**
     * Vrací textovou reprezentaci daného XML elementu.
     * @return textovou reprezentaci daného XML elementu 
     */
    @Override
    public String toString() {

        return toStringOdsad(0,true);
    }



}
