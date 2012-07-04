package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Num;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Num.
 * @author Tomáš Křen
 */
public class NumShape extends CircleShape { 

    private Num num;

    public NumShape( Num n ){
        super( 15 , Color.yellow );
        num = n;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.BOLD , 12 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info,
            Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);

        g.drawString( ((Integer)num.get()).toString() , 
                       drawPos.getX()-3-( num.get() > 9 ? 4 : 0 )-( num.get() > 99 ? 3 : 0 ) ,
                       drawPos.getY()+4 );
    }

}
