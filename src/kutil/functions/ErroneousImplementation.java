package kutil.functions;

import kutil.kobjects.KObject;

/**
 * FunctionImplementace vracená pokud došlo při překladu z Kispové reprezentace na
 * FunctionImplementation k chybě.
 * @author Tomáš Křen
 */
public class ErroneousImplementation implements FunctionImplemetation{

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
