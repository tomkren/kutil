package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.core.Global;
import kutil.core.Int2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.DynamicShape;

/**
 * Tvar pro vytváření tvaru konvexního polygonu.
 * @author Tomáš Křen
 */
public class ConvexPolygonShape implements KShape{

    private   Color         color;
    private   ConvexPolygon convexPolygon;
    protected Int2D         posPoint;
    protected Int2D         t;


    private static final Color highlightColor = Color.red;
    private static final Font  idFont = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );


    public ConvexPolygonShape( Int2D[] vs , Int2D posPoint , Int2D t , Color c ){
        color = c;

        //přepočítáme souřadnice tak aby těžiště bylo na souřadnici [0,0]

        posPoint = posPoint.minus(t);

        for( int i=0 ; i<vs.length ; i++ ){
            vs[i] = vs[i].minus(t) ;
        }

        this.t = t;

        convexPolygon = new ConvexPolygon( Int2D.toROVector2f(vs) );
        this.posPoint = posPoint;
    }

    public void draw(Graphics2D g, boolean isSel, String info , 
                     Int2D pos , Int2D center , double rot,boolean isRotable){

        Vector2f[] verts = convexPolygon.getVertices( getPhys2dCenter(pos) , (float) rot );
        Int2D drawPos = center.plus(pos);

        int numVertices = convexPolygon.NumVertices();

        int[] polyXs = new int[numVertices];
        int[] polyYs = new int[numVertices];

        int dx = center.getX();
        int dy = center.getY();

        for ( int i = 0 ; i < verts.length; i++ ) {
            polyXs[i] = (int) (0.5f + verts[i].getX()) + dx;
            polyYs[i] = (int) (0.5f + verts[i].getY()) + dy;
        }

        g.setColor( color );
        g.fillPolygon( polyXs , polyYs , numVertices );

        if( isSel ) {
            g.setColor( highlightColor );

            for ( int i = 0, j = verts.length-1; i < verts.length; j = i, i++ ) {
                g.drawLine(     (int) (0.5f + verts[i].getX()) + dx,
                                (int) (0.5f + verts[i].getY()) + dy,
                                (int) (0.5f + verts[j].getX()) + dx,
                                (int) (0.5f + verts[j].getY()) + dy );
            }
        }

        if( Global.rucksack().showInfo() ){
            g.setFont(idFont);
            g.drawString( info , drawPos.getX() , drawPos.getY()-3 );
        }

    }
    public boolean isHit( Int2D pos , Int2D clickPos , double rot ){

        Vector2f[] verts = convexPolygon.getVertices( getPhys2dCenter(pos) , (float) rot );

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
    }
    public DynamicShape getPhys2dShape(){
        return convexPolygon;
    }
    public ROVector2f getPhys2dCenter( Int2D pos ){
        return Int2D.toROVector2f( pos.minus(posPoint) );
    }
    public Int2D getPosByPhys2dCenter( ROVector2f v ){
        return Int2D.fromROVector2f(v) .plus( posPoint );
    }


}
