package kutil.shapes;

import java.awt.Font;
import java.awt.Graphics2D;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.kobjects.Mr;

/**
 * Tvar KObjectu Mr.
 * @author Tomáš Křen
 */
public class MrShape extends AnimationShape {

    private Mr mr;

    public MrShape( Mr mr ) {
        super( Global.shapeFactory().mrImgs,
               Global.shapeFactory().mrImgSel,
                new Int2D[]{new Int2D( 23 ,  0 ) ,
                            new Int2D( 60 ,  0 ) ,
                            new Int2D( 60 , 151) ,
                            new Int2D( 23 , 151) },
                new Int2D(28,14),
                new Int2D(28,14)
              );
        this.mr = mr;
    }


    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {

        super.drawFrame( mr.getFrame() ,  g, isSel, info, pos, center, rot,isRotable);

    }

}
