package ff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Sea implements MotionCommander{

    
    
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


        sea.fallSteps_();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String str = null;
            while( true ) {

                


                System.out.print("$ ");
                str = in.readLine();

                if( str.equals("") ) break;

                //Log.it("LOG: " + str);

                for( char ch : str.toCharArray() ){
                    sea.fishStep_(ch);
                }

            } 

        } catch (IOException e) {
        }

        //Ob ob = new Ob('a',seaStrs );
    }

    private void fishStep_(char ch){
        fishStep( toCmd(ch) );
        fallSteps_();
    }

    private void fallSteps_(){

        while(isMoving()){
            Log.it(this);
            fallStep();
        }
        Log.it( this );
    }

    public enum GameStatus { Normal , GameOver , YouWin }

    public enum Cmd { U , D , L , R , S }
    

    Map<Int2D, Ob>  posMap;
    Set<Ob>         obSet;
    Set<Ob>         moving;
    Int2D           rec;
    LinkedList<Ob>  fishes;
    GameStatus      status;

    public void addBlock(char px, Set<Int2D> poses) {
        Ob ob = new Ob(px, poses);
        
        // todo   : vyřešit když to přidávam někam kde už něco je
        
        obSet.add(ob);
        
        for( Int2D pos : ob.getPoses() ){
            posMap.put( pos , ob);
        }
        
        moving = mkMoving();

        
        // .....
    }

    public void removeBlock(char px) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<MotionCmd> getNewCmds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
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
                fishes.addFirst(ob);
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

        if( status==GameStatus.YouWin   ) sb.append("Y O U   W I N   !!!");
        if( status==GameStatus.GameOver ) sb.append("G A M E   O V E R   !!!");


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

        sb.append( "\nActual fish : " );
        if( ! fishes.isEmpty() ) {
            sb.append( fishes.getFirst().getPx() );
        }

        return sb.toString();
    }

    private Set<Ob> mkMoving( ){
        Set<Ob> wallsAndFishes  = new HashSet<Ob>();
        Set<Ob> ret             = new HashSet<Ob>();

        for( Ob o : obSet ){
            if( o.isNotDeleted() ){
                ret.add(o);
                if( o.isFish() || o.isWall() ){
                    wallsAndFishes.add(o);
                }
            }
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

    public void fishStep( Cmd cmd ){
        if( cmd == null ) return;

        Ob.Dir dir = toDir(cmd);
        if( dir == null ){
            Ob firstFish = fishes.removeFirst();
            fishes.addLast(firstFish);
        } else {

            Ob actFish = fishes.getFirst();

            Set<Ob> pusheds = getMoveables(dir, actFish );
            if( pusheds != null ){

                for( Ob o : pusheds ){
                    move(dir,o);
                }
                moving = mkMoving(); // TODO  U N E F F E C T I V E

                // update killing
                if( dir==Ob.Dir.LEFT || dir==Ob.Dir.RIGHT ){
                    for( Ob fish : fishes ){
                        if( isSpineKilled(pusheds, fish) ||
                            isSteelKilled(fish) ){
                            fish.kill();
                            status = GameStatus.GameOver;
                        }
                    }
                }

                // update winning
                if( status == GameStatus.Normal && isGoal(actFish)  ){
                    for( Int2D pos : actFish.getPoses() ){
                        posMap.remove(pos);
                    }
                    actFish.nullThePos();
                    fishes.removeFirst();

                    if( fishes.isEmpty() ){
                        status = GameStatus.YouWin ;
                    }
                }
                
            }
        }
    }

    private boolean isGoal( Ob fish ){
        for( Int2D fishPos : fish.getPoses() ){
            int x = fishPos.getX();
            int y = fishPos.getY();

            if( x <= 0 ||
                y <= 0 ||
                x >= rec.getX() -1 ||
                y >= rec.getY() -1 ){
                return true;
            }
        }
        return false;
    }

    private boolean isSpineKilled( Set<Ob> pusheds , Ob fish ){
        for( Ob o : neighbors(Ob.Dir.UP, fish) ){
            if( o.isBlock()         &&
                pusheds.contains(o) &&
                ! isFixedByWall(o)     ){
                  return true;
            }
        }
        return false;
    }

    private boolean isSteelKilled( Ob fish ){
        if(fish.isBigFish()) return false;

        for( Ob o : haDeNeighbors(Ob.Dir.UP, fish) ){
            if( o.isSteel() && ! isFixedByWall(o) ){
                return true;
            }
        }
        return false;
    }

    private boolean isFixedByWall( Ob ob ){
        for( Ob o : haDeNeighbors(Ob.Dir.DOWN, ob)){
            if( o.isWall() ) return true;
        }
        return false;
    }

    private Set<Ob> getMoveables(Ob.Dir dir , Ob fish ){

        Set<Ob> xs = haDeNeighbors(dir, fish);

        for( Ob o : xs ){
            if( o.isWall()                     ) return null;
            if( o.isFish()  && !o.equals(fish) ) return null;
            if( o.isSteel() && o.isSmallFish() ) return null;
        }

        return xs;
    }

    public static Ob.Dir toDir(Cmd cmd){
        switch( cmd ){
            case U : return Ob.Dir.UP;
            case D : return Ob.Dir.DOWN;
            case L : return Ob.Dir.LEFT;
            case R : return Ob.Dir.RIGHT;
            default: return null;
        }
    }

    public static Cmd toCmd( char ch ){
        switch( ch ){
            case 'w': return Cmd.U;
            case 's': return Cmd.D;
            case 'a': return Cmd.L;
            case 'd': return Cmd.R;
            case ' ': return Cmd.S;
            default:  return null;
        }
    }

    public void fallStep( ){

        for( Ob ob : moving ){
            move( Ob.Dir.DOWN , ob );
        }

        Set<Ob> stoppeds = updateMoving();
        Set<Ob> killedFishes = getKilledFishes(stoppeds);

        if( ! killedFishes.isEmpty() ){
            status = GameStatus.GameOver;
        }

        for( Ob fish : killedFishes ){
            fish.kill();
        }
        
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
    
    private static Set<Ob> separateStdOrSteel( Set<Ob> obs ){
        Set<Ob> ret = new HashSet<Ob>();
        for( Ob ob : obs ){
            if(ob.isStd() || ob.isSteel() ) ret.add(ob);
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
