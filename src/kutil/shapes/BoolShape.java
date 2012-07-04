package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Bool;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Bool.
 * @author Tomáš Křen
 */
public class BoolShape extends CircleShape {

    private Bool bool;

    public BoolShape( Bool bool ){
        super( 15 , ( bool.get() ? Color.white : Color.black ) );
        this.bool = bool;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.BOLD , 12 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        
        setColor( bool.get() ? Color.white : Color.black );

        super.draw(g, isSelected, info, pos, center, rot, isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor( ( bool.get() ? Color.black : Color.white ) );

        g.drawString( (bool.get() ? "T" : "F" ) ,
                       drawPos.getX()-3 ,
                       drawPos.getY()+4 );
    }

}

