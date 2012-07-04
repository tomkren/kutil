package kutil.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import kutil.kobjects.In;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu In.
 * @author Tomáš Křen
 */
public class InShape extends ImageShape {

    private In in;

    public InShape( In in ){
        super(Global.shapeFactory().inImg ,
              Global.shapeFactory().inImgSel,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,19),
                           new Int2D(0,19)
                         },
              new Int2D(0,0),
              new Int2D(17,25));

        this.in = in;
    }

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, Int2D pos, Int2D center, double rot,
                     boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == in ){
            g.drawLine( drawPos.getX()+16 , drawPos.getY()+19 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( in.doDrawArrow() ){
            Int2D arrowEnd = in.getTargetArrowEnd().plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+16 , drawPos.getY()+19 , cilX , cilY );
        }
    }



}
