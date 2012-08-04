package ff;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import kutil.core.Int2D;
import kutil.core.Log;

public class Ob {

    public static void main( String[] args ){
        Log.it("Hello world!");

        String[] seaStrs = {
         "      ",
         " a a  ",
         " aaa  ",
         "   a  "
        };

        Ob ob = new Ob('a',seaStrs );
    }

    public enum Type { STD , STEEL , WALL , SMALL_FISH , BIG_FISH , DEAD_FISH }

    public enum Dir  {
        UP(0) , DOWN(1) , LEFT(2) , RIGHT(3) ;
        private final int i; Dir(int i_){i=i_;}
        public int i(){return i;}
    }

    private Type                type    ;
    private List<Int2D>         shape   ;
    private Sigs[]              signifs ;
    private Int2D               pos1    ; // upper left corner
    private Int2D               pos2    ; // lower right corner
    private char                px      ;

    private class Sigs{
        public List<Int2D> sigs;
        public Sigs(List<Int2D> sigs_){sigs=sigs_;}
        @Override
        public String toString() {return sigs.toString();}
    }

    public Ob( char pix , String[] seaStrs ){

        px      = pix;
        type    = typeByPx(pix);
        
        shapeAndPosFromStrs( seaStrs );

        signifs = mkSignifs(shape);

        /*
        Log.it( pos1.toString() );
        Log.it( pos2.toString() );
        Log.it( shape.toString() );
        Log.it( signifs[0].toString() );
        Log.it( signifs[1].toString() );
        Log.it( signifs[2].toString() );
        Log.it( signifs[3].toString() );
        */

    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Ob ){
            Ob ob = (Ob) obj;
            return ob.px == px ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.px;
        return hash;
    }


    public void move(Dir dir){
        Int2D delta = dirDelta(dir);
        pos1 = pos1.plus( delta );
        pos2 = pos2.plus( delta );
    }

    public List<Int2D> getPoses(){
        List<Int2D> ret = new LinkedList<Int2D>();
        for( Int2D p : shape ){
            ret.add(p.plus(pos1));
        }
        return ret;
    }

    public List<Int2D> getSigPoses( Dir dir ){
        List<Int2D> ret = new LinkedList<Int2D>();
        for( Int2D pos : signifs[dir.i()].sigs ){
            ret.add(pos.plus(pos1));
        }
        return ret;
    }

    public Type getType(){
        return type;
    }

    public char getPx(){
        if( isDead() ) return '+';
        return px;
    }

    public void kill(){
        if( isFish() ){
            type = Type.DEAD_FISH;
        }
    }

    public boolean isWall(){
        return type == Type.WALL ;
    }

    public boolean isStd(){
        return type == Type.STD ;
    }

    public boolean isSteel(){
        return type == Type.STEEL ;
    }

    public boolean isBlock(){
        return isStd() || isSteel();
    }

    public boolean isNotDeleted(){
        return pos1 != null ;
    }

    public void nullThePos(){
        pos1 = null;
        pos2 = null;
    }


    public boolean isFish(){
        return type == Type.BIG_FISH   ||
               type == Type.SMALL_FISH ||
               type == Type.DEAD_FISH ;
    }

    public boolean isDead(){
        return type == Type.DEAD_FISH ;
    }

    public boolean isBigFish(){
        return type == Type.BIG_FISH ;
    }

    public boolean isSmallFish(){
        return type == Type.SMALL_FISH ;
    }

    private Sigs[] mkSignifs( List<Int2D> sh ){
        Sigs[] signifs_ = {
            mkSigs(Dir.UP, sh),
            mkSigs(Dir.DOWN, sh),
            mkSigs(Dir.LEFT, sh),
            mkSigs(Dir.RIGHT, sh) };
        return signifs_;
    }

    private static int relevantPart( Dir dir , Int2D p ){
        switch ( dir ) {
            case UP : case DOWN : return p.getY();
            default             : return p.getX();
        }
    }

    private Sigs mkSigs( Dir dir , List<Int2D> sh ){

        List<Int2D> ret = new LinkedList<Int2D>();
        List<List<Int2D>> layers = sepLayers(dir, sh );

        List<Int2D> last = layers.get(0);
        for( Int2D p : last ){
            ret.add( p.plus( dirDelta(dir) ) );
        }
        //ret.addAll(last);

        for( int i = 1 ; i < layers.size() ; i++ ){
            List<Int2D> adds = new LinkedList<Int2D>();
            for( Int2D p : layers.get(i) ){
                Int2D checkFor = p.plus( dirDelta(dir) );
                if( ! last.contains(checkFor) ){
                    adds.add( checkFor );
                }
            }
            ret.addAll(adds);
            last = layers.get(i);
        }

        return new Sigs(ret);
    }

    private List<List<Int2D>> sepLayers( Dir dir , List<Int2D> lst ){

        int i1 = relevantPart(dir, pos1 );
        int i2 = relevantPart(dir, pos2 );
        int size = i2 - i1 + 1;

        List<List<Int2D>> ret = new ArrayList<List<Int2D>>(size);
        for( int k = 0 ; k < size ; k++ ) {
            ret.add(k, new LinkedList<Int2D>());
        }

        for( Int2D p : lst ){
            int i = relevantPart(dir, p) ;
            if( dir == Dir.DOWN || dir == Dir.RIGHT ){
                i = size - i - 1;
            }
            ret.get(i).add(p);
        }

        return ret;
    }

    public static Dir rot180( Dir dir ){
        switch(dir) {
            case UP    : return Dir.DOWN;
            case DOWN  : return Dir.UP;
            case LEFT  : return Dir.RIGHT;
            default    : return Dir.LEFT;
        }
    }

    public static Int2D dirDelta( Dir dir ){
        switch (dir) {
            case UP    : return new Int2D( 0,-1);
            case DOWN  : return new Int2D( 0, 1);
            case LEFT  : return new Int2D(-1, 0);
            default    : return new Int2D( 1, 0);
        }
    }

    private void shapeAndPosFromStrs( String[] seaStrs ){
        List<Int2D> ret = new LinkedList<Int2D>();
        int numRows = seaStrs.length;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for ( int i = 0 ; i < numRows ; i++ ){
            for( int j = 0 ; j < seaStrs[i].length() ; j++ ){
                char ch = seaStrs[i].charAt(j);
                if ( ch == px ){
                    int x = j;
                    int y = i;
                    if(x < minX) minX = x;
                    if(y < minY) minY = y;

                    if(x > maxX) maxX = x;
                    if(y > maxY) maxY = y;

                    ret.add(new Int2D(x, y));
                }
            }
        }

        for( Int2D p : ret ){
            p.adjustX(-minX);
            p.adjustY(-minY);
        }

        shape = ret;
        pos1  = new Int2D(minX,minY);
        pos2  = new Int2D(maxX,maxY);
    }

    public static Type typeByPx( char px ){
        switch( px ) {
            case '$' : return Type.WALL ;
            case '~' : return Type.SMALL_FISH ;
            case '#' : return Type.BIG_FISH ;
            case '+' : return Type.DEAD_FISH ;
            default  : 
                if ( Character.isUpperCase(px) )
                     return Type.STEEL ;
                else return Type.STD ;
        }
    }
}

