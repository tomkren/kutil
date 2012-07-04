package kutil.kobjects;

import java.awt.Color;
import kutil.items.Int2DItem;
import kutil.items.ListItem;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.core.Utils;
import kutil.shapes.FlyShape;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;

/**
 * Objekt virtuálního světa reprezentující mouchu - základní typ agenta.
 * @author Tomáš Křen
 */
public class Fly extends Basic {

    private Int2DItem goal;    // cíl mouchy
    private ListItem  mem;     // paměť mouchy oddělená od programu

    private FlyCollisionListener listener;    // listener sloužící k detekci kolizí s mouchou
    private World                parentWorld; // svět fyz. symulace rodiče této mouchy

    private boolean reactionDroped;     // určuje zda byla učiněna reakce na příchod do cíle
    private Direction actualDirection;  // směr naposledy vykonaný jako instrukce pro změnu cíle
    private Direction hitDirection;     // směr, ve kterém došlo ke kolizi

    /**
     * Vytvoří Fly podle KAtts.
     */
    public Fly( KAtts kAtts ){
        super( kAtts );
        goal = items().addInt2D(kAtts , "goal", Int2D.zero() );
        mem  = items().addList(kAtts  , "mem" );

        create();
    }

    /**
     * Vytvoří základní mouchu.
     */
    public Fly(  ){
        super( );
        goal = items().addInt2D( "goal", Int2D.zero() );
        mem  = items().addEmptyList( "mem" ) ;
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Fly( Fly f ){
        super(f);
        goal = items().addInt2D( "goal", f.goal.get().copy() );

        mem  = items().addEmptyList( "mem" ) ;

        for( KObject o :  f.mem.get() ){
            KObject copy = o.copy();
            mem.get().add( copy );
        }
       
        create();
    }

    @Override
    public KObject copy() {
        return new Fly( this );
    }

    private void create(){
        setType( "fly" );
        setPhysical(true);
        setAttached(false);
        setShape( new FlyShape(this) );
        setIsAffectedByGravity(false);
        setBgcolor(new Color(80, 80, 80));

        reactionDroped  = false;
        actualDirection = new Direction( Direction.Vals.right );
        hitDirection    = null;

        setSpeed(60);
    }

    @Override
    public void setParent(KObject newParent) {
        super.setParent(newParent);
        resetCollisionListener();
    }

    @Override
    public void init() {
        super.init();

        for( KObject o : mem.get() ){
            o.init( );
        }

        resetCollisionListener();
    }

    private void resetCollisionListener(){
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }

        parentWorld = getParentWorld();

        if( parentWorld != null ){
            listener = new FlyCollisionListener(this);
            parentWorld.addListener( listener );
        }

    }



    @Override
    public void step() {
        super.step();


        if( ! Global.rucksack().isSimulationRunning() ) return;

        resetInsideSlots();
        refreshSmellSensors();

        stepToGoal();

        if( !isGoalReached() ){
            reactionDroped = false;
        }

        if( !reactionDroped && isGoalReached() ){

            reactionDroped = true;

            for( KObject o : inside() ){
                if( o instanceof GoalSensor ){
                    GoalSensor fg = (GoalSensor) o;
                    fg.fire( actualDirection.copy() );
                }
            }            
        }

        if( hitDirection != null ){

            for( KObject o : inside() ){
                if( o instanceof TouchSensor ){
                    TouchSensor ts = (TouchSensor) o;
                    ts.fire( hitDirection.copy() );
                }
            }

            hitDirection = null;
        }
    }

    private Direction.Vals appleDirVal, waspDirVal , flyDirVal;

    /**
     * Pokud ve slotu nějaké funkce čeká nějaký zapomentů vstup, smažeme ho.
     */
    private void resetInsideSlots(){
        for( KObject o : inside() ){
            if( o instanceof Function ){
                Function f = (Function) o;
                f.resetSlots();
            }
        }
    }

    /**
     * Spočítá nejbližší pozcie jablíčka, mouchy a vosy. Používá se pak pro senzory.
     */
    private void refreshSmellSensors(){

        if( parent() == null ) return;

        KObject nearestApple = null;
        KObject nearestFly   = null;
        KObject nearestWasp  = null;
        double  minDistApple = Double.MAX_VALUE;
        double  minDistFly   = Double.MAX_VALUE;
        double  minDistWasp  = Double.MAX_VALUE;

        for( KObject o : parent().inside() ){
            if( o instanceof Apple ){
                double distance = pos().minus(o.pos()).abs();
                if( distance < minDistApple ){
                    nearestApple = o;
                    minDistApple = distance;
                }
            }
            else if( o instanceof Wasp ){
                double distance = pos().minus(o.pos()).abs();
                if( distance < minDistWasp ){
                    nearestWasp = o;
                    minDistWasp = distance;
                }
            }
            else if( o instanceof Fly ){
                double distance = pos().minus(o.pos()).abs();
                if( distance < minDistFly ){
                    nearestFly = o;
                    minDistFly = distance;
                }
            }
        }

        appleDirVal = computeDirVal(nearestApple);
        waspDirVal  = computeDirVal(nearestWasp );
        flyDirVal   = computeDirVal(nearestFly  );
    }

