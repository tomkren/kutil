package kutil.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar zadaný kružnicí a obrázkem.
 * @author sekol
 */
public class ImageCircleShape extends CircleShape {

    private BufferedImage image , imageSelected;

    private int stredX, stredY;

    private static final Font  idFont = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );

    private Int2D posPoint;

    public ImageCircleShape( BufferedImage image, BufferedImage imageSelected,
                       int r , Int2D posPoint , Int2D t ) {

        //super(vs , posPoint , t , Color.CYAN );
        super( r, Color.red );

        this.posPoint = posPoint.minus(t);

        this.image          = image;
        this.imageSelected  = imageSelected;

        stredX = t.getX();
        stredY = t.getY();

    }

    public void resetImage( BufferedImage img , BufferedImage imgSel ){
        image         = img;
        imageSelected = imgSel;
    }

    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {

        BufferedImage img = ( isSel ? imageSelected : image );

        Int2D drawPos = center.plus(pos);

        Int2D tPos = drawPos.minus(posPoint);


        if( isRotable ){

            g.translate( tPos.getX() , tPos.getY() );
            g.rotate( rot );
            g.translate( -stredX , -stredY );

    //        BufferedImage i = new BufferedImage(img.getWidth()*1, img.getHeight()*1, BufferedImage.TYPE_INT_ARGB );
    //        Graphics2D gg = ((Graphics2D)i.getGraphics());
    //        gg.rotate( rot ,stredX , stredY);
    //        gg.drawImage(img, 0, 0, null );
    //        gg.rotate( -rot ,stredX , stredY );

            g.drawImage( img , 0 , 0 , null);


            g.translate( stredX , stredY );
            g.rotate( -rot );
            g.translate( -tPos.getX() , -tPos.getY() );
        }
        else{
            g.drawImage( img , tPos.getX()-stredX , tPos.getY()-stredY , null);
        }

        if( Global.rucksack().showInfo() ){
            g.setColor(Color.BLACK);
            g.setFont(idFont);
            g.drawString( info , drawPos.getX() , drawPos.getY()-3 );
        }


        //super.draw( g,isSel, info, pos, center, rot);

    }





}
