package kutil.kobjects;

import kutil.items.StringItem;
import kutil.core.KAtts;
import kutil.shapes.DirectionShape;

/**
 * Objekt virtuálního světa reprezentující směr.
 * Tyto objekty využívá hlavně moucha uvnitř běhu svého programu.
 * @author Tomáš Křen
 */
public class Direction extends Basic {

    private StringItem val;
    private DirectionShape shape;

    /**
     * Pět možných hodnot směru.
     * Nahoru, dolu, doleva, doprava a náhodný směr.
     */
    public enum Vals { up , down , left , right , randdir }

    /**
     * Vytvoří Direction podle kAtts.
     * Přidává položku val, reprezentující hodnotu směru.
     */
    public Direction( KAtts kAtts ){
        super( kAtts );
        val = items().addString(kAtts , "val" , Vals.up.toString() );
        create();
    }

    /**
     * Vytváří směr na základě hodnoty směru.
     */
    public Direction( Vals v ){
        super();
        val = items().addString( "val" , v.toString() );
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Direction( Direction d ){
        super(d);
        val = items().addString( "val" , d.val.get() );
        create();
    }

    @Override
    public String toKisp() {
        return "[" + val.get() + "]" ;
    }

    @Override
    public KObject copy() {
        return new Direction( this );
    }

    private void create(){
        setType( "direction" );
        setPhysical(true);
        setAttached(false);
        shape = new DirectionShape(this);
        setShape( shape );
    }

    /**
     * Vrací hodnotu směru.
     */
    public Vals get(){
        return Vals.valueOf( val.get() );
    }
    
    
    /**
     * Nastavuje hodnotu směru.
     */
    public void set( Vals v ){
        val.set( v.toString() );
        shape.resetImage();
    }

    /**
     * Změní hodnotu směru tak, že ho zrotuje po směru hodinových ručiček.
     */
    public void rotateCW(){
        switch( Vals.valueOf( val.get() ) ){
            case up    : val.set( Vals.right.toString() ); break;
            case down  : val.set( Vals.left .toString() ); break;
            case left  : val.set( Vals.up   .toString() ); break;
            case right : val.set( Vals.down .toString() ); break;
        }
    }




}
