package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Speciální FunctionImplementace používaná pro častečnou aplikaci do funkce eval.
 * Nutno ošetřit zvlašť neboť fce eval predstavuje výjimku.
 * (HAX)
 * @author Tomáš Křen
 */
public class UnarEval extends UnarImplementation  {

    KObject arg1;

    public UnarEval( KObject arg1 , String title ){
        super(title, 3);
        this.arg1 = arg1;
    }

    public KObject getArg(){
        return arg1;
    }

    /**
     * Nepoužívá se!
     */
    @Override
    public KObject compute(KObject o) {
        return null;
    }
}
