package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar funkce s třemi vstupy a jedním výstupem.
 * @author Tomáš Křen
 */
public class TernarShape extends ImageShape {

    private Function ternar;

    public TernarShape( Function t ){
        super(Global.shapeFactory().ternarImg ,
              Global.shapeFactory().ternarImgSel ,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(100,0),
                           new Int2D(100,9),
                           new Int2D(68,50),
                           new Int2D(33,50),
                           new Int2D(0,9)
                         },
              new Int2D(0,0),
              new Int2D(50,25));

        ternar = t;
    }

    private static final Font font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        g.drawString( ternar.getImplementation().title() ,
                      drawPos.getX()+3+ternar.getImplementation().getTitleShift() ,
                      drawPos.getY()+28 );

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == ternar ){
            g.drawLine( drawPos.getX()+50 , drawPos.getY()+50 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( ternar.doDrawArrow(0) ){

            Int2D arrowEnd = ternar.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+50 , drawPos.getY()+50 , cilX , cilY );
        }

    }

}
