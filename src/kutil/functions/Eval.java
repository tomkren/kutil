package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Falešná implementace funkce eval,
 * funkce eval tvoří výjimku a její chování je přímo řízeno ve třídě Function.
 * @author Tomáš Křen
 */
public class Eval extends BinarImplementation{
    public Eval(){ super("eval",20); }

    //copute se při eval nepoužívá
    public KObject compute(KObject o1 , KObject o2 ) {
        return null;
    }
}
