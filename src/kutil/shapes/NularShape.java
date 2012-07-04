package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar funkce s nula vstupy jedním výstupem.
 * @author Tomáš Křen
 */
public class NularShape extends ImageShape {

    private Function nular;


    public NularShape( Function f ){
        super( Global.shapeFactory().nularImg  ,
               Global.shapeFactory().nularImgSel  ,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,41),
                           new Int2D(0,41)
                         },
              new Int2D(0,0),
              new Int2D(17,20));

        nular = f;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        g.drawString( nular.getImplementation().title() ,
                      drawPos.getX()+3+nular.getImplementation().getTitleShift() ,
                      drawPos.getY()+21 );

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == nular ){
            g.drawLine( drawPos.getX()+16 , drawPos.getY()+41 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( nular.doDrawArrow(0) ){

            Int2D arrowEnd = nular.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+16 , drawPos.getY()+41 , cilX , cilY );
        }

    }

}
