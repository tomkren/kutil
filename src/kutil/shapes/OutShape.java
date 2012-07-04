package kutil.shapes;

import kutil.core.Global;
import kutil.core.Int2D;

/**
 * Tvar KObjectu Out.
 * @author Tomáš Křen
 */
public class OutShape extends ImageShape{

    public OutShape( ){
        super(Global.shapeFactory().outImg ,
              Global.shapeFactory().outImgSel,
              new Int2D[]{ new Int2D(0,0),
                           new Int2D(34,0),
                           new Int2D(34,19),
                           new Int2D(0,19)
                         },
              new Int2D(0,0),
              new Int2D(17,25));
    }

}
