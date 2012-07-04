package kutil.shapes;

import java.awt.Graphics2D;
import kutil.core.Int2D;
import net.phys2d.math.ROVector2f;
import net.phys2d.raw.shapes.DynamicShape;

/**
 * Interface zastřešující všechny tvary objektů.
 * @author Tomáš Křen
 */
public interface KShape {

    public void draw(Graphics2D g, boolean isSel, String id , Int2D pos , Int2D c , double rot, boolean isRotable );
    public boolean isHit( Int2D pos , Int2D clickPos , double rot);
    public DynamicShape getPhys2dShape();
    public ROVector2f getPhys2dCenter( Int2D pos );
    public Int2D getPosByPhys2dCenter( ROVector2f v );

}
