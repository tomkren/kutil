package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Budha;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Budha.
 * @author Tomáš Křen
 */
public class BudhaShape extends ImageShape {

    private Budha budha;

    public BudhaShape( Budha b ) {
        super( Global.shapeFactory().budhaImg ,
               Global.shapeFactory().budhaImgSel ,
                    new Int2D[]{new Int2D( 35, 40 ),
                                new Int2D( 55, 40 ),
                                new Int2D( 80, 80 ),
                                new Int2D( 75,105 ),
                                new Int2D( 25,105 ),
                                new Int2D( 15, 80 ) },
                    new Int2D(50,75),
                    new Int2D(50,100)
             );

        budha = b;
    }

    private static final Font font = new Font( Font.SERIF , Font.PLAIN , 10 );

    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSel, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        String str = budha.toKisp();
        g.drawString( str , drawPos.getX()-1-(2*str.length()) , drawPos.getY()-45 );
    }



}
