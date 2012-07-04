package kutil.shapes;

import java.awt.image.BufferedImage;
import kutil.kobjects.Direction;
import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Direction.
 * @author Tomáš Křen
 */
public class DirectionShape extends ImageShape {

    private Direction direction;

    public DirectionShape( Direction d ){
        super( img(d) ,
               imgSel(d) ,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(40,0),
                           new Int2D(40,40),
                           new Int2D(0,40)
                         },
              new Int2D(20,20),
              new Int2D(20,20));

        direction = d;
    }

    public void resetImage(){
        resetImage( img(direction) , imgSel(direction) );
    }
    
    private static BufferedImage img( Direction d ){
        switch( d.get() ){
            case up     : return Global.shapeFactory().upImg;
            case down   : return Global.shapeFactory().downImg;
            case left   : return Global.shapeFactory().leftImg;
            case right  : return Global.shapeFactory().rightImg;
            case randdir : return Global.shapeFactory().randomImg;
        }
        return null;
    }
    private static BufferedImage imgSel( Direction d ){
        switch( d.get() ){
            case up     : return Global.shapeFactory().upImgSel;
            case down   : return Global.shapeFactory().downImgSel;
            case left   : return Global.shapeFactory().leftImgSel;
            case right  : return Global.shapeFactory().rightImgSel;
            case randdir : return Global.shapeFactory().randomImgSel;
        }
        return null;
    }

}
