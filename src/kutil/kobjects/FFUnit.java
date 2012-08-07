package kutil.kobjects;

import ff.MotionCmd;
import ff.MotionCommander;
import ff.Sea;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.core.Log;

/**
 *
 * @author Tomáš Křen
 */
public class FFUnit extends Basic{
   
    private Map<Character,KObject>  pxToKOb;
    private Map<String ,Character>  oidToPx;
    private char                    nextPx;
    private int                     step;
    private MotionCommander         mc;
    private List<MotionCmd>         nowRunningCmds;

    private void create(){
        setType( "ffunit" );

        mc              = new Sea(new Int2D(100, 100) ); // new TestCommander();
        nowRunningCmds  = new LinkedList<MotionCmd>();
        pxToKOb         = new HashMap< Character , KObject >();
        oidToPx         = new HashMap< String  , Character >();
        nextPx          = 'a';
        step            = 0  ;
    }

    public void addKObject( KObject o ){
        
        String ffType = ( (Basic) o).getFFType();
        
        char px;
        
        if( "wall".equals(ffType) ){
            px = '$';
        }else{
            px = nextPx;
            nextPx++;
        }
        
        pxToKOb.put( px , o );
        oidToPx.put( o.id() , px);
        mc.addBlock(px, getFFPoses(o) );

        Log.it("[Added kobject] | px: '"+ nextPx + "' | xml: "+ o.toXml() );
    }

    public void removeKObject( KObject o ){

        char px = oidToPx.get(o.id());

        oidToPx.remove(o.id());
        pxToKOb.remove(px);

        mc.removeBlock( px );
        
        List<MotionCmd> toRemove = new LinkedList<MotionCmd>();
        
        for( MotionCmd cmd : nowRunningCmds ){
            if( px == cmd.getPx() ){
                toRemove.add(cmd);
            }
        }
        
        nowRunningCmds.removeAll(toRemove);
    }

    private static Set<Int2D> getFFPoses( KObject o ){
        Set<Int2D> poses = new HashSet<Int2D>();
        poses.add( toFFPos(o.pos()) );
        return poses;
    }

    //private static final int   TICK  = 50;
    private static final int   SIDE  = 16;
    //private static final Int2D DELTA = new Int2D(0, SIDE);

    @Override
    public void step() {
        super.step();
        
        if( Global.rucksack().isSimulationRunning() ){

            if( step % (SIDE+1) == 0 ){
                nowRunningCmds = mc.getNewCmds();
            } else {
                for( MotionCmd cmd : nowRunningCmds ){
                    KObject o = pxToKOb.get( cmd.getPx() ) ;
                    o.setPos( o.pos().plus( cmd.getDelta() ) );
                }
            }

            step++;
        }
    }

    private static Int2D toFFPos(Int2D pos){
        return new Int2D( pos.getX() / SIDE  , pos.getY() / SIDE );
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
}


class TestCommander implements MotionCommander{

    private Set<Character> chs;

    public TestCommander(){
        chs = new HashSet<Character>();
    }

    public void addBlock(char px, Set<Int2D> poses) {
       chs.add(px);
    }

    public void removeBlock(char px) {
        chs.remove(px);
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


    /*
    private class Block{

        private KObject kObject;
        private char px;

        public Block( char pix , KObject o ){
            px      = pix;
            kObject = o;
        }

        public KObject getKObject(){
            return kObject;
        }
    } */