    /**
     * Spočítá směr, v jakém se nacházi daný objekt.
     */
    private Direction.Vals computeDirVal( KObject o ){
        
        if( o == null ) return Direction.Vals.randdir;
        
        Int2D relPos = o.pos().minus(pos());
        boolean posHalf = relPos.getX() >   relPos.getY();
        boolean negHalf = relPos.getX() > - relPos.getY();

        if( posHalf ){ if( negHalf ){ return Direction.Vals.right; }
                       else         { return Direction.Vals.up;    }
        }else        { if( negHalf ){ return Direction.Vals.down;  }
                       else         { return Direction.Vals.left;  }}
    }


    /**
     * Vrací směr nejližšího jablíčka.
     */
    public KObject appleSensor(){
        Direction dir = new Direction(appleDirVal);
        return KObjectFactory.insertKObjectToSystem(dir, null);
    }
    /**
     * Vrací směr nejližší vosy.
     */
    public KObject waspSensor(){
        Direction dir = new Direction(waspDirVal);
        return KObjectFactory.insertKObjectToSystem(dir, null);
    }
    /**
     * Vrací směr nejližší mouchy.
     */
    public KObject flySensor(){
        Direction dir = new Direction(flyDirVal);
        return KObjectFactory.insertKObjectToSystem(dir, null);
    }
    /**
     * Vrací obsah paměti v podobě výrazu Kispu.
     */
    public String memString(){
        
        StringBuilder sb = new StringBuilder();
        sb.append("( ");

        for( KObject o : mem.get() ){
            sb.append(o.toKisp());
            sb.append(" ");
        }

        sb.append(")");

        return sb.toString();
    }

    /**
     * Interpretuje KObjekt jako příkaz pro mouchu a provede ho.
     * @param o objekt virtuálního světa který bude interpretován jako příkaz
     */
    public void flyCmd( KObject o ){

        if( o instanceof Direction ){

            actualDirection = ((Direction)o) ;

            switch( actualDirection.get() ){
                case up   : moveGoal( 0, -1); break;
                case down : moveGoal( 0,  1); break;
                case left : moveGoal(-1,  0); break;
                case right: moveGoal( 1,  0); break;

                case randdir :
                    int x = Global.random().nextInt(3)-1;
                    int y = Global.random().nextInt(3)-1;
                    moveGoal(x, y);
                    break;
            }

            reactionDroped = false;
        }
    }

    /**
     * Vrátí seznam reprezentující kopii paměti.
     */
    public KObject getMem(){

        Box box = new Box();
        KObjectFactory.insertKObjectToSystem(box, null);

        for( KObject o : mem.get() ){
            KObject copy = KObjectFactory.insertKObjectToSystem(o.copy(), null);
            box.add( copy );

        }
        box.step();

        return box;
    }

    /**
     * Vrátí první prvek paměti.
     */
    public KObject topMem( ){
        if( mem.get().isEmpty() ) return null;
        return KObjectFactory.insertKObjectToSystem( mem.get().getFirst().copy() , null);
    }

    /**
     * Vrátí první prvek paměti a smaže ho.
     */
    public KObject popMem( ){
        if( mem.get().isEmpty() ) return null;
        return mem.get().pop();
    }

    /**
     * Rozzšíří paměť o kopii objektu.
     */
    public void pushMem( KObject vzor ){
        if( vzor == null ) return;
        mem.get().push( KObjectFactory.insertKObjectToSystem( vzor.copy() , null) );
    }

    /**
     * Vrátí objekt, který se nachází ve slotu na aktuální pozici mouchy.
     */
    public KObject getDataFromActualPosition(){

        if( parent() == null || !(parent() instanceof Basic) ) return null;

        Basic parent = (Basic) parent();

        for( KObject o : parent.inside() ){
            if( o instanceof Slot ){

                Slot slot = (Slot)o;

                if( isNear( slot.pos() , pos() , nearForSlot ) ){

                    if( slot.inside().isEmpty() ){
                        return null;
                    }

                    KObject ret = slot.inside().getFirst().copy();
                    return KObjectFactory.insertKObjectToSystem( ret , null );
                }
            }
        }

        return KObjectFactory.insertKObjectToSystem(new Box(),null);
    }

