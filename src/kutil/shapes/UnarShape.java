package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Function;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar funkce s jením vstupem.
 * @author Tomáš Křen
 */
public class UnarShape extends ImageShape {

    private Function unar;
    private boolean  isBlack;
    
    public UnarShape( Function u , boolean isBlack ){
        super( (isBlack ? Global.shapeFactory().unarBlackImg    : Global.shapeFactory().unarImg ) ,
               (isBlack ? Global.shapeFactory().unarBlackImgSel : Global.shapeFactory().unarImgSel ) ,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,50),
                           new Int2D(0,50)
                         },
              new Int2D(0,0),
              new Int2D(17,25));
        
        unar = u;
        this.isBlack = isBlack;
    }

    private static final Font  font = new Font( Font.SANS_SERIF , Font.PLAIN , 9 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info, 
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        if( isBlack ){
            g.setFont( font );
            g.setColor(Color.white);
            g.drawString( unar.id() ,
                          drawPos.getX()+13 - unar.id().length()*2 ,
                          drawPos.getY()+28 );
        } else {
            g.setFont( font );
            g.setColor(Color.black);
            g.drawString( unar.getImplementation().title() ,
                          drawPos.getX()+3+unar.getImplementation().getTitleShift() ,
                          drawPos.getY()+28 );
        }

        if( isSelected ){
            g.setColor(Color.red);
        } else{
            g.setColor(Color.gray );
        }

        if( Global.rucksack().getFrom() == unar ){
            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 , 
                        Global.rucksack().getMouseX() , Global.rucksack().getMouseY() );
        }

        if( unar.doDrawArrow(0) ){

            Int2D arrowEnd = unar.getTargetArrowEnd(0).plus(center);

            int cilX = arrowEnd.getX() ;
            int cilY = arrowEnd.getY() ;

            g.drawLine( drawPos.getX()+16 , drawPos.getY()+50 , cilX , cilY );
        }

    }

}
