package kutil.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Stack;
import kutil.kobjects.KObject;
import kutil.kobjects.Text;
import kutil.kobjects.Time;
import kutil.core.IdDB;
import kutil.core.KAtts;
import kutil.kobjects.KObjectFactory;
import kutil.core.Global;
import kutil.core.Log;
import kutil.core.Rucksack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Třída určená pro převod XML reprezentace do hierarchie KObjectů.
 * @author Tomáš Křen
 */
public class XmlLoader implements ContentHandler {

    private static final boolean loguj = !true;

    // Umožnuje zacílit místo v dokumentu, kde vznikla aktualní událost
    private Locator locator;

    private StringBuilder textBuffer; // pomáhá načítat text
    private StringBuilder odsazeni;   // pouze pro ladící učely, textové odsazení

    /**
     * Aktuální stav automatu zpracovávajícího XML soubor
     */
    private enum Stav{ init, insideObject , insideTag, insideInnerObject }

    private Stav stav; //Aktuální stav automatu zpracovávajícího XML soubor

    private Stack<Stav>   minuleStavy; //zasobník stavů
    private Stack<String> aktualniTag; //zasobník tagů
    private Stack<KAtts>  kAttsStack;  //zásobník s kAtts

    private LinkedList<Time> times;    //reference na objevivší se instance Time

    private IdDB     idDB;             // reference na globální DB unik. id

    /**
     * Inicializace automatu.
     */
    private void init(){
        stav        = Stav.init;
        idDB        = Global.idDB();    

        minuleStavy = new Stack<Stav>();
        aktualniTag = new Stack<String>();
        kAttsStack  = new Stack<KAtts>();
        kAttsStack.add( new KAtts() );

        textBuffer  = new StringBuilder();
        odsazeni    = new StringBuilder();

        times       = new LinkedList<Time>();
    }

    /**
     * Vytvoří Loader.
     */
    public XmlLoader(){}

