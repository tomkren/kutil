package kutil.kobjects;

import kutil.core.KAtts;
import kutil.shapes.BoxShape;

/**
 * Objekt virtuálního světa reprezentující seznam.
 * @author Tomáš Křen
 */
public class Box extends Basic {

    /**
     * Vytvoří Box podle kAtts.
     */
    public Box( KAtts kAtts ){
        super( kAtts );
        create();
    }

    /**
     * Vytvoří Box pro prázný seznam.
     */
    public Box(){
        super();
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Box( Box l ){
        super(l);
        create();
    }


    @Override
    public KObject copy() {
        return new Box( this );
    }

    private void create(){
        setType( "box" );
        setPhysical(true);
        setAttached(false);
        setShape( new BoxShape(this) );

        setStepInside(false);
    }

    /**
     * Vrací zda se jedná o prázdný seznam.
     */
    public boolean isEmpty(){
        return inside().isEmpty();
    }



    



}
