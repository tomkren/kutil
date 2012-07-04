package kutil.kobjects;

import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.shapes.BudhaShape;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;

/**
 * Objekt virtuálního světa fungující jako figurka ovládánaná hráčem.
 * @author Tomáš Křen
 */
public class Budha extends Basic implements Figure{

    private World parentWorld;
    private BudhaCollisionListener listener;


    /**
     * Vytvoří Budhu podle KAtts.
     */
    public Budha( KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * kopírovací konstruktor.
     */
    public Budha( Budha b ) {
        super(b);
        create();
    }

    @Override
    public KObject copy() {
        return new Budha(this);
    }

    private boolean isFreezed(){
        if( parent() instanceof Basic ){
            return ! ((Basic)parent()).getStepInside();
        }else{
            return true;
        }
    }

    private void create(){
        setType( "budha" );
        setPhysical(true);
        setAttached(false);
        setShape( new BudhaShape(this) );
        setStepInside(false);

        Global.rucksack().setActualFigure(this);
    }

    @Override
    public void step() {
        super.step();
    }

    @Override
    public void setParent(KObject newParent) {
        super.setParent(newParent);
        resetCollisionListener();
    }

    


    @Override
    public void init() {
        super.init();
        resetCollisionListener();
    }


    private void resetCollisionListener(){
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }

        parentWorld = getParentWorld();

        if( parentWorld != null ){
            listener = new BudhaCollisionListener(this);
            parentWorld.addListener( listener );
        }

    }


    /**
     * Reaguj na příkaz.
     * @param cmd příkaz pro budhu
     */
    public void figureCmd( Figure.FigureCmd cmd ){

        if( isFreezed() || ! Global.rucksack().isSimulationRunning() ) return;
        switch( cmd ){
            case left:
                touched = null;
                if( getBody().getVelocity().getY() > 0.1f  ){
                    getBody().adjustVelocity( KROK_DOLEVA );
                } else if( getBody().getVelocity().length() < 10f ) {
                    getBody().adjustVelocity( KROK_DOLEVA );
                }
                break;
            case right:
                touched = null;
                if( getBody().getVelocity().getY() > 0.1f  ){
                    getBody().adjustVelocity( KROK_DOPRAVA );
                }
                else if( getBody().getVelocity().length() < 10f ) {
                    getBody().adjustVelocity( KROK_DOPRAVA );
                }
                break;
            case up:
                touched = null;
                if( getBody().getVelocity().getY() > -20f ){
                    getBody().adjustVelocity( KROK_NAHORU );
                }
                while( getBody().getVelocity().getY() > -1f ){
                    getBody().adjustVelocity( KROK_NAHORU );
                }
                break;
            case down:
                if( touched != null ){
                    moveToBudha(touched);
                    touched = null;
                }else{
                    moveFromBudha();
                }
                break;
            case shiftDown:
                if( touched != null ){
                    moveDeepToBudha(touched);
                    touched = null;
                }else{
                    moveDeepFromBudha();
                }
                break;
            case shiftLeft:
                rotateInsideLeft();
                break;
            case shiftRight:
                rotateInsideRight();
                break;
            case shiftUp:
                moveAllFromBudha();
                break;
            default:
                break;
        }
    }

    private static final float    KROK         = 5.0f;
    private static final Vector2f KROK_NAHORU  = new Vector2f(0,-2f*KROK);
    private static final Vector2f KROK_DOLEVA  = new Vector2f(-KROK,-KROK);
    private static final Vector2f KROK_DOPRAVA = new Vector2f(KROK,-KROK);

    private static final Int2D shiftBudha = new Int2D(0,-50);
    private static final Int2D shiftObject = new Int2D(0,60);


    private void moveToBudha( KObject o ){
        o.remove();
        o.setParent(this);
        this.addFirst(o);
    }
    private void moveFromBudha(){
        KObject first = popFirst();

        if( first == null ) return;

        setPos( pos().plus( shiftBudha ) );
        first.setPos( pos().plus( shiftObject ) );
        parent().add(first);
        first.setParent( parent() );
    }

    private void moveAllFromBudha(){
        KObject first = popFirst();

        while( first != null ){

            //setPos( pos().plus( shiftBudha ) );
            //first.setPos( pos().plus( shiftObject ) );
            parent().add(first);
            first.setParent( parent() );

            step();

            first = popFirst();
        }
    }

    private void moveDeepToBudha( KObject o ){
        if( ! inside().isEmpty() ){
            o.remove();
            KObject first = inside().getFirst();
            o.setParent(first);
            first.addFirst(o);
            first.step();
        }
    }
    private void moveDeepFromBudha(){ 
        if( ! inside().isEmpty() ){

            KObject first = inside().getFirst();
            KObject firstFirst = first.popFirst();
            first.step();
            
            if( firstFirst == null ) return;

            setPos( pos().plus( shiftBudha ) );
            firstFirst.setPos( pos().plus( shiftObject ) );
            parent().add(firstFirst);
            firstFirst.setParent( parent() );
        }
    }

    private void rotateInsideLeft(){
        KObject first = popFirst();
        if( first == null ) return;
        add(first);
        step();
    }

    private void rotateInsideRight(){
        KObject last = popLast();
        if( last == null ) return;
        addFirst(last);
        step();
    }

    private KObject touched;
    protected void handleCollision( KObject o ){
        if( ! ((Basic)o).isTakable() ) {
            touched = null;
        } else{
            touched  = o;
        }
    }
}

class BudhaCollisionListener implements CollisionListener {

    private Budha budha;

    public BudhaCollisionListener( Budha b ){
        this.budha = b;
    }

    public void collisionOccured(CollisionEvent event) {

        Body inputBody = null;
        if ( event.getBodyA() == budha.getBody() ) {
            inputBody = event.getBodyB();
        } else if(  event.getBodyB() == budha.getBody() ) {
            inputBody = event.getBodyA();
        }

        if( inputBody == null ) return;

        budha.handleCollision( (KObject) inputBody.getUserData() );
    }
}
