package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import kutil.kobjects.Comment;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Comment. Vypadá jako inženýr Houbička, kterému jde od pusy bublina s textem.
 * @author Tomáš Křen
 */
public class CommentShape extends ImageShape {

    private Comment comment;

    public CommentShape( Comment comment ) {
        super( Global.shapeFactory().ingImg,
               Global.shapeFactory().ingImgSel,
                new Int2D[]{new Int2D(  30 , 40 ) ,
                            new Int2D( 153,  40 ) ,
                            new Int2D( 153, 220 ) ,
                            new Int2D(  30, 220 ) },
                new Int2D(75,140),
                new Int2D(75,140)
              );
        this.comment = comment;



    }

    private static final Font font  = new Font( Font.MONOSPACED , Font.PLAIN , 12 );
    private static final int  charX =  7;
    private static final int  charY = 15;



    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {
        super.draw(g, isSel, info, pos, center, rot,isRotable);

        if( comment.isSilent() ) return;

        Int2D drawPos = center.plus(pos);

        int x = drawPos.getX()+50;
        int y = drawPos.getY()-40;

        int width  = 13 + charX*comment.longestLine() ;
        int height = 13 + charY*comment.numLines() ;

        g.setColor(Color.white);
        g.fillRoundRect( x + 35 , y - 13+4 , width , height , 10 , 10);
        g.setColor(Color.black);
        g.drawRoundRect( x + 35 , y - 13+4 ,  width , height,  10 , 10);


        int[] trojX = new int[]{x+25-30  ,x+35+5    ,x+36} ;
        int[] trojY = new int[]{y-13-15   ,y-4-5    ,y+5+3 };

        g.setColor(Color.white);
        g.fillPolygon( trojX , trojY , 3 );
        g.setColor(Color.black);
        g.drawLine   ( trojX[0],trojY[0], trojX[1] , trojY[1] );
        g.drawLine   ( trojX[0],trojY[0], trojX[2]-1 , trojY[2] );


        g.setFont( font );
        g.setColor(Color.black);
        for( int i = 0 ; i < comment.numLines() ; i++ ){
            g.drawString( comment.line(i) , x+42 , y+8+i*charY );
        }
        
    }

}
