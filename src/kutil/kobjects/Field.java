package kutil.kobjects;

import java.util.HashSet;
import java.util.Set;
import kutil.core.Global;
import kutil.core.IdDB;
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
    public void init() {
        super.init();
        actor.init();
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
        
        String[] parts = action.split( "\\s+" , 2 );
        
        String actionName   = parts[0];
        String actionParams = null;
        if( parts.length == 2 ){
            actionParams = parts[1];
        }
        
        
        if( "log".equals(actionName) ) return new LogActor();
        if( "ff" .equals(actionName) ) return new FishFilletsActor( actionParams );

        return new LogActor();
    }

}
interface FieldActor {
   public void reactToObjectPresence( KObject o );
   public void init();
}



class FishFilletsActor implements FieldActor {

    private String ffUnitId;
    private FFUnit ffUnit;
    
    private Set<KObject> inField;
    
    public FishFilletsActor( String params ){
        if( params == null ) return;
        
        ffUnitId = params;
        
        inField = new HashSet<KObject>();
        
        // Log.it(params);
    }
    
    public void init() {
        ffUnit = (FFUnit) Global.idDB().get(ffUnitId);
    }
    
    public void reactToObjectPresence(KObject o) {
       if( ffUnit != null && ! inField.contains(o) ){
           inField.add(o);
           act( o );
       }
    }
    
    private void act(KObject o){
        o.setPhysicalOff();
        ffUnit.addKObject(o);
        
    }
}

class LogActor implements FieldActor {

    public void reactToObjectPresence(KObject o) {
       Log.it( "Field was visited by : " + o.toXml() );
    }

    public void init() { }
    
    

}
