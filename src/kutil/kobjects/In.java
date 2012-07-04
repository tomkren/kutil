package kutil.kobjects;

import kutil.items.StringItem;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.shapes.InShape;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;

/**
 * Objekt virtuálního světa zajištující převod oběktů na "signál", který může poslat funkci.
 * @author Tomáš Křen
 */
public class In extends Basic implements Outputable {

    private StringItem targetString;

    private Inputable target;
    private int       targetPort;

    private World parentWorld;
    private InCollisionListener listener;

    public In( KAtts kAtts ){
        super( kAtts );
        targetString = items().addString( kAtts , "target" , "" );
        create();
    }
    
    public In( In in ){
        super( in );
        targetString = items().addString( "target" , in.targetString.get() );
        create();
    }
    
    public In(  ){
        super( );
        targetString = items().addString( "target" , "" );
        create();
    }


    @Override
    public KObject copy() {
        return new In(this);
    }

    private void create(){
        setType("in");
        setPhysical(true);
        setAttached(true);
        setShape( new InShape(this) );
    }

    @Override
    public void init() {
        super.init();
        resetCollisionListener();
        resetTargets();
    }

    @Override
    public void resolveCopying() {
        super.resolveCopying();

        if( isCopied() ){

            String newTargetString = Global.idChangeDB().repairTargetString( targetString.get() ) ;

            targetString.set( newTargetString );
            if( targetString.get() != null ){
                resetTargets();
            }
        }
    }

    @Override
    public void resolveRenaming(String oldId, String newId) {
        super.resolveRenaming(oldId, newId);

        targetString.set( Function.repairTarget(oldId, newId, targetString.get() ) );
        if( targetString.get() != null ){
                resetTargets();
        }
    }



    @Override
    public void setParent(KObject newParent) {
        super.setParent(newParent);
        resetCollisionListener();
    }


    private void resetCollisionListener(){
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }

        parentWorld = getParentWorld();

        if( parentWorld != null ){
            listener = new InCollisionListener(this);
            parentWorld.addListener( listener );
        }

    }

    @Override
    public void delete() {
        super.delete();
        if( parentWorld != null ){
            parentWorld.removeListener(listener);
        }
    }





    @Override
    public void click(Int2D clickPos) {
        if(bottomArrowBarHit(clickPos)){
            bottomArrowBar();
        }
        super.click(clickPos);
    }

    @Override
    public void drag(Int2D clickPos, Int2D delta,Frame f) {
        if(bottomArrowBarHit(clickPos)){
            //bottomArrowBar();
        }
        super.drag(clickPos,delta,f);
    }

    private void bottomArrowBar(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }

    private boolean bottomArrowBarHit(Int2D clickPos){
        return clickPos.minus( pos() ).getY() > 9 ;
    }

    private void resetTargets(){

        if( targetString.get().equals("null") ){
                target     = null;
                targetPort = -1;
                return;
        }

        String[] parts = targetString.get().split(":");

        KObject o = idDB().get(parts[0]);
        if( o instanceof Inputable ){
            target = (Inputable) o;
            targetPort = Integer.parseInt(parts[1]);
        }
    }

    public void setTargetAndPort(int fromPort, Inputable t, int p) {

        String[] ts;
        if( targetString.get() != null ){
            ts = targetString.get().split(" ");
        }
        else{
            ts = new String[0];
        }

        if( ts.length <= fromPort ){
            String[] ts2 = new String[fromPort+1];
            System.arraycopy(ts, 0, ts2, 0, ts.length);
            for( int i=ts.length ; i<ts2.length ; i++ ){
                ts2[i] = "null";
            }
            ts = ts2;
        }

        if( t != null ){
            ts[fromPort] = t.id() + ":" + p;
        }else{
            ts[fromPort] = "null";
        }

        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i<ts.length ; i++ ){
            sb.append( ts[i] );
            if( i != ts.length-1 ){
                sb.append(" ");
            }
        }

        //Log.it("NOVý targetString : >"+ sb +"<" );
        targetString.set( sb.toString() );
        resetTargets();
    }



    public void handleWorldInput( KObject input ){
        input.remove();
        if( target != null ){
            target.handleInput( input , targetPort );
        }
    }

    public Int2D getTargetArrowEnd(){
        if( target == null ) return pos();

        return target.getArrowEnd( targetPort );
    }

    public boolean doDrawArrow(){
        if(target == null ) return false;
        return ( target.parent() == parent() );
    }

}

class InCollisionListener implements CollisionListener {

    private In in;

    public InCollisionListener( In in ){
        this.in = in;
    }


    public void collisionOccured(CollisionEvent event) {

        Body inputBody = null;
        if ( event.getBodyA() == in.getBody() ) {
            inputBody = event.getBodyB();
        } else if(  event.getBodyB() == in.getBody() ) {
            inputBody = event.getBodyA();
        }

        if( inputBody == null ) return;

        in.handleWorldInput( (KObject) inputBody.getUserData() );
    }
}
