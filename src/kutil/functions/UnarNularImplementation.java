package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Abstraktní třída od které je možno odvozovat implementace funkcí s jedním vstupem a žádnám výstupem.
 * @author Tomáš Křen
 */
public abstract class UnarNularImplementation implements FunctionImplemetation {

    private String title;
    private int    titleShift;

    /**
     * Vytvoří UnarNularImplementation
     * @param title titulek(pak se zobrazuje na funkci)
     */
    public UnarNularImplementation( String title ){
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

    public UnarNularImplementation( String title , int titleShift ){
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
        return 0;
    }

    public KObject[] compute(KObject[] objects ) {
        compute( objects[0] );
        return new KObject[]{ };
    }

    public abstract void compute(KObject o);

}
