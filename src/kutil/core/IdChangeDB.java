package kutil.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Pomocná třída umožňující překlad unikátních id (ze starých na nově vzniklá) při kopírování KObjectů.
 * V programu se používá jediná instance přístupná přes třídu Global.
 * @author Tomáš Křen
 */
public class IdChangeDB {

    private HashMap< String , String > tab ;

    /**
     * Vytvoří novou pomocnou databázi.
     */
    public IdChangeDB() {
        tab = new HashMap<String, String>();
    }

    /**
     * Vloží zápis do databáze
     * @param oldId půbvodní id
     * @param newId nové id
     */
    public void put( String oldId , String newId ){
        tab.put(oldId, newId);
    }

    /**
     * Vyčistí databázi pro další použití.
     */
    public void clear(){
        tab.clear();
    }

    /**
     * Opraví předložený targetString na základě hodnot uložených v databázu.
     * @param targetString původní target string
     * @return aktualizovaný target string
     */
    public String repairTargetString( String targetString ){
        
        if( targetString == null )    return null;
        if( targetString.equals("") ) return "";

        StringBuilder sb = new StringBuilder();

        String[] ts = targetString.split(" ");

        for( int i=0 ; i<ts.length ; i++ ){

            if( ts[i].equals("null") ){
                sb.append("null");
            } else {
                String[] part = ts[i].split(":");

                if( tab.containsKey( part[0] ) ){
                    sb.append( tab.get(part[0]) );
                }else{
                    sb.append(part[0]);
                }
                sb.append(":");
                sb.append(part[1]);
            }

            if( i != ts.length-1 ) sb.append(" ");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("idChangeDB : \n");
        Iterator<Entry<String,String>> it =  tab.entrySet().iterator();

        while( it.hasNext() ){
            Entry<String,String> ent = it.next();
            sb.append( ent.getKey() );
            sb.append( "->" );
            sb.append( ent.getValue() );
            sb.append( '\n' );
        }

        return sb.toString();
    }



}
