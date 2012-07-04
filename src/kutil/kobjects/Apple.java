package kutil.kobjects;

import kutil.core.KAtts;
import kutil.shapes.AppleShape;

/**
 * Objekt jablíčko, pokud ho "sní" moucha, rozmoží se.
 * @author Tomáš Křen
 */
public class Apple extends Basic {

    /**
     * Vytvoří Apple podle kAtts.
     */
    public Apple( KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Apple( Apple a ){
        super(a);
        create();
    }

    @Override
    public KObject copy() {
        return new Apple(this);
    }

    private void create(){
        setType("apple");
        setPhysical(true);
        setAttached(true);
        //setIsAffectedByGravity(false);
        setShape( new AppleShape() );
    }


}
