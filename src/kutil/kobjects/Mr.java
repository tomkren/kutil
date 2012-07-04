package kutil.kobjects;

import kutil.core.Global;
import kutil.core.KAtts;
import kutil.shapes.MrShape;
import net.phys2d.math.Vector2f;

/**
 *
 * @author Tomáš Křen
 */
public class Mr extends Basic implements Figure{

      /**
     * Vytvoří Mistra podle KAtts.
     */
    public Mr( KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * kopírovací konstruktor.
     */
    public Mr( Mr b ) {
        super(b);
        create();
    }

    @Override
    public KObject copy() {
        return new Mr(this);
    }

    private void create(){
        setType( "mr" );
        setPhysical(true);
        setAttached(false);
        setShape( new MrShape(this) );
        setStepInside(false);

        //Global.rucksack().setActualBudha(this);
    }

    private boolean isFreezed(){
        if( parent() instanceof Basic ){
            return ! ((Basic)parent()).getStepInside();
        }else{
            return true;
        }
    }


    private static final float    KROK         = 7.0f;
    private static final Vector2f KROK_NAHORU  = new Vector2f(0,-KROK);
    private static final Vector2f KROK_DOLEVA  = new Vector2f(-KROK,0);
    private static final Vector2f KROK_DOPRAVA = new Vector2f(KROK,0);


    public void figureCmd( Figure.FigureCmd cmd ){

       if( isFreezed() || ! Global.rucksack().isSimulationRunning() ) return;
       
       switch( cmd ){
           case right:
               frame ++;
               getBody().adjustPosition(KROK_DOPRAVA);
               break;
           case left:
               frame --;
               getBody().adjustPosition(KROK_DOLEVA);
               break;
           case up:
               getBody().adjustPosition(KROK_NAHORU);
               break;
           default: break;
       }

   }

   private int frame = 0;
   public int getFrame(){
       return frame/2;
   }


}
