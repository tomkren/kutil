package kutil.core;

import java.util.HashMap;
import java.util.LinkedList;
import kutil.kobjects.KObject;
import kutil.kobjects.Text;

/**
 * Třída sloužící jako společný typ argumentu pro všechny konstruktory KObjectů využívaných
 * při převodu z XML reprezentace.
 * Informuje KObjekt o jeho "součástkách", ty jsou indexovány textovým řetězcem, kterému zde říkáme tag.
 * Tag odpovídá klíči v abstraktním pojetí objektu virtuálního světa, viz text práce.
 * Je realizována jako tabulka dvojic klíč - seznam-hodnot-pod-tímto-klíčem,
 * která odpovídá těmto dvojicím v XML reprezentaci.
 * Zajišťuje přístup k hodnotám přes klíč, volající si může vyžádat jak se má daná hodnota interpretovat
 * (jakého bude typu).
 * @author Tomáš Křen
 */
public class KAtts {

    private HashMap< String , LinkedList<KObject> > atts;     // seznam vnitřních objektů indexovaný jménem


    /**
     * Vytvoří prázdný KAtts.
     */
    public KAtts( ){
        atts = new HashMap<String, LinkedList<KObject>>();
    }

    /**
     * Započne kaskádu informování všech objektů uvnitř KAtts o jejich rodičovských KObjektech.
     * Jelikož KObjekty na vrcholu hierarchie nemají rodiče, jsou volány s argumentem null.
     * Tato metoda je určena pouze pro volání z XmlLoader.
     */
    public void parentInfo(){
        for( LinkedList<KObject> list : atts.values() ){
            for( KObject o : list ){
                o.parentInfo(null);
            }
        }
    }

    /**
     * Započne kaskádu inicializací.
     * Tato metoda je určena pouze pro volání z XmlLoader.
     */
    public void init(){
        for( LinkedList<KObject> list : atts.values() ){
            for( KObject o : list ){
                o.init();
            }
        }
    }

    /**
     * odpovídá na otázku, zda se něco nachází pod tagem tag.
     * @param tag dotazovaný tag.
     * @return zda se něco nachází pod daným tagem
     */
    public boolean has( String tag ){
        return ( atts.get(tag) != null ) ;
    }

    /**
     * Přidává KObject pod daný tag.
     * @param tag tag pod který bude přidáno
     * @param o objekt který bude přidán
     * @return KAtts do kterého bylo přidáno
     */
    public KAtts add( String tag , KObject o ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null  ){
            list = new LinkedList<KObject>();
            list.add( o );
            atts.put( tag, list );
        }
        else {
            list.add(o);
        }

        return this;
    }

    /**
     * Vrací první hodnotu ze seznamu hodnot pod zadaným tagem.
     * @param tag zadaný tag
     * @return první hodnota z odpovídajících (tomuto tagu) KObjektů
     */
    public KObject get( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null ){
            return null;
        }
        if( list.isEmpty() ){
            return null;
        }
        return list.getFirst();
    }

    /**
     * Vrací celý seznam hodnot pod tímto tagem.
     * @param tag zadaný tag
     * @return celý seznam hodnot
     */
    public LinkedList<KObject> getList( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null ){
            return new LinkedList<KObject>();
        }

        return list;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Int2D, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací dosazenou defaultVal hodnotu.
     * @param tag zadaný tag
     * @param defaultVal implicitní hodnota pro možnost, že pod tímto tagem není uložena žádná hodnota.
     * @return první hodnota interpretovaná jako Int2D.
     */
    public Int2D getInt2D( String tag, Int2D defaultVal ){
        Int2D ret = getInt2D(tag);
        if( ret == null ) ret = defaultVal;
        return ret;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Int2D, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací null.
     * @param tag zadaný tag
     * @return první hodnota interpretovaná jako Int2D, případně null při prázdném tagu.
     */
    public Int2D getInt2D( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null )  {return null;}
        if( list.isEmpty() ){return null;}

        KObject o = list.getFirst();
        if( o instanceof Text ){
            return Int2D.parseInt2D( ((Text)o).get() );
        }
        return null;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Boolean, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací dosazenou defaultVal hodnotu.
     * @param tag zadaný tag
     * @param defaultVal implicitní hodnota pro možnost, že pod tímto tagem není uložena žádná hodnota.
     * @return první hodnota interpretovaná jako Boolean.
     */
    public Boolean getBoolean( String tag, boolean defaultVal  ){
        Boolean ret = getBoolean(tag);
        if( ret == null ) return defaultVal;
        return ret;
    }
    /**
     * Interpretuje první hodnotu pod tagem jako Boolean, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací null.
     * @param tag zadaný tag
     * @return první hodnota interpretovaná jako Boolean, případně null při prázdném tagu.
     */
    public Boolean getBoolean( String tag ){
        LinkedList<KObject> list = atts.get( tag );

        if( list == null )  {return null;}
        if( list.isEmpty() ){return null;}

        KObject o = list.getFirst();

        if( o instanceof Text ){
            return Boolean.parseBoolean( ((Text)o).get() );
        }
        return null;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako String, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací dosazenou defaultVal hodnotu.
     * @param tag zadaný tag
     * @param defaultVal implicitní hodnota pro možnost, že pod tímto tagem není uložena žádná hodnota.
     * @return první hodnota interpretovaná jako String.
     */
    public String getString( String tag, String defaultVal ){
        String ret = getString(tag);
        if( ret == null ) return defaultVal;
        return ret;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako String, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací null.
     * @param tag zadaný tag
     * @return první hodnota interpretovaná jako String, případně null při prázdném tagu.
     */
    public String getString( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null )  {return null;}
        if( list.isEmpty() ){return null;}

        KObject o = list.getFirst();

        if( o instanceof Text ){
            return ((Text)o).get();
        }
        return null;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Integer, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací dosazenou defaultVal hodnotu.
     * @param tag zadaný tag
     * @param defaultVal implicitní hodnota pro možnost, že pod tímto tagem není uložena žádná hodnota.
     * @return první hodnota interpretovaná jako Integer.
     */
    public Integer getInteger( String tag, Integer defaultVal ){
        Integer ret = getInteger(tag);
        if( ret == null ) return defaultVal;
        return ret;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Integer, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací null.
     * @param tag zadaný tag
     * @return první hodnota interpretovaná jako Integer, případně null při prázdném tagu.
     */
    public Integer getInteger( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null ){return null;}
        if( list.isEmpty() ){return null;}

        KObject o = list.getFirst();

        if( o instanceof Text ){
            return Integer.parseInt(  ((Text)o).get()  ) ;
        }
        return null;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Double, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací dosazenou defaultVal hodnotu.
     * @param tag zadaný tag
     * @param defaultVal implicitní hodnota pro možnost, že pod tímto tagem není uložena žádná hodnota.
     * @return první hodnota interpretovaná jako Double.
     */
    public Double getDouble( String tag, Double defaultVal ){
        Double ret = getDouble(tag);
        if( ret == null ) return defaultVal;
        return ret;
    }

    /**
     * Interpretuje první hodnotu pod tagem jako Double, pokud pod tímto tagem
     * není uložena žádná hodnota, vrací null.
     * @param tag zadaný tag
     * @return první hodnota interpretovaná jako Double, případně null při prázdném tagu.
     */
    public Double getDouble( String tag ){

        LinkedList<KObject> list = atts.get( tag );

        if( list == null   ) { return null; }
        if( list.isEmpty() ) { return null; }

        KObject o = list.getFirst();

        if( o instanceof Text ){
            return  Double.parseDouble( ((Text)o).get() );
        }
        return null;
    }


}
