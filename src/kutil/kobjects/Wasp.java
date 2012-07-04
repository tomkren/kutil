package kutil.kobjects;

import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.shapes.WaspShape;

/**
 * Objekt virtuálního světa reprezentující vosu - speciální typ agenta sloužící
 * k redukci populace much.
 * @author Tomáš Křen
 */
public class Wasp extends Fly {


    public Wasp( KAtts kAtts ){
        super( kAtts );
        create();
    }

    public Wasp(  ){
        super( );
        create();
    }

    public Wasp( Wasp w ){
        super(w);
        create();
    }

    @Override
    public KObject copy() {
        return new Wasp( this );
    }

    private void create(){
        setType( "wasp" );
        setShape( new WaspShape(this) );
        setSpeed(30);
    }

    @Override
    protected void handleCollision(Int2D hitPos, KObject hitObject) {
        super.handleCollision(hitPos, hitObject);

        if( hitObject instanceof Fly && !( hitObject instanceof Wasp ) ){
            hitObject.delete();
        }
        
    }


    


}
