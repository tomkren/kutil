package kutil.core;

import javax.swing.UIManager;
import kutil.xml.XmlLoader;

/**
 * Tato třída obsahuje funkci main.
 * @author Tomáš Křen
 */
public class Main {

    
    private static final String defaultXmlFile = "main.xml";

    /**
     * Funkce main programu.
     * @param args Argumenty příkazové řádky, předpokládá se prázdný nebo jméno XML souboru
     */
    public static void main( String[] args ){

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}

        //main_test(args);
        main_noTest(args);
    }

    /**
     * Varianta funkce main bez testu.
     * @param args Argumenty příkazové řádky, předpokládá se prázdný nebo jméno XML souboru
     */
    private static void main_noTest( String[] args ){

        Global.init();

        XmlLoader loader = new XmlLoader();
        KAtts loaded = null;
        if( args.length > 0 ){
            loaded = loader.loadFile( args[0] );
        }else{
            loaded = loader.loadResource( defaultXmlFile );
        }

        if( loaded == null ) {
            Log.it("[XML-ERROR => nic se nenahrálo.]");
        }
        else {
            Scheduler s = new Scheduler(loaded);
        }
    }

    /**
     * Varianta funkce main s testem.
     * Jedná se o test nahrávání a následného generování XML reprezentace.
     * Nejprve se nahraj ze souboru čímž s epřevede XML reprez. do KObjectů,
     * -> to se převede do XML -> to znovu nahraje a až teď se opravdu spouští běh programu.
     * @param args Argumenty příkazové řádky
     */
    private static void main_test( String[] args ){

        Global.init();

        XmlLoader loader = new XmlLoader();
        KAtts loaded = loader.loadResource( defaultXmlFile );

        if( loaded == null ) {
            Log.it("[XML-ERROR => nic se nenahrálo.]");
        }
        else {

            String xmlString = Scheduler.getXMLString( loaded ) ;

            Log.it("------------------------------------------------------");

            Global.init();

            KAtts loaded2 = loader.loadString( xmlString );

            if( loaded2 == null ) {
                Log.it("[LEVEL2: XML-ERROR => nic se nenahrálo.]");
            }
            else {
                Scheduler s = new Scheduler( loaded2 );
            }
        }
    }

}
