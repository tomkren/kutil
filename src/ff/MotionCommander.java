package ff;

import java.util.List;
import java.util.Set;
import kutil.core.Int2D;

/**
 *
 * @author Tomáš Křen
 */
public interface MotionCommander {
    public List<MotionCmd> getNewCmds();
    public void addBlock( char px , Set<Int2D> poses );
    public void removeBlock( char px );
}