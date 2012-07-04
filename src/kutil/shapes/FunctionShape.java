package kutil.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Int2D;

/**
 * Defaultní tvar funkce, pokud její implementace nezapadá do standardních typů
 * (tzn má moc vstupů/výstupů atd.)
 * @author Tomáš Křen
 */
public class FunctionShape extends RectangleShape {

    private Function function;

    public FunctionShape( Function f ){
        super( new Int2D(32,32), Color.gray );
        function = f;
    }

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        for( int i=0 ; i<function.getNumTargets() ; i++ ){
            if( function.doDrawArrow(i) ){               

                Int2D arrowEnd = function.getTargetArrowEnd(i).plus(center);

                int cilX = arrowEnd.getX() ;
                int cilY = arrowEnd.getY() ;

                if( isSelected ){
                    g.setColor(Color.red);
                } else{
                    g.setColor(Color.gray );
                }

                g.drawLine( drawPos.getX()+16 , drawPos.getY()+32 , cilX , cilY );

                g.drawLine( cilX , cilY , cilX-8 , cilY-8 );
                g.drawLine( cilX , cilY , cilX+8 , cilY-8 );
            }
        }
    }

}
