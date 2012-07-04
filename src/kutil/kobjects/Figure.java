package kutil.kobjects;

/**
 *
 * @author sekol
 */
public interface Figure {

    /**
     * Jednotlivé příkazy, které může Figure dostat.
     */
    public enum FigureCmd { left, right, up, down ,shiftLeft, shiftRight, shiftUp, shiftDown }

    public void figureCmd( FigureCmd cmd );
    public KObject parent();

}
