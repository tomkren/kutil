package kutil.kobjects;

import kutil.items.IntegerItem;
import kutil.core.KAtts;
import kutil.shapes.NumShape;

/**
 * Objekt virtuálního světa reprezentující číslo.
 * @author Tomáš Křen
 */
public class Num extends Basic {

    private IntegerItem val;

    public Num( KAtts kAtts ){
        super( kAtts );
        val = items().addInteger(kAtts , "val" , 0);
        create();
    }

    public Num( int i ){
        super();
        val = items().addInteger( "val" , i );
        create();
    }

    public Num( Num n ){
        super(n);
        val = items().addInteger( "val" , n.val.get() );
        create();
    }

    @Override
    public String toKisp() {
        return val.get().toString();
    }

    @Override
    public KObject copy() {
        return new Num( this );
    }

    private void create(){
        setType( "num" );
        setPhysical(true);
        setAttached(false);
        setShape( new NumShape(this) );
    }
    

    public int get(){
        return val.get();
    }
    public void set( int v ){
        val.set( v );
    }




}
