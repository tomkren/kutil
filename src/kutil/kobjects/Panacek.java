package kutil.kobjects;

import java.awt.Graphics2D;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.shapes.AnimationShape;
import net.phys2d.math.Vector2f;

/**
 *
 * @author Tomáš Křen
 */
public class Panacek extends Basic implements Figure{

      /**
     * Vytvoří Panáčka podle KAtts.
     */
    public Panacek( KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * kopírovací konstruktor.
     */
    public Panacek( Panacek p ) {
        super(p);
        create();
    }

    @Override
    public KObject copy() {
        return new Panacek(this);
    }

    private void create(){
        setType( "panacek" );
        setPhysical(true);
        setAttached(false);
        setShape( new PanacekShape(this) );
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


    private static final float    KROK         = 3.0f;
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
       return frame;
   }


}

class PanacekShape extends AnimationShape {

    private Panacek panacek;

    public PanacekShape( Panacek panacek ) {
        super( Global.shapeFactory().panacekImgs,
               Global.shapeFactory().panacekImgSel,
                new Int2D[]{new Int2D(  0 , 0 ) ,
                            new Int2D( 19 , 0 ) ,
                            new Int2D( 19 , 34 ) ,
                            new Int2D(  0 , 34 ) },
                new Int2D(10,17),
                new Int2D(10,17)
              );
        this.panacek = panacek;
    }


    @Override
    public void draw(Graphics2D g, boolean isSel, String info,
                     Int2D pos, Int2D center, double rot,boolean isRotable) {

        super.drawFrame( panacek.getFrame() ,  g, isSel, info, pos, center, rot,isRotable);

    }

}