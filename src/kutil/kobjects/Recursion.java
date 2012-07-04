package kutil.kobjects;

import kutil.items.IntegerItem;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.core.Log;
import kutil.shapes.RecursionShape;

/**
 * Rekurze je speciálním typem funkce, která umožňuje odkazovat se na černou funkci uvnitř jí samotné.
 * @author Tomáš Křen
 */
public class Recursion extends Function {

    private IntegerItem depth;

    public Recursion( KAtts kAtts ){
        super( kAtts );
        depth = items().addInteger(kAtts, "depth", -1);
        create();
    }

    public Recursion( Recursion r ){
        super(r);
        depth = items().addInteger( "depth" , r.depth.get() );
        create();
    }
    @Override
    public KObject copy() {
        return new Recursion(this);
    }


    private void create(){
        setType("recursion");
        setShape( new RecursionShape(this));
        depth.set( depth.get() + 1 );
    }


    @Override
    public void doAfterComputation() {

        this.changeInsideTargets( null );
        this.clearAfterAdding();

    }



    @Override
    public void doBeforeComputation(  ) {

        if( depth.get() > 500 ){
            Log.it("ERROR: Recursion depth is to big!");
            return;
        }

        KObject parentCopy = parent().copy();
        if( parentCopy instanceof Function ){

            KObjectFactory.insertKObjectToSystem( parentCopy , this );
            this.add(parentCopy);
            parentCopy.setPos( new Int2D(200, 200) );

            this.changeInsideTargets( parentCopy.id()+":0" );
            ((Function)parentCopy).changeOutsideTargets( this.id()+":0" );
            ((Function)parentCopy).changeTargets( null );

        }
    }


}
