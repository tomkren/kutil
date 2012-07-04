package kutil.kobjects;


import kutil.core.KAtts;
import kutil.shapes.SlotShape;

/**
 * Slot je objekt virtuálního světa zastávající roli paměťové buňky, zvlaště
 * myšlený jako místo, kam může moucha zapisovat nebo z něj číst.
 * @author Tomáš Křen
 */
public class Slot extends Basic {
    
    public Slot( KAtts kAtts ){
        super( kAtts );
        create();
    }

    public Slot(){
        super();
        create();
    }

    public Slot( Slot s ){
        super(s);
        create();
    }

    @Override
    public KObject copy() {
        return new Slot( this );
    }

    private void create(){
        setType( "slot" );
        setShape( new SlotShape(this) );

        setStepInside(false);

        
    }

//    @Override
//    public void setPos(Int2D p) {
//        super.setPos(correctPos(p ));
//    }
//
//    private Int2D correctPos(Int2D pos ){
//        int x = pos.getX();
//        int y = pos.getY();
//
//        x = ((int)Math.round(x/32.0)*32);
//        y = ((int)Math.round(y/32.0)*32);
//
//
//        return new Int2D(x, y) ;
//    }

    


}
