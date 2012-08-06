package kutil.kobjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kutil.core.Global;
import kutil.core.Int2D;
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
    public void step() {
        super.step();
        actor.step();
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

    private FieldActor toActor( String action ){
        
        String[] parts = action.split( "\\s+" , 2 );
        
        String actionName   = parts[0];
        String actionParams = null;
        if( parts.length == 2 ){
            actionParams = parts[1];
        }
        
        
        if( "log".equals(actionName) ) return new LogActor();
        if( "ff" .equals(actionName) ) return new FishFilletsActor( actionParams , this );

        return new LogActor();
    }

}
interface FieldActor {
   public void reactToObjectPresence( KObject o );
   public void init();
   public void step();
}



class FishFilletsActor implements FieldActor {

    private String ffUnitId;
    private FFUnit ffUnit;
    private Field  field;

    private Set<KObject> inField;
    private Map<String,Boolean> wasPhysical;
    
    public FishFilletsActor( String params , Field f){
        if( params == null ) return;
        
        ffUnitId = params;
        field    = f;

        inField = new HashSet<KObject>();
        wasPhysical = new HashMap<String, Boolean>();

        // Log.it(params);
    }
    
    public void init() {
        ffUnit = (FFUnit) Global.idDB().get(ffUnitId);
    }
    
    public void step(){
        
        List<KObject> toRemove = new LinkedList<KObject>();
        
        for( KObject o : inField ){
            if( ! field.isVisitedBy(o) ){
                ffUnit.removeKObject(o);
                toRemove.add(o);
                if( wasPhysical.get(o.id()) ){
                    o.setPhysicalOn();
                }
                wasPhysical.remove(o.id());
            }
        }
        
        inField.removeAll(toRemove);
    }

    public void reactToObjectPresence(KObject o) {
       if( ffUnit != null && ! inField.contains(o) ){

           boolean wasPhys = o.isPhysical();

           inField.add(o);
           wasPhysical.put(o.id(), wasPhys);

           if( wasPhys ){
                o.setPhysicalOff();
           }

           ffUnit.addKObject(o);
       }
    }
    
    private static void alignToGrid(KObject o){
        
        Int2D pos = o.pos();
        //o.setPos(  );
    }
    
}

class LogActor implements FieldActor {

    public void reactToObjectPresence(KObject o) {
       Log.it( "Field was visited by : " + o.toXml() );
    }

    public void init() { }
    public void step() { }
    

}
