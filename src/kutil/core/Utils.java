package kutil.core;

import java.awt.Color;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

/**
 * Pomocná třída statických metod pro různé úkony.
 * @author Tomáš Křen
 */
public class Utils {

    /**
     * Zastav těleso fyzikální simulace
     * @param body
     */
    public static void stopBody(Body body){
        ROVector2f v = body.getVelocity();
        body.adjustVelocity( new Vector2f( - v.getX(), - v.getY() ) );
    }

    public static Color parseColor(String colorCmd ){
        String[] part = colorCmd.split(" ");

        return new Color( Integer.parseInt(part[0]),
                          Integer.parseInt(part[1]),
                          Integer.parseInt(part[2]));
    }

}
