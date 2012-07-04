package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Abstraktní třída od které je možno odvozovat implementace funkcí s jedním vstupem a dvěma výstupy.
 * @author Tomáš Křen
 */
public abstract class UnarBinarImplementation implements FunctionImplemetation {

    private String title;
    private int    titleShift;

    /**
     * Vytvoří UnarBinarImplementation
     * @param title titulek(pak se zobrazuje na funkci)
     */
    public UnarBinarImplementation( String title ){
        this.title = title;
        this.titleShift = 0;
    }
    /**
     * zpřístupní titulek
     * @return titulek
     */
    public UnarBinarImplementation( String title , int titleShift ){
        this.title      = title;
        this.titleShift = titleShift;
    }
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
    /**
     * Počet vstupů.
     * @return počet vstupů
     */
    public int numArgs() {
        return 1;
    }

    public int numOutputs() {
        return 2;
    }

    public KObject[] compute(KObject[] objects ) {
        KObject[] ret = compute( objects[0] );
        return new KObject[]{ ret[0] , ret[1] };
    }

    public abstract KObject[] compute(KObject o);

}
