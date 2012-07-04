package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Fly;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Wasp.
 * @author Tomáš Křen
 */
public class WaspShape extends AnimationShape {

    private Fly fly;

    public WaspShape( Fly fly ) {
        super( Global.shapeFactory().waspImgs,
               Global.shapeFactory().waspImgSel,
                new Int2D[]{new Int2D( 28 ,  0 ) ,
                            new Int2D( 44 , 14 ) ,
                            new Int2D( 28 , 28 ) ,
                            new Int2D( 14 , 14 ) },
                new Int2D(28,14),
                new Int2D(28,14)
              );
        this.fly = fly;
    }

    private static final int  crossSize = 4;
    private static final Font font = new Font( Font.SERIF , Font.PLAIN , 10 );


    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {


        Int2D crossPos = center.plus( fly.goalPos() );
        int x = crossPos.getX();
        int y = crossPos.getY();

        g.setColor(Color.black);

        g.drawLine( x - crossSize , y - crossSize ,
                    x + crossSize , y + crossSize);

        g.drawLine( x - crossSize , y + crossSize ,
                    x + crossSize , y - crossSize );

        super.draw(g, isSel, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        String str = fly.memString();
        g.drawString( str , Math.round( drawPos.getX()+4-(1.7*str.length())) , drawPos.getY()-25 );
    }



}
