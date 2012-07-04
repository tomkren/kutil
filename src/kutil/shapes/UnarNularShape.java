package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar funkce s jedním vstupem  ažádným výstupem.
 * @author Tomáš Křen
 */
public class UnarNularShape extends ImageShape{

    Function f;

    public UnarNularShape( Function f ){
        super(Global.shapeFactory().unarNularImg ,
              Global.shapeFactory().unarNularImgSel,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,41),
                           new Int2D(0,41)
                         },
              new Int2D(0,0),
              new Int2D(17,25));
        this.f = f;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        g.drawString( f.getImplementation().title() ,
                      drawPos.getX()+3+f.getImplementation().getTitleShift() ,
                      drawPos.getY()+26 );

    }

}
