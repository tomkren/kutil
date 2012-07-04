package kutil.shapes;

import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Apple.
 * @author Tomáš Křen
 */
public class AppleShape extends ImageShape {

    public AppleShape() {

    super( Global.shapeFactory().appleImg ,
            Global.shapeFactory().appleImgSel ,
            new Int2D[]{new Int2D(  0, 0 ),
                        new Int2D( 30, 0 ),
                        new Int2D( 30, 34 ),
                        new Int2D(  0, 34 ) },
            new Int2D( 15, 17 ),
            new Int2D( 15, 17 )
            );

    }



}