    /**
     * Nahraje interní XML soubor z balíčku kutil.xml
     * @param filename jméno souboru z balíčku kutil.xml
     * @return hierarchie KObjektů zabalená v třídě KAtts
     */
    public KAtts loadResource( String filename ) {

        try {
            filename = "/kutil/xml/" + filename ;
            InputStream inputStream = getClass().getResource(filename).openStream();
            InputSource source = new InputSource( inputStream );

            return loadFromInputSource( source );
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Nahraje externí XML soubor, aktuální aresář je ten kde byl zpuštěn program.
     * @param filename jméno souboru
     * @return hierarchie KObjektů zabalená v třídě KAtts
     */
    public KAtts loadFile( String filename ) {

        try {
            InputSource source = new InputSource( filename );
            return loadFromInputSource( source );
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Nahraje z textového řetězce reprezentujícího XML data.
     * @param str XML data
     * @return hierarchie KObjektů zabalená v třídě KAtts
     */
    public KAtts loadString( String str ) {

        try {
            InputSource source = new InputSource( new StringReader(str) );
            return loadFromInputSource( source );
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class XmlLoaderException extends SAXException {

        public XmlLoaderException( String msg ){
            super(msg);
        }
    }

    private void error( String msg ) throws XmlLoaderException {
        Log.it( "[" + locator.getLineNumber() +"] XML-ERROR: " + msg );
        throw new XmlLoaderException(msg);
    }

    private KAtts loadFromInputSource( InputSource source ){

        init();

        try {
            // Vytvoríme instanci parseru.
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // Nastavíme náš vlastní content handler pro obsluhu SAX událostí.
            parser.setContentHandler( this );

            // Zpracujeme vstupní proud XML dat.
            parser.parse( source );

            KAtts ret = kAttsStack.peek();

            ret.parentInfo();
            ret.init();

            for( Time t : times ){
                ret.add("times", t);
            }

            return ret;
        }
        catch ( XmlLoaderException e ){
            //(...)
            return null;
        }
        catch ( SAXException e ){
            //e.printStackTrace();
            Log.it( "[" + locator.getLineNumber() +"] XML-ERROR: " + e.getMessage() );
            return null;
        }
        catch ( IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nastaví locator
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Obsluha události "znaková data".
     * SAX parser muže znaková data dávkovat jak chce. Nelze tedy počítat s tím,
     * že je celý text dorucen v rámci jednoho volání.
     * Text je v poli (ch) na pozicich (start) az (start+length-1).
     * @param ch Pole se znakovými daty
     * @param start Index zacátku úseku platných znakových dat v poli.
     * @param length Délka úseku platných znakových dat v poli.
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        textBuffer.append(ch, start, length);
    }

    /**
     * Obsluha události "začátek elementu".
     * @param uri URI jmenného prostoru elementu (prázdné, pokud element není v žádném jmennem prostoru)
     * @param localName Lokální jméno elementu (vždy neprázdné)
     * @param qName Kvalifikované jméno (tj. prefix-uri + ':' + localName, pokud je element v nějakém jmenném
     * prostoru, nebo localName, pokud element není v žádném jmenném prostoru)
     * @param atts Atributy elementu
     */
    public void startElement(String uri,String localName,String qName,Attributes atts) throws SAXException{

        if( stav == Stav.init && !"kutil".equals(localName) ){
            error("Kořenový element musí být <kutil>.");
        }

        minuleStavy.push( stav );

        if( "object".equals(localName) ){

            if( stav == Stav.insideTag ){
                stav = Stav.insideObject;
                kAttsStack.push(new KAtts() );
            }
            else if( stav == Stav.insideObject || stav == Stav.insideInnerObject ){
                //object uvnitř objectu

                stav = Stav.insideInnerObject;
                kAttsStack.push(new KAtts() );

                //error("Object uvnitř objectu.");
            }

            //zpracuji attributy
            KAtts kAtts = kAttsStack.peek();
            for( int i=0 ; i<atts.getLength() ; i++ ){
                kAtts.add( atts.getLocalName(i) , new Text( atts.getValue(i) ) );
                if(loguj)Log.it( "[ATTRIBUT] "+ atts.getLocalName(i) +" : "+ atts.getValue(i) );
            }

        }
        else{ //TAG

            if( stav == Stav.insideTag ){
                error("Na místo <"+ localName +"> by měl být element <object> nebo text.");
            }

            stav = Stav.insideTag;
            aktualniTag.push(localName);
        }

        //atts.getValue("kdata");
        if(loguj)Log.it( odsazeni + " -> Vstupuji do elementu <" + localName + ">, jsem ve stavu "+ stav+"." );
        odsazeni.append(" ");

    }

    /**
     * Obsluha události "konec elementu"
     * Parametry mají stejný vyznam jako u @see startElement
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if( stav == Stav.insideTag ){
            if( textBuffer.length() != 0 ){

                String str = textBuffer.toString().replaceAll("\\s+", " ").trim();
                textBuffer.setLength(0);

                if( !"".equals(str) ){
                    Text text = new Text( str );
                    if(loguj)Log.it( odsazeni + "[text] " + text );
                    kAttsStack.peek().add( aktualniTag.peek() , text );
                }
            }
            aktualniTag.pop();
        }
        else if( stav == Stav.insideObject ||
                 stav == Stav.insideInnerObject ){

            if(loguj)Log.it("[vytvářím OBJECT a vkládam ho do tagu "+aktualniTag.peek() +"]");

            KAtts kAttsProNovyObject = kAttsStack.pop();
            KObject newKObject = KObjectFactory.newKObject( kAttsProNovyObject );

            String id = kAttsProNovyObject.getString("id") ;
            if ( id != null ) {
                idDB.put(id, newKObject);
                if(loguj)Log.it("pridano id "+id);
            }

            String tag = (  stav == Stav.insideObject  ?  aktualniTag.peek() : "inside"  ) ;

            kAttsStack.peek().add( tag , newKObject );

            if( newKObject instanceof Time ){
                times.add( (Time)newKObject );
            }
        }

        stav = minuleStavy.pop();

        if(loguj)Log.it( odsazeni + "<- Vystupuji z elementu <" + localName + ">, jsem ve stavu "+ stav+"."+
                (stav==Stav.insideTag ? " Uvnitř <"+ aktualniTag.peek()+">." : "" ) );
        odsazeni.deleteCharAt(0);
    }


    /**
     * Obsluha události "začátek dokumentu"
     */
    public void startDocument() throws SAXException {
        // ...
    }
    /**
     * Obsluha události "konec dokumentu"
     */
    public void endDocument() throws SAXException {
        // ...
    }


    /**
     * Obsluha události "deklarace jmenného prostoru".
     * @param prefix Prefix prirazení jmennému prostoru.
     * @param uri URI jmenného prostoru.
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

        // ...

    }
    /**
     * Obsluha události "konec platnosti deklarace jmenného prostoru".
     */
    public void endPrefixMapping(String prefix) throws SAXException {

        // ...

    }
    /**
     * Obsluha události "ignorovaní bílý znaky".
     * Stejné chování a parametry jako @see characters
     */
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

        // ...

    }
    /**
     * Obsluha události "instrukce pro zpracování".
     */
    public void processingInstruction(String target, String data) throws SAXException {

      // ...

    }
    /**
     * Obsluha události "nezpracovaná entita"
     */
    public void skippedEntity(String name) throws SAXException {

      // ...

    }
}
