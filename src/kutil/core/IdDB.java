package kutil.core;

import java.util.HashMap;
import kutil.kobjects.KObject;

/**
 * Třída sloužící jako aktuální databáze všech objektů virtuálního světa,
 * kteréžto jsou zde přístupne pod svým unikátním id.
 * V programu se používá jediná instance přístupná přes třídu Global.
 * @author Tomáš Křen
 */
public class IdDB {

    private HashMap< String , KObject > tab ;
    private int nextId;

    /**
     * Vytvoří novou databázi.
     */
    public IdDB(){
        tab    = new HashMap<String, KObject>();
        nextId = 1;
    }

    /**
     * Přejmenuje id na nové jméno.
     * @param oldId původní id
     * @param newId nové id
     * @return povedlo se přejmenovat?
     */
    public boolean rename( String oldId , String newId ){
        if( oldId == null || newId == null )    return false;
        if( tab.containsKey( newId ) )          return false;
        if( ! tab.containsKey(oldId) )          return false;

        KObject o = tab.remove(oldId);
        tab.put(newId, o);

        return true;
    }

    /**
     * vloží nový záznam do databaze.
     * @param id unikátní id
     * @param o referencec na KObjekt s tímto id
     */
    public void put( String id , KObject o ){
        tab.put(id, o);
    }

    /**
     * Vrátí KObjekt s daným id
     * @param unikátní id požadovaného objektu
     * @return referenc ena požadovaný KObjekt
     */
    public KObject get( String id ){
        if( id == null ) return null;
        return tab.get(id);
    }

    /**
     * odstraní záznam z databáze na základě id.
     * @param id odstraňované id
     */
    public void remove( String id ){
        tab.remove(id);
    }

    /**
     * Vrací unikátní ještě nepoužité id.
     * @return unikátní ještě nepoužité id
     */
    public String getUniqueID(){
        String candidate = "$" + nextId ;
        if( tab.containsKey( candidate ) ){
            nextId ++ ;
            return getUniqueID();
        }
        return candidate;
    }

}
