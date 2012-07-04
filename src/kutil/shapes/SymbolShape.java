package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Symbol;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Symbol.
 * @author Tomáš Křen
 */
public class SymbolShape extends RectangleShape {

    private Symbol symbol;

    public SymbolShape( Symbol s ){
        super( Int2D.zero , Color.white );
        symbol = s;
        setSize( new Int2D( symbol.getWidth() , 32) );
    }

    private static final Font  font = new Font( Font.MONOSPACED , Font.BOLD , 12 );

    @Override
    public void draw(Graphics2D g, boolean isSelected, String info,
            Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSelected, info, pos, center, rot,isRotable);

        Int2D drawPos = center.plus(pos);

        g.setFont( font );
        g.setColor(Color.black);

        g.drawString( symbol.get() ,
                       drawPos.getX()+8 ,
                       drawPos.getY()+19 );
    }

}