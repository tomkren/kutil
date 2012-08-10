package kutil.kobjects;

import java.util.List;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.items.ListItem;
import kutil.shapes.MultiShape;

/**
 *
 * @author Tomáš Křen
 */
public class MultiObject extends Basic {
    
    private ListItem obs;
    
    public MultiObject( KAtts kAtts ){
        super( kAtts );
        obs  = items().addList(kAtts  , "obs" );
        create();
    }
    
    public MultiObject( MultiObject mo ) {
        super(mo);
        
        obs  = items().addEmptyList( "obs" ) ;

        for( KObject o :  mo.obs.get() ){
            KObject copy = o.copy();
            obs.get().add( copy );
        }
        
        create();
    }
    
    public MultiObject( List<KObject> bricks ){
        super();
        obs  = items().addEmptyList( "obs" ) ;

        for( KObject o :  bricks ){
            obs.get().add( o );
        }
        
        create();
        
        Int2D pos = ((MultiShape) getShape()).getPivot().pos(); 
        setPos( pos );
        
        for( KObject o : obs.get() ){
            o.setPos( o.pos().minus(pos) );
        }
    }

    private void create(){
        setType( "multiobject" );
        MultiShape shape = new MultiShape(obs.get()); 
        setShape( shape );
        
        
    }
    
    @Override
    public KObject copy() {
        return new MultiObject(this);
    }

}
