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
    
    public void informFieldAboutDeletation( KObject deleted ){
        actor.informFieldAboutDeletation(deleted);
    }

    public boolean isVisitedBy( KObject o ){
        
        boolean ret = isHit( o.pos().plus(new Int2D(1, 1)) ); 
                
        //if( ret )Log.it("ve fíldu: "+o.toXml() );
        
        return ret;
        
        //return getShape().isHit( pos() , , getRot() ) ; //hodne divnej bug: o.pos().plus(new Int2D(5, 5)) 
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
   public void informFieldAboutDeletation( KObject deleted );
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
    
    public void informFieldAboutDeletation( KObject deleted ){
        
        if( ! inField.contains(deleted) ) {
            //Log.it("neni ve fíldu :"+field.id());
            return;
        }
        
        //Log.it("lalalalala");
        
        ffUnit.removeKObject( deleted );
        if( wasPhysical.get(deleted.id()) ){
            deleted.setPhysicalOn();
        }
        wasPhysical.remove(deleted.id());
        inField.remove(deleted);
        
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
                                
                o.setPos( o.pos().align(FFUnit.SIDE) );
                ((Basic)o).setSpeed(Int2D.zero);                
           }

           ffUnit.addKObject(o);
       }
    }
    

    
}

class LogActor implements FieldActor {

    public void informFieldAboutDeletation( KObject deleted ){}
    
    public void reactToObjectPresence(KObject o) {
       Log.it( "Field was visited by : " + o.toXml() );
    }

    public void init() { }
    public void step() { }
    

}
