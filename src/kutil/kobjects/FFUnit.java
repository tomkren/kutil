package kutil.kobjects;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
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
    } 
    
    private Map<Character,Block> blocks;
    private char nextPx;
    private int  step;

    public void addKObject( KObject o ){
        blocks.put( nextPx , new Block(nextPx, o) );
        Log.it("[Added kobject] | px: '"+ nextPx + "' | xml: "+ o.toXml() );
        
        nextPx ++;
        
    }

    private static final int   TICK  = 50;
    private static final Int2D DELTA = new Int2D(0, 16);
    
    
    @Override
    public void step() {
        super.step();
        
        if( Global.rucksack().isSimulationRunning() ){
        
            if( step % TICK == 0 ){

                for( Block b : blocks.values() ){
                    KObject o = b.getKObject();
                    o.setPos( o.pos().plus( DELTA ) );
                }

            }

            step++;
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

    private void create(){
        setType( "ffunit" );
        
        blocks = new HashMap< Character , Block>();
        nextPx = 'a';
        step = 0;
    }

    @Override
    public KObject copy() {
        return new FFUnit(this);
    }
    
    
    

    
   
    
    
}
