package kutil.kobjects;

import ff.MotionCmd;
import ff.MotionCommander;
import ff.Sea;
import java.util.*;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.core.Log;
import kutil.functions.UnarImplementation;
import kutil.shapes.ImageShape;
import org.javatuples.Pair;

/**
 *
 * @author Tomáš Křen
 */
public class FFUnit extends Function { //Basic{
 
    public static final int  SIDE  = 16;

    private Map<Character,KObject>          pxToKOb;
    private Map<String ,Character>          oidToPx;
    private char                            nextPx;
    private int                             step;
    private int                             stepp;
    private MotionCommander                 mc;
    private List<MotionCmd>                 nowRunningCmds;
    private List<Pair<KObject,Character>>   toAdd;

    private void create(){
        setType( "ffunit" );
        // setShape( new FFShape() );
        resetVal("ffunit");
        
        mc              = new Sea(new Int2D(20, 25) ); // new TestCommander();
        nowRunningCmds  = new LinkedList<MotionCmd>();
        pxToKOb         = new HashMap< Character , KObject >();
        oidToPx         = new HashMap< String  , Character >();
        nextPx          = 'a';
        step            = 0  ;
        stepp           = 0  ;
        toAdd           = new LinkedList<Pair<KObject,Character>>();
    }

    public void addKObject( KObject o ){
        
        String ffType = ( (Basic) o).getFFType();
        
        char px;
        
        if ( "wall".equals(ffType) ){
            px = '$';
        } else if ( "small".equals(ffType) ){
            px = '~';
        } else if ( "big".equals(ffType) ){
            px = '#';
        } else {
            px = nextPx;
            nextPx++;
        }
        
        pxToKOb.put( px , o );
        oidToPx.put( o.id() , px);
        
        toAdd.add( new Pair<KObject, Character>(o, px));

        //Log.it("[Added kobject] | px: '"+ nextPx + "' | xml: "+ o.toXml() );
    }

    public void removeKObject( KObject o ){

        char px = oidToPx.get(o.id());

        oidToPx.remove(o.id());
        pxToKOb.remove(px);

        if( px != '$' ) mc.removeBlock( px );
        else mc.removeBlock2( getFFPoses(o) );
        
        Log.it(mc);
        
        List<MotionCmd> toRemove = new LinkedList<MotionCmd>();
        
        for( MotionCmd cmd : nowRunningCmds ){
            if( px == cmd.getPx() ){
                toRemove.add(cmd);
            }
        }
        
        nowRunningCmds.removeAll(toRemove);
    }

    private static Set<Int2D> getFFPoses( KObject o ){

        Set<Int2D>   poses    = new HashSet<Int2D>();
        Stack<Int2D> testThem = new Stack<Int2D>();
        Set<Int2D>   tested   = new HashSet<Int2D>();
                
        Int2D initPos = o.pos().align(SIDE);
        
        poses .add( toFFPos(initPos) );
        tested.add(         initPos  );
        
        testThem.addAll(neigborPoses(initPos, tested));
        
        while( ! testThem.isEmpty() ){
            
            Int2D testNow = testThem.pop();
            tested.add(testNow);
            
            if( isKObjectAround(o, testNow) ){
                poses.add( toFFPos(testNow) );
                testThem.addAll(neigborPoses( testNow , tested));
            }
        }
        
        return poses;
    }

    private static final int dot1 = SIDE / 4;    //  4
    private static final int dot2 = SIDE / 2;    //  8
    private static final int dot3 = SIDE - dot1; // 12
    
