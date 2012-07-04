package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar funkce s jedním vstupem a dvěma výstupy.
 * @author Tomáš Křen
 */
public class UnarBinarShape extends ImageShape {

    private Function unarBinar;

    public UnarBinarShape( Function ub ){
        super(Global.shapeFactory().unarBinarImg ,
              Global.shapeFactory().unarBinarImgSel,
              new Int2D[]{ new Int2D(16,0),
                           new Int2D(50,0),
                           new Int2D(66,40),
                           new Int2D(66,50),
                           new Int2D(0,50),
                           new Int2D(0,40)
                         },
              new Int2D(0,0),
              new Int2D(33,25));

        unarBinar = ub;
    }

    private static final Font font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);
        g.drawString( unarBinar.getImplementation().title() ,
                      drawPos.getX()+3+unarBinar.getImplementation().getTitleShift() ,
                      drawPos.getY()+28 );

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == unarBinar &&
            Global.rucksack().getFromPort() == 0 ){
            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }
        if( Global.rucksack().getFrom() == unarBinar &&
            Global.rucksack().getFromPort() == 1 ){
            g.drawLine( drawPos.getX()+50 , drawPos.getY()+50 ,
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( unarBinar.doDrawArrow(0) ){

            Int2D arrowEnd = unarBinar.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 , cilX , cilY );
        }

        if( unarBinar.doDrawArrow(1) ){

            Int2D arrowEnd = unarBinar.getTargetArrowEnd(1).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+50 , drawPos.getY()+50 , cilX , cilY );
        }
    }

}
