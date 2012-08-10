package kutil.shapes;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import kutil.core.Int2D;
import kutil.kobjects.Basic;
import kutil.kobjects.KObject;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.shapes.DynamicShape;

/**
 *
 * @author Tomáš Křen
 */
public class MultiShape implements KShape {
   
    private LinkedList<Basic> obs;
    private Basic             pivot;
    
    public MultiShape( List<KObject> os ){
        
        obs = new LinkedList<Basic>();
        
        for( KObject o : os ){
            if( o instanceof Basic){
                Basic b = (Basic) o;
                obs.add( b );
            }
        }
        
        if( ! obs.isEmpty() ){
            pivot = obs.getFirst();
        }
    }
    
    public KObject getPivot(){
        return pivot;
    }
    
    public void draw(Graphics2D g, boolean isSel, String info, Int2D pos, Int2D c, double rot, boolean isRotable) {
        for( Basic o : obs ){
            o.getShape().draw( g , isSel , o.getInfoString() , pos.plus(o.pos()) , c , o.getRot() , o.getIsRotable() );
        }
    }

    public ROVector2f getPhys2dCenter(Int2D pos) {    
        return pivot.getShape().getPhys2dCenter(pos);
    }

    public DynamicShape getPhys2dShape() {
        return pivot.getShape().getPhys2dShape();
    }

    public Int2D getPosByPhys2dCenter(ROVector2f v) {
        return pivot.getShape().getPosByPhys2dCenter(v);
    }

    public boolean isHit(Int2D pos, Int2D clickPos, double rot) {
        for( Basic o : obs ){
            if( o.getShape().isHit(pos.plus(o.pos()), clickPos, rot)) {
                return true;
            }
        }
        return false;
    }

    
}
