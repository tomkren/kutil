package kutil.shapes;

import kutil.core.Global;
import kutil.core.Int2D;

/**
 *
 * @author Tomáš Křen
 */
public class EarthShape extends ImageCircleShape {

    public EarthShape() {
        super( Global.shapeFactory().earthImg,
                   Global.shapeFactory().earthImgSel,
                    93,
                    new Int2D(108,110),
                    new Int2D(108,110)
                  );
    }





}
