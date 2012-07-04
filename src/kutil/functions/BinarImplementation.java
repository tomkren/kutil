package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Abstraktní třída od které je možno odvozovat implementace funkcí s dvěma vstupy a jedním výstupem.
 * @author Tomáš Křen
 */
public abstract class BinarImplementation implements FunctionImplemetation {

    private String title;
    private int    titleShift;

    /**
     * Vytvoří BinarImplementation
     * @param title
     */
    public BinarImplementation( String title ){
        this.title = title;
        this.titleShift = 0;
    }
    /**
     * Vytvoří BinarImplementation
     * @param title
     * @param titleShift 
     */
    public BinarImplementation( String title , int titleShift ){
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
        return 2;
    }

    public int numOutputs() {
        return 1;
    }

    public KObject[] compute(KObject[] objects ) {
        return new KObject[] { compute( objects[0] , objects[1] ) };
    }

    public abstract KObject compute(KObject o1 ,KObject o2);

}
