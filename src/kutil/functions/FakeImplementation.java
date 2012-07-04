package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Implementace sloužící pouze jako nositel hodnoty v podobě KObjectu.
 * Hodí se nám aby výsledek každého Kispového převodu (tzn. převodu textové verze
 * Kispu na interní) byla Implemetace, tuto třídu použijeme pokud je výsledkem hodnota.
 * @author Tomáš Křen
 */
public class FakeImplementation implements FunctionImplemetation{

    
    KObject o;

    public FakeImplementation( KObject o ){
        this.o = o;
    }
    /**
     * Dává k dispozici KObjekt, pro který je tato FunctionImplementation
     * nositel.
     * @return
     */
    public KObject get(){
        return o;
    }
    
    /**
     * Nepoužívá se!
     */
    public KObject[] compute( KObject[] objects ){
        return null;
    }
    /**
     * Nepoužívá se!
     */
    public int       numArgs(){
        return 0;
    }
    /**
     * Nepoužívá se!
     */
    public int numOutputs() {
        return 0;
    }
    /**
     * Nepoužívá se!
     */
    public String    title(){
        return null;
    }
    /**
     * Nepoužívá se!
     */
    public int getTitleShift(){
        return 0;
    }

}
