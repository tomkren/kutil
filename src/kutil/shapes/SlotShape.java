package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Slot;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Slot.
 * @author Tomáš Křen
 */
public class SlotShape extends ImageShape{

        private Slot slot;

    public SlotShape( Slot slot ) {
        super( Global.shapeFactory().slotImg,
               Global.shapeFactory().slotImgSel,
                new Int2D[]{new Int2D(  0 , 0 ) ,
                            new Int2D( 32 , 0 ) ,
                            new Int2D( 32 , 32 ) ,
                            new Int2D(  0 , 32 ) },
                new Int2D(16,16),
                new Int2D(16,16)
              );
        this.slot = slot;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );

    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSel, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        
        if( ! slot.inside().isEmpty() ){
            String kisp = slot.inside().getFirst().toKisp();
            g.drawString( kisp , drawPos.getX()+0-(2*kisp.length()) , drawPos.getY()-22 );
        }
        String id   = slot.id();
        g.drawString( id , drawPos.getX()-2-(2*id.length()) , drawPos.getY()+26 );

    }

}
