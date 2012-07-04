package kutil.kevents;

import kutil.kobjects.Frame;
import kutil.core.Int2D;

/**
 * DragEvent představuje soubor informací podstatných při reakci na táhnutí KObjekt myší,
 * tyto informace jsou pozice, vektor posunutí a Frame ve kterém bylo posunuto.
 * @author Tomáš Křen
 */
public class DragEvent implements KEvent {

    private Int2D pos;
    private Int2D delta;
    private Frame frame;

    public DragEvent( Int2D pos , Int2D delta , Frame frame ){
        this.pos   = pos   ;
        this.delta = delta ;
        this.frame = frame ;
    }

    public Int2D getPos(){
        return pos;
    }

    public Int2D getDelta(){
        return delta;
    }

    public Frame getFrame(){
        return frame;
    }

    @Override
    public String toString() {
        return "[Draged from : "+ pos +" by : "+ delta +" ]";
    }
}