    private static final Int2D[] testDots = 
        { new Int2D(dot1,dot1) , new Int2D(dot2,dot1) , new Int2D(dot3,dot1)  
        , new Int2D(dot1,dot2) , new Int2D(dot2,dot2) , new Int2D(dot3,dot2)  
        , new Int2D(dot1,dot3) , new Int2D(dot2,dot3) , new Int2D(dot3,dot3)  };   

    
    private static boolean isKObjectAround( KObject o , Int2D pos ){
        
        for( Int2D testDot : testDots ){
            if ( o.isHit( pos.plus(testDot) ) ) { return true; }
        }
        return false;
    }
    
   
    private static final Int2D[] deltas = 
        { new Int2D( SIDE,    0)
        , new Int2D(-SIDE,    0)
        , new Int2D(    0, SIDE)
        , new Int2D(    0,-SIDE)
        , new Int2D( SIDE, SIDE)
        , new Int2D( SIDE,-SIDE)
        , new Int2D(-SIDE, SIDE)
        , new Int2D(-SIDE,-SIDE) };
    
    private static List<Int2D> neigborPoses( Int2D pos , Set<Int2D> tested ){
        List<Int2D> ret = new LinkedList<Int2D>();
        
        for( Int2D delta : deltas ){
            Int2D nPos = pos.plus(delta);
            if( ! tested.contains( nPos ) ){
                ret.add(nPos);
            }
        }
        
        return ret;
    }
    
    private static Int2D toFFPos(Int2D pos){
        return new Int2D( pos.getX() / SIDE  , pos.getY() / SIDE );
    }    


    @Override
    public void step() {
        super.step();
        
        if( Global.rucksack().isSimulationRunning() ){

            if( stepp % 1 == 0 ) {
                
                if( step % (SIDE) == 0 ){

                    for( Pair<KObject,Character> p : toAdd ){
                        mc.addBlock( p.getValue1() , getFFPoses( p.getValue0() ) );
                    }


                    nowRunningCmds = mc.getNewCmds();

                    if( ! nowRunningCmds.isEmpty() || ! toAdd.isEmpty() ){
                        Log.it(mc);
                    }
                    
                    toAdd.clear();

                    
                } 

                for( MotionCmd cmd : nowRunningCmds ){
                    KObject o = pxToKOb.get( cmd.getPx() ) ;
                    if( o != null ){
                        o.setPos( o.pos().plus( cmd.getDelta() ) );
                    }
                }

                step++; 
            }
            
            stepp++;
            
        }
    }

    
    public FFUnit( KAtts kAtts ){
        super( kAtts );
        create();
    }
    
    public FFUnit( FFUnit u ) {
        super(u);
        create();
    }

    @Override
    public KObject copy() {
        return new FFUnit(this);
    }
    
    protected KObject getResponse(KObject o){
        
        mc.cmd(o);
        
        return null;
    }
    
    public static class FFUnitImplementation extends UnarImplementation {
    FFUnit ffUnit;
    public FFUnitImplementation(Function f){
        super("ffunit",0);
        ffUnit = (FFUnit) f;
    }
    public KObject compute( KObject o ) {

        KObject response = ffUnit.getResponse( o );

        if( response == null ) return null;

        return KObjectFactory.insertKObjectToSystem( response ,null );
    }
}
}




class TestCommander implements MotionCommander{

    private Set<Character> chs;

    public TestCommander(){
        chs = new HashSet<Character>();
    }

    public void cmd(KObject o) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    

    public void addBlock(char px, Set<Int2D> poses) {
       chs.add(px);
    }

    public void removeBlock(char px) {
        chs.remove(px);
    }

    public void removeBlock2(Set<Int2D> poses) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
    private static final Int2D[] deltas = { new Int2D(0,1) , new Int2D(1,0) };
    private int rada = 0;
    
    public List<MotionCmd> getNewCmds() {
        List<MotionCmd> ret = new LinkedList<MotionCmd>();

        
        for ( char px : chs ){
            ret.add( new MotionCmd( px , deltas[rada%deltas.length]  ) );
            rada++;
        }

        return ret;
    }

}

class FFShape extends ImageShape {

    public FFShape() {

    super(  Global.shapeFactory().ffImg ,
            Global.shapeFactory().ffImgSel ,
            new Int2D[]{new Int2D(  0, 0 ),
                        new Int2D( 38, 0 ),
                        new Int2D( 38, 38 ),
                        new Int2D(  0, 38 ) },
            new Int2D( 0, 0 ),
            new Int2D( 0, 0 )
            );

    }
}
