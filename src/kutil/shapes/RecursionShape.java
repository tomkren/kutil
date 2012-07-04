package kutil.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import kutil.kobjects.Recursion;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Recursion.
 * @author Tomáš Křen
 */
public class RecursionShape extends ImageShape {

    private Recursion recursion;

    public RecursionShape( Recursion r ){
        super( Global.shapeFactory().recursionImg ,
               Global.shapeFactory().recursionImgSel ,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,50),
                           new Int2D(0,50)
                         },
              new Int2D(0,0),
              new Int2D(17,25));

        recursion = r;
    }


    @Override
    public void draw(Graphics2D g, boolean isSelected, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);


        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == recursion ){
            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( recursion.doDrawArrow(0) ){

            Int2D arrowEnd = recursion.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 , cilX , cilY );
        }

    }

}