    /**
     * Změní obsah slotu na aktualní pozici mouchy na kopii předaného objektu.
     */
    public void setDataForActualPosition( KObject vzor ){

        if( parent() == null || !(parent() instanceof Basic) ) return;

        Basic parent = (Basic) parent();

        for( KObject o : parent.inside() ){
            if( o instanceof Slot ){

                Slot slot = (Slot)o;

                if( isNear( slot.pos() , pos() ,  nearForSlot ) ){

                    slot.directClear();
                    KObject copy = vzor.copy();

                    slot.directAdd( copy );
                    KObjectFactory.insertKObjectToSystem( copy , slot );
                    return;
                }
            }
        }

        //na tom místě není, uděláme jí 8-)
        Slot newSlot =  (Slot)KObjectFactory.insertKObjectToSystem( new Slot() , parent );
        parent.addFirst(newSlot);

        int posX = ((int)Math.floor( pos().getX() / 32.0))*32 + 16 ;
        int posY = ((int)Math.floor( pos().getY() / 32.0))*32 + 16 ;

        newSlot.setPos( new Int2D( posX  , posY ) );

        KObject copy = vzor.copy();
        newSlot.directAdd( copy );
        KObjectFactory.insertKObjectToSystem( copy , newSlot );

    }

    /**
     * posune souřadnici cíle
     * @param x posun x-ové složky
     * @param y posun y-ové složky
     */
    private void moveGoal( int x , int y ){
        goal.set(goal.get().plus(new Int2D(x, y)));
    }

    /**
     *  Vrací směr naposledy vykonaný jako instrukce pro změnu cíle
     */
    public Direction getActualDirection(){
        return  actualDirection;
    }


    private static final Int2D shift      = new Int2D(16,16);
    private static final Int2D childShift = new Int2D(0,64);

    /**
     * vrací pozici cíle ve standardních souřadnicích
     */
    public Int2D goalPos(){
        return goal.get().times(32).plus(shift);
    }

    private boolean isGoalReached(){
        return isNear( goalPos() , pos() , nearForGoal );
    }


    private static final double nearForGoal = 5.0;
    private static final double nearForSlot = 6.0;


    private boolean isNear( Int2D pos1 , Int2D pos2 , double r ){
        return pos1.minus(pos2).abs() < r;
    }

    private void stepToGoal(){

        Vector2f v = Int2D.toROVector2f( goalPos() );
        Body body = getBody();

        v.sub( body.getPosition() );

        v.normalise();
        v.scale(speed);

        Utils.stopBody( body );
        body.adjustVelocity( v );

    }

    private int speed;
    /**
     * Nastaví rychlost mouchy.
     */
    public void setSpeed( int s ){
        speed = s;
    }



    /**
     * Reaguje na kolizi.
     * @param hitPos souřadnice kolize
     * @param hitObject objekt s kterým moucha kolidovala
     */
    protected void handleCollision( Int2D hitPos , KObject hitObject ){
        Int2D relHitPos = hitPos.minus( pos() ) ;
        int x = relHitPos.getX();
        int y = relHitPos.getY();

        if( y < -8 ){
            //Log.it("nahoře");
            hitDirection = new Direction(Direction.Vals.up);
        }
        else if( y > 8 ){
            //Log.it("dole");
            hitDirection = new Direction(Direction.Vals.down);
        }
        else if( x < -8 ){
            //Log.it("vlevo");
            hitDirection = new Direction(Direction.Vals.left);
        }
        else if( x > 8 ){
            //Log.it("vpravo");
            hitDirection = new Direction(Direction.Vals.right);
        }
        else{
            //Log.it("nevim");
            //hitDirection = new Direction(Direction.Vals.randdir);
        }


        if( (hitObject instanceof Apple) && !(this instanceof Wasp) ){
            hitObject.delete();
            KObject child = copy();
            
            // tady když se misto null da parent() a druhej řadek se da pryč
            // tak je moucha najednou zazračně rychlejší
            KObjectFactory.insertKObjectToSystem( child, null );
            child.setParent( parent() );

            parent().add(child);

            child.setPos(pos().plus(childShift));
        }
    }

}

class FlyCollisionListener implements CollisionListener {

    private Fly fly;

    public FlyCollisionListener( Fly f ){
        this.fly = f;
    }

    public void collisionOccured(CollisionEvent event) {

        Body inputBody = null;
        if ( event.getBodyA() == fly.getBody() ) {
            inputBody = event.getBodyB();
        } else if(  event.getBodyB() == fly.getBody() ) {
            inputBody = event.getBodyA();
        }

        if( inputBody == null ) return;

        fly.handleCollision( Int2D.fromROVector2f( event.getPoint() ) ,
                             (KObject) inputBody.getUserData() );
    }
}
