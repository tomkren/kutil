package kutil.kobjects;

import kutil.core.KAtts;
import kutil.core.Log;
import kutil.items.StringItem;

/**
 *
 * @author Tomáš Křen
 */
public class Field extends Basic {

    private StringItem action;
    private FieldActor actor;

    public Field( KAtts kAtts ){
        super( kAtts );
        action = items().addString(kAtts , "action" , "log" );
        create();
    }
    
    public Field( Field f ) {
        super(f);
        action = items().addString( "action" , f.action.get() );
        create();
    }

    private void create(){
        setType( "field" );
        actor = toActor(action.get());
    }

    @Override
    public KObject copy() {
        return new Field(this);
    }

    public void reactToObjectPresence(KObject o){
        actor.reactToObjectPresence(o);
    }

    public boolean isVisitedBy( KObject o ){
        return getShape().isHit( pos() , o.pos() , getRot() ) ;
    }

    private static FieldActor toActor( String action ){
        if( "log".equals(action) ) return new LogFieldActor();
        if( "ff" .equals(action) ) return new FishFilletsFieldActor();

        return new LogFieldActor();
    }

}

interface FieldActor {
   public void reactToObjectPresence( KObject o );
}

class FishFilletsFieldActor implements FieldActor {

    public void reactToObjectPresence(KObject o) {

       Log.it( "FF : " + o.toXml() );
    }

}

class LogFieldActor implements FieldActor {

    public void reactToObjectPresence(KObject o) {
       Log.it( "Field was visited by : " + o.toXml() );
    }

}
