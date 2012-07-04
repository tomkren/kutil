package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar pro binární funkce.
 * @author Tomáš Křen
 */
public class BinarShape extends ImageShape {

    private Function binar;

    public BinarShape( Function b ){
        super(Global.shapeFactory().binarImg ,
              Global.shapeFactory().binarImgSel,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(66,0),
                           new Int2D(66,9),
                           new Int2D(50,50),
                           new Int2D(16,50),
                           new Int2D(0,9)
                         },
              new Int2D(0,0),
              new Int2D(33,25));

        binar = b;
    }

    private static final Font font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        g.drawString( binar.getImplementation().title() ,
                      drawPos.getX()+3+binar.getImplementation().getTitleShift() ,
                      drawPos.getY()+28 );

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == binar ){
            g.drawLine( drawPos.getX()+33 , drawPos.getY()+50 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( binar.doDrawArrow(0) ){

            Int2D arrowEnd = binar.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+33 , drawPos.getY()+50 , cilX , cilY );
        }

    }

}
