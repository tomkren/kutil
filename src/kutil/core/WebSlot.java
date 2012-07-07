package kutil.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Třída zajišťující komunikaci s jednoduchým webovým serverem, zajišťující přesun
 * objektů virtuálního světa po internetu.
 * Používají se static metody.
 * WebSlotem se rozumí virtuální místo na serveru s unikatním id portem, kam je možno posílat
 * objekty - taková schránka.
 * Kontext: Pro komunikaci po internetu existují dvě speciální funkce virtuálního světa.
 * Funkce webInput má jeden vstup a žádný výstup, slouží k odeslání objektu na server
 * (jedná se o minimalistický server napsaný v jazyku PHP, v současné chvíli se
 * nachází na doméně kutil.php5.cz), kde je do databáze uložena jeho XML reprezentace.
 * Společně s XML reprezentací se na server odesílá i identifikační port, což je
 * textový řetězec fungující jako klíč, pod kterým je daný objekt uložen.
 * Funkce webOutput, která má jeden výstup a žádný vstup, pokud je použita
 * ve virtuálním světe pravidelně kontroluje, zda se na severu pod sledovaným
 * identifikačním portem neobjevil nějaký objekt, pokud ano ze serveru je odstraněn
 * a funkce ho vrátí jako svůj výstup.
 * Tato funkcionalita je zatím spíše v experimentální fázi, takže komunikace například
 * probíhá stále na stejném identifikačním portu.
 * @author Tomáš Křen
 */

public class WebSlot {

    private static final String server = "http://kutil.php5.cz" ; //umístění serveru na webu

    /**
     * Testovací metoda main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        WebSlot ws = new WebSlot();

        while(true){
            ws.testStep();
        }
        
    }

    private long lastCheck;
    private int i;

    private WebSlot(){
        lastCheck = time();
        i = 1;
    }

    private void testStep(){
        if( time() - lastCheck > 1000 ){
            lastCheck = time();

            setWebSlot_post("kutil", "<object type=\"lol\">"+i+"</object>" );
            Log.it( getWebSlot("kutil") );
            i++;
        }
    }

    /**
     * Metoda pro přístup na server.
     * @param id identifikační port
     * @return obsah "webSlotu" na serveru (XML reprezentace objektu v daném webSlotu)
     */
    public static String getWebSlot( String id ){
        return getPage( "?get=" + id );
    }

      /**
     * nastavit obsah webSlotu metodou GET (nedoporučeno).
     * @param id id port
     * @param val XML reprezentace objektu ve stringu
     */
    @Deprecated
    public static String setWebSlot_get( String id , String val ){
        try {

            val = val.replace('\n', ' ');
            val = URLEncoder.encode( val, "UTF-8" );

            //Log.it("loguju: >"+val+"<");

            
            return getPage( "?set=" + id + "&val=" + val );
        } catch ( UnsupportedEncodingException e){
            return null;
        }
    }

    /**
     * nastavit obsah webSlotu metodou POST (doporučeno).
     * @param id id port
     * @param val XML reprezentace objektu ve stringu
     */
    public static void setWebSlot_post( String id , String val ){
        try {
            // Construct data
            String data =  "set=" + URLEncoder.encode( id  , "UTF-8")
                        + "&val=" + URLEncoder.encode( val , "UTF-8");

            // Send data
            URL url = new URL( server );
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            wr.close();
            rd.close();

        } catch (Exception e) {
            Log.it("nepo");
        }
    }

    private static String getPage( String page ){

        try {

            URL yahoo = new URL( server + "/" + page );

            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    yahoo.openStream()));


            StringBuilder sb = new StringBuilder();

            String inputLine;

            while ((inputLine = in.readLine()) != null){
                sb.append( inputLine );
            }

            in.close();
            
            return sb.toString() ;

        } catch(Exception e){
            return null;
        }
    }


    private static long time(){
        return System.currentTimeMillis();

        //return System.nanoTime()/1000000L; //v milisekundach
    }

}