package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Falešná implementace funkce call,
 * funkce call tvoří výjimku a její chování je přímo řízeno ve třídě Function.
 * @author Tomáš Křen
 */
public class Call extends BinarImplementation{
    public Call(){ super("call",20); }

    //copute se při call nepoužívá
    public KObject compute(KObject o1 , KObject o2 ) {
        return null;
    }
}