package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Box;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Box.
 * @author Tomáš Křen
 */
public class BoxShape extends ImageShape {


    private Box box;

    public BoxShape( Box box ) {
        super( Global.shapeFactory().boxImg,
               Global.shapeFactory().boxImgSel,
                new Int2D[]{new Int2D( 10 , 10 ) ,
                            new Int2D( 50 , 10 ) ,
                            new Int2D( 50 , 45 ) ,
                            new Int2D( 10 , 45 ) },
                new Int2D(30,25),
                new Int2D(30,40)
              );
        this.box = box;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.BOLD , 12 );

    @Override
    public void draw(Graphics2D g, boolean isSel, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSel, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        String str = box.toKisp();
        g.drawString( str , drawPos.getX()+7-(3*str.length()) , drawPos.getY()-30 );
    }


}
