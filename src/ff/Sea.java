package ff;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import kutil.core.Int2D;
import kutil.core.Log;

public class Sea {

    public static void main( String[] args ){

        Log.it("Hello world!");

        String[] seaStrs = {
             "               I             ",
             "             $$I$$           ",
             "              $I$            ",
             "              $I$            ",
             "              $I$     $$   $$",
             "$$$$$     $$$$$I$   $$$$$$$$$",
             "$$$$$$$$$$$$$$$I$$$$$$$$$$$$$",
             "$$$$$$$$$$$$$$$I$$$$$$$$$$$$$",
             "$$$$$$$$$$$     $$$$$$$$$$$$$",
             "$$$$$$$           $$$$$$$$$$$",
             "$$$$                 $$$$$$$$",
             "$$$                    $$$$$$",
             "$$$                    $$$$$$",
             "$$$                     $$$$$",
             "$$$                      $$$$",
             "$$$                        $$",
             "$$$                          ",
             "$$$    a~~~        ####c     ",
             "$$$    addd bbbbbbb####c     ",
             "$$$$   aaaa  b   b  cccc     ",
             "$$$$$  a  a  b   b  c  c  $$$",
             "$$$$$$$a  a  b   b  c  c $$$$",
             "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",
             "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",
             "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",
             "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$",
             "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
        };

        //Log.it( charsIn(seaStrs) );

        Sea sea = new Sea(seaStrs);

        while(sea.isMoving()){
            Log.it(sea);
            sea.fallStep();
        }
        Log.it( sea );


        //Ob ob = new Ob('a',seaStrs );
    }

    public enum GameStatus { Normal , GameOver , YouWin }

    

    Map<Int2D, Ob> posMap;// PosMap
    Set<Ob> obSet; // ObMap
    Set<Ob> moving;// Moving
    Int2D rec; // Rectangle
    List<Ob> fishes; // Fishes
    GameStatus status;

    public Sea( String[] seaStrs ){

        rec = recFromStrs(seaStrs);

        obSet  = new HashSet<Ob>();
        posMap = new HashMap<Int2D, Ob>();
        fishes = new LinkedList<Ob>();
        status = GameStatus.Normal;

        for( char ch : charsIn(seaStrs) ){
            Ob ob = new Ob(ch, seaStrs);
            obSet.add( ob );
            for( Int2D pos : ob.getPoses() ) {
                posMap.put( pos , ob );
            }
            if( ob.isFish() ){
                fishes.add(ob);
                if( ob.isDead() ){
                    status = GameStatus.GameOver ;
                }
            }
        }

        moving = mkMoving();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        int rows = rec.getY();
        int cols = rec.getX();

        for( int i = 0 ; i < rows ; i++ ){
            for( int j = 0 ; j < cols ; j++ ){
                Ob ob = posMap.get( new Int2D(j,i) );
                char px = ob == null ? ' ' : ob.getPx();
                sb.append(px);
                sb.append(' ');
            }
            sb.append('\n');
        }

        sb.append( "Moving : " );

        for( Ob o : moving ){
            sb.append( o.getPx() );
            sb.append(' ');
        }

        return sb.toString();
    }

    private Set<Ob> mkMoving( ){
        Set<Ob> wallsAndFishes  = new HashSet<Ob>();
        Set<Ob> ret             = new HashSet<Ob>();

        for( Ob o : obSet ){
            if( o.isFish() || o.isWall() ) wallsAndFishes.add(o);
            if( o.isNotDeleted()         ) ret           .add(o);
        }

        Set<Ob> fixeds = new HashSet<Ob>();

        for( Ob wof : wallsAndFishes ){
            fixeds.addAll( haDeNeighbors( Ob.Dir.UP , wof ) );
        }

        ret.removeAll(fixeds);

        return ret;
    }

    public boolean isMoving(){
        return ! moving.isEmpty();
    }

    private void fallStep( ){

        for( Ob ob : moving ){
            move( Ob.Dir.DOWN , ob );
        }

        Set<Ob> stoppeds = updateMoving();

        
    }

    private Set<Ob> getKilledFishes( Set<Ob> stoppeds ){

        Set<Ob> ret = new HashSet<Ob>();

        for( Ob stopped : stoppeds ){
            Set<Ob> obs =  haDeNeighbors(Ob.Dir.DOWN, stopped) ;
            if( separateWalls(obs).isEmpty() ){
                ret.addAll( separateFishes(obs) );
            }
        }

        return ret;
    }

    private static Set<Ob> separateFishes( Set<Ob> obs ){
        Set<Ob> ret = new HashSet<Ob>();
        for( Ob ob : obs ){
            if(ob.isFish()) ret.add(ob);
        }
        return ret;
    }

