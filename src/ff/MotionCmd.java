package ff;

import kutil.core.Int2D;

/**
 *
 * @author sekol
 */
public class MotionCmd{
    private char  blockPx;
    private Int2D delta;

    public MotionCmd( char blockPx , Int2D delta ){
        this.blockPx = blockPx;
        this.delta   = delta;
    }

    public char  getPx()    { return blockPx; }
    public Int2D getDelta() { return delta;   }
}
