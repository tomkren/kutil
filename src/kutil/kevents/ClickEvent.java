package kutil.kevents;


import kutil.kobjects.Frame;
import kutil.core.Int2D;

/**
 * ClickEvent představuje soubor informací podstatných při reakci na kliknutí na KObjekt,
 * tyto informace jsou pozice kliknutí a Frame ve kterém bylo kliknuto.
 * @author Tomáš Křen
 */
public class ClickEvent implements KEvent {

    private Int2D pos;
    private Frame frame;

    public ClickEvent( Int2D pos , Frame frame ){
        this.pos   = pos;
        this.frame = frame;
    }

    public Int2D getPos(){
        return pos;
    }

    public Frame getFrame(){
        return frame;
    }

    @Override
    public String toString() {
        return "[Clicked on position : "+ pos +" ]";
    }



}
