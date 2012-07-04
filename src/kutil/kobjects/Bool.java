package kutil.kobjects;

import kutil.items.BooleanItem;
import kutil.core.KAtts;
import kutil.shapes.BoolShape;

/**
 * Objekt virtuálního světa reprezentující boolovskou hodnotu.
 * @author Tomáš Křen
 */
public class Bool extends Basic {

    private BooleanItem val;

    /**
     * Vytvoří Bool podle kAtts.
     * Přidává položku val, reprezentující boolovskou hodnotu.
     */
    public Bool( KAtts kAtts ){
        super( kAtts );
        val = items().addBoolean(kAtts , "val" , true);
        create();
    }

    /**
     * Konstruktor převádějící boolean na Bool.
     */
    public Bool( boolean b ){
        super();
        val = items().addBoolean( "val" , b , true );
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Bool( Bool b ){
        super(b);
        val = items().addBoolean( "val" , b.val.get() , true );
        create();
    }

    @Override
    public String toKisp() {
        return "[" + Boolean.toString( val.get() )+ "]" ;
    }

    @Override
    public KObject copy() {
        return new Bool( this );
    }

    private void create(){
        setType( "bool" );
        setPhysical(true);
        setAttached(false);
        setShape( new BoolShape(this) );
    }

    /**
     * Změní hodnotu tohoto objektu na jeho negaci.
     */
    public void negate(){
        val.set( ! val.get() );
    }

    /**
     * Vrací hodnotu jako boolean.
     */
    public boolean get(){
        return val.get();
    }

    /**
     * Změní hodnotu na tu zadanou jako boolean.
     */
    public void set( boolean v ){
        val.set( v );
    }




}
