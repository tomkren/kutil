package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.core.Global;
import kutil.core.Int2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.DynamicShape;

/**
 * Kulatý tvar.
 * @author Tomáš Křen
 */
public class CircleShape implements KShape {

    private int     r;
    private Color   color;
    private Circle  phys2DCircle;

    private static final Font  idFont = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );

    public CircleShape( int r , Color color ) {
        this.r       = r;
        this.color   = color;
        phys2DCircle = new Circle(r);
    }

    public void setColor( Color c ){
        color = c;
    }

    public void draw(Graphics2D g,
                     boolean isSelected,
                     String info ,
                     Int2D pos ,
                     Int2D center ,
                     double rot,
                     boolean isRotable){

        Int2D drawPos = center.plus(pos);

        float xo = (float) (Math.cos(rot) * r);
        float yo = (float) (Math.sin(rot) * r);

        g.setColor( color );
        g.fillOval((int) (drawPos.getX()-r),(int) (drawPos.getY()-r),(int) (r*2),(int) (r*2));

        if( color == Color.white ){
            g.setColor( Color.black );
            g.drawOval((int) (drawPos.getX()-r),  (int) (drawPos.getY()-r),(int) (r*2),(int) (r*2));
        }

        if( isSelected ){
            g.setColor( Color.red );
            g.drawOval((int) (drawPos.getX()-r),  (int) (drawPos.getY()-r),(int) (r*2),(int) (r*2));
            g.drawLine((int)  drawPos.getX()    , (int)  drawPos.getY()      ,
                       (int) (drawPos.getX()+xo), (int) (drawPos.getY()+yo) );
        }

        if( Global.rucksack().showInfo() ){
            g.setFont(idFont);
            g.drawString( info , drawPos.getX()-r , drawPos.getY()-r );
        }
    }

    public boolean isHit( Int2D pos , Int2D clickPos , double rot ){

        int dx = pos.getX() - clickPos.getX();
        int dy = pos.getY() - clickPos.getY();

        return dx*dx + dy*dy < r*r;
    }

    public DynamicShape getPhys2dShape(){
        return phys2DCircle;
    }

    public ROVector2f getPhys2dCenter( Int2D pos ){
        return new Vector2f( pos.getX() , pos.getY() );
    }

    public Int2D getPosByPhys2dCenter( ROVector2f v ){
        return new Int2D( (int)v.getX() , (int)v.getY() );
    }


}
