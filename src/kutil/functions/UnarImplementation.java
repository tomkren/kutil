package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Abstraktní třída od které je možno odvozovat implementace funkcí s jedním vstupem a jedním výstupem.
 * @author Tomáš Křen
 */
public abstract class UnarImplementation implements FunctionImplemetation {

    private String title;
    private int    titleShift;

    /**
     * Vytvoří UnarImplementation
     * @param title
     */
    public UnarImplementation( String title ){
        this.title = title;
        this.titleShift = 0;
    }
    /**
     * zpřístupní titulek
     * @return titulek
     */
    public String title(){
        return title;
    }
    /**
     * Posun titulku na x-ové ose pro vycentrování.
     * @return titulek
     */
    public int getTitleShift(){
        return titleShift;
    }

    public UnarImplementation( String title , int titleShift ){
        this.title      = title;
        this.titleShift = titleShift;
    }
    /**
     * Počet vstupů.
     * @return počet vstupů
     */
    public int numArgs() {
        return 1;
    }

    public int numOutputs() {
        return 1;
    }

    public KObject[] compute(KObject[] objects ) {
        return new KObject[]{ compute( objects[0] ) };
    }

    public abstract KObject compute(KObject o);

}
