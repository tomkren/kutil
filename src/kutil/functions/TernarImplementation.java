package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Abstraktní třída od které je možno odvozovat implementace funkcí s třemi vstupy a jedním výstupem.
 * @author Tomáš Křen
 */
public abstract class TernarImplementation implements FunctionImplemetation {

    private String title;
    private int    titleShift;

    /**
     * Vytvoří TernarImplementation
     * @param title titulek(pak se zobrazuje na funkci)
     */
    public TernarImplementation( String title ){
        this.title = title;
        this.titleShift = 0;
    }
    public TernarImplementation( String title , int titleShift ){
        this.title      = title;
        this.titleShift = titleShift;
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
    /**
     * Počet vstupů.
     * @return počet vstupů
     */
    public int numArgs() {
        return 3;
    }

    public int numOutputs() {
        return 1;
    }

    public KObject[] compute(KObject[] objects ) {
        return new KObject[] { compute( objects[0] , objects[1], objects[2] ) };
    }

    /**
     * Ternární compute.
     */
    public abstract KObject compute(KObject o1 ,KObject o2,KObject o3);

}