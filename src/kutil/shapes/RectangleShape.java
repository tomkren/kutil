package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.core.Global;
import kutil.core.Int2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.DynamicShape;

/**
 * Tvar obdélníkového KObjektu.
 * @author Tomáš Křen
 */
public class RectangleShape implements KShape {

    private Int2D       size;
    private Color       color;
    private Box         phys2DBox;

    private static final Color highlightColor = Color.red;
    private static final Font  idFont = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );

    public RectangleShape( Int2D size , Color color ) {
        this.size     = size;
        this.color    = color;
        phys2DBox     = new Box( size.getX() , size.getY() );
    }

    public void setSize( Int2D s ){
        size      = s;
        phys2DBox = new Box( size.getX() , size.getY() );
    }

    public void draw(Graphics2D g,
                     boolean isSelected,
                     String info ,
                     Int2D pos ,
                     Int2D center ,
                     double rot,
                     boolean isRotable){

        Vector2f[] vs = phys2DBox.getPoints( getPhys2dCenter(pos) , (float) rot );

        Int2D drawPos = center.plus(pos);

        int dx = center.getX();
        int dy = center.getY();

        int[] xs = {(int)vs[0].x+dx,(int)vs[1].x+dx,(int)vs[2].x+dx,(int)vs[3].x+dx};
        int[] ys = {(int)vs[0].y+dy,(int)vs[1].y+dy,(int)vs[2].y+dy,(int)vs[3].y+dy};

        g.setColor( color );
        g.fillPolygon( xs , ys , 4 );

        if( color == Color.white ){
            g.setColor(Color.black);
            g.drawPolygon( xs , ys , 4 );
        }

        if( isSelected ){
            g.setColor( highlightColor );
            //g.setStroke(BodyDrawer.oznacStroke);
            g.drawPolygon( xs , ys , 4 );
        }

        if( Global.rucksack().showInfo() ){
            g.setFont(idFont);
            g.drawString( info , drawPos.getX() , drawPos.getY()-3 );
        }

    }

    public boolean isHit( Int2D pos , Int2D clickPos , double rot ){

        Vector2f[] verts = phys2DBox.getPoints( getPhys2dCenter(pos) , (float) rot );

        Vector2f p = Int2D.toROVector2f( clickPos );

        // p is in the polygon if it is left of all the edges
        int l = verts.length;
        for ( int i = 0; i < verts.length; i++ ) {
                Vector2f x = verts[i];
                Vector2f y = verts[(i+1)%l];
                Vector2f z = p;

                // does the 3d cross product point up or down?
                if ( (z.x-x.x)*(y.y-x.y)-(y.x-x.x)*(z.y-x.y) >= 0 )
                        return false;
        }

        return true;

        //return Int2D.rectangeHit(clickPos, pos , size.getX() , size.getY() ) ;
    }

    public DynamicShape getPhys2dShape(){
        return phys2DBox;
    }

    public ROVector2f getPhys2dCenter( Int2D pos ){
        return new Vector2f( pos.getX() + size.getX()/2f , pos.getY() + size.getY()/2f );
    }

    public Int2D getPosByPhys2dCenter( ROVector2f v ){
        return new Int2D( (int)(v.getX() - size.getX()/2f) , (int)(v.getY() - size.getY()/2f)  );
    }
    

}