    private static Set<Ob> separateWalls( Set<Ob> obs ){
        Set<Ob> ret = new HashSet<Ob>();
        for( Ob ob : obs ){
            if(ob.isWall()) ret.add(ob);
        }
        return ret;
    }

    private Set<Ob> updateMoving( ){

        Set<Ob> stoppeds = new HashSet<Ob>();
        boolean hotovo ;

        do{
            hotovo = true;
            Set<Ob> toRemove = new HashSet<Ob>();

            for( Ob mov : moving ){
                if( isFixedNow( mov , moving ) ){
                    hotovo = false;
                    toRemove.add(mov);
                    stoppeds.add(mov);
                }
            }

            moving.removeAll(toRemove);

        }while( !hotovo );
        
        return stoppeds;
    }

    private boolean isFixedNow( Ob ob , Set<Ob> movs ){

        for( Ob neib : neighbors(Ob.Dir.DOWN, ob) ){
            if( ! movs.contains(neib) ){
                return true ;
            }
        }

        return false;
    }

    private void move( Ob.Dir dir , Ob ob){
        
        List<Int2D> posesToInsert = ob.getSigPoses(dir);
        ob.move(dir);
        List<Int2D> posesToDelete = ob.getSigPoses( Ob.rot180(dir) );

        for( Int2D posToDel : posesToDelete ){
            Ob there = posMap.get(posToDel);
            if ( there != null && ob.equals(there) ){
                posMap.remove( posToDel );
            }
        }

        for( Int2D posToIns : posesToInsert ){
            posMap.put(posToIns, ob);
        }
    }

    private Set<Ob> haDeNeighbors ( Ob.Dir dir , Ob ob ){
        Set<Ob> acc = new HashSet<Ob>();

        Stack<Ob> stack = new Stack<Ob>();
        stack.push(ob);

        boolean firstLoop = true;

        while( ! stack.empty() ){
            Ob top = stack.pop();
            Set<Ob> childs = ( top.isFish() || top.isWall() ) && !firstLoop
                           ? new HashSet<Ob>()
                           : neighbors(dir, top);

            childs.removeAll( acc );
            
            for( Ob child : childs ){
                stack.add(child);
            }
            
            acc.add(top);

            firstLoop = false ;
        }

        return acc;
    }

    private Set<Ob> neighbors ( Ob.Dir dir , Ob ob ){
        Set<Ob> ret = new HashSet<Ob>();
        List<Int2D> sigs = ob.getSigPoses(dir);

        for( Int2D pos : sigs ){
            Ob neigbor = posMap.get(pos);
            if( neigbor != null ){
                ret.add(neigbor);
            }
        }
        
        return ret;
    }

    private static Int2D recFromStrs( String[] strs ){

        int maxLen = Integer.MIN_VALUE;

        for( String str : strs ){
            int len = str.length();
            if ( len > maxLen ) maxLen = len;
        }

        return new Int2D( maxLen ,strs.length);
    }

    private static List<Character> charsIn(String[] strs ){

        StringBuilder sb = new StringBuilder();
        for( String str : strs ){
            sb.append(str);
        }

        Set<Character> charSet = new HashSet<Character>();

        for( char ch : sb.toString().toCharArray() ){
            charSet.add(ch);
        }

        charSet.remove(' ');

        List<Character> ret = new LinkedList<Character>();

        for( char ch : charSet ){
            ret.add(ch);
        }

        Collections.sort(ret);
        return ret;

    }

    private class NResult{
        private Set<Ob> stds;
        private Set<Ob> steels;
        private Set<Ob> walls;
        private Set<Ob> fishes;
        private Set<Ob> all;

        public Set<Ob> getFishes(){ return fishes; }
        public Set<Ob> getWalls (){ return walls ; }

        private void init(){
            stds   = new HashSet<Ob>();
            steels = new HashSet<Ob>();
            walls  = new HashSet<Ob>();
            fishes = new HashSet<Ob>();
            all    = new HashSet<Ob>();
        }

        public NResult(){
            init();
        }

        public NResult( Set<Ob> obs ){
            init();
            for(Ob ob : obs ){
                add(ob);
            }
        }

        public final void add(Ob ob){

            all.add(ob);

            switch( ob.getType() ){
                case BIG_FISH:
                case SMALL_FISH:
                case DEAD_FISH:
                   fishes.add(ob);  break;
                case STD:
                    stds.add(ob);   break;
                case STEEL:
                    steels.add(ob); break;
                case WALL:
                    walls.add(ob);  break;
            }
        }
    }

}
