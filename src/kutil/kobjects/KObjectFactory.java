package kutil.kobjects;

import java.util.LinkedList;
import kutil.core.Global;
import kutil.core.KAtts;
import kutil.xml.XmlLoader;

/**
 * Tato třída je souborem statických metod pro manipulaci s KObjekty (při vytváření).
 * Důležité metody jsou newKObject, v jejímž těle se musí zaregistrovat nové typy objektů,
 * aby mohly být použity v programu.
 * Dále je důležitá metoda insertKObjectToSystem, která zajišťuje správné vložení nového
 * objektu do systému.
 * @author Tomáš Křen
 */
public class KObjectFactory {

    /**
     * Tato metoda na základě KAtts (podíva se na tag "type" jehož hodnotu interpretuje jako string)
     * sama rozhodne o typu konstruktoru k použití a vytvoří nový KObject.
     * V rámci této metody se registrují nové typy KObjektů do programu!
     * @param kAtts definuje nový objekt, balíček otagovaných skupin KObjektů.
     * @return
     */
    public static KObject newKObject( KAtts kAtts ){

        KObject type     = kAtts.get("type");
        String  typeName = "basic";

        if( type instanceof Text ){
           typeName = ((Text)type).get(); 
        }

        // registrace typů KObjectů do programu:

        if( "basic"         .equals(typeName) ) return new Basic      ( kAtts );
        if( "button"        .equals(typeName) ) return new Button     ( kAtts );
        if( "frame"         .equals(typeName) ) return new Frame      ( kAtts );
        if( "function"      .equals(typeName) ) return new Function   ( kAtts );
        if( "in"            .equals(typeName) ) return new In         ( kAtts );
        if( "logger"        .equals(typeName) ) return new Logger     ( kAtts );
        if( "num"           .equals(typeName) ) return new Num        ( kAtts );
        if( "out"           .equals(typeName) ) return new Out        ( kAtts );
        if( "time"          .equals(typeName) ) return new Time       ( kAtts );
        if( "tool"          .equals(typeName) ) return new Tool       ( kAtts );
        if( "box"           .equals(typeName) ) return new Box        ( kAtts );
        if( "fly"           .equals(typeName) ) return new Fly        ( kAtts );
        if( "wasp"          .equals(typeName) ) return new Wasp       ( kAtts );
        if( "apple"         .equals(typeName) ) return new Apple      ( kAtts );
        if( "bool"          .equals(typeName) ) return new Bool       ( kAtts );
        if( "symbol"        .equals(typeName) ) return new Symbol     ( kAtts );
        if( "recursion"     .equals(typeName) ) return new Recursion  ( kAtts );
        if( "direction"     .equals(typeName) ) return new Direction  ( kAtts );
        if( "slot"          .equals(typeName) ) return new Slot       ( kAtts );
        if( "comment"       .equals(typeName) ) return new Comment    ( kAtts );
        if( "budha"         .equals(typeName) ) return new Budha      ( kAtts );
        if( "mr"            .equals(typeName) ) return new Mr         ( kAtts );
        if( "field"         .equals(typeName) ) return new Field      ( kAtts );
        if( "ffunit"        .equals(typeName) ) return new FFUnit     ( kAtts );
        if( "multiobject"   .equals(typeName) ) return new MultiObject( kAtts );
        if( "panacek"       .equals(typeName) ) return new Panacek    ( kAtts );
        
        if( "touchSensor"   .equals(typeName) ) return new TouchSensor( kAtts );
        if( "goalSensor"    .equals(typeName) ) return new GoalSensor ( kAtts );
        if( "webOutput"     .equals(typeName) ) return new WebOutput  ( kAtts );
        if( "clock"         .equals(typeName) ) return new Clock      ( kAtts );
        if( "incubator"     .equals(typeName) ) return new Incubator  ( kAtts );


        
        return new Basic( kAtts );
    }

    /**
     * na základě XML textových dat vytvoří nový KObject
     * @param xmlString XML textová data
     * @return nový KObject
     */
    public static KObject newKObject( String xmlString ){

        XmlLoader loader = new XmlLoader();
        KAtts kAtts = loader.loadString( "<kutil>" + xmlString + "</kutil>" );
        return kAtts.get("kutil");
    }

    /**
     * na základě interního xml souboru vytvoří sadu nových KObjectů
     * @param filename  XML soubor v balíčku kutil.xml
     * @return sada nových KObjectů
     */
    public static LinkedList<KObject> newKObjectFromResource( String filename ){

        XmlLoader loader = new XmlLoader();
        KAtts kAtts = loader.loadResource(filename);
        return kAtts.getList( "kutil" );
    }

    /**
     * na základě externího xml souboru vytvoří sadu nových KObjectů
     * @param filename  XML soubor - cesta z aktualního adresáře programu
     * @return sada nových KObjectů
     */
    public static LinkedList<KObject> newKObjectFromFile( String filename ){

        XmlLoader loader = new XmlLoader();
        KAtts kAtts = loader.loadFile(filename);
        return kAtts.getList( "kutil" );
    }

    /**
     * Hojně používaná metoda, která zajišťuje správné vložení nového
     * objektu do systému. TODO: odstranit tuto metodu tak aby vytvoření nového objektu
     * automaticky implikovalo tyto kroky.
     * @param newKObject
     * @param parent
     * @return
     */
    public static KObject insertKObjectToSystem( KObject newKObject , KObject parent ){

        //if( newKObject.isInitialised() ) return newKObject ;

        newKObject.parentInfo( parent );
        newKObject.init();

        newKObject.resolveCopying();
        Global.idChangeDB().clear();

        return newKObject ;
    }

}
