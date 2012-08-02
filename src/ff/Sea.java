package ff;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kutil.core.Int2D;
import kutil.core.Log;

public class Sea {

    public static void main( String[] args ){

        Log.it("Hello world!");

        String[] seaStrs = {
             "           $               ",
             "           $$O$$           ",
             "            $O$            ",
             "            $O$            ",
             "            $O$            ",
             "            $O$            ",
             "            $O$            ",
             "        $$$$$O$            ",
             "    $$$$$     $$$          ",
             " $$$$           $$$$       ",
             "$$                 $$$     ",
             "$                    $     ",
             "$                    $$    ",
             "$                     $$   ",
             "$                      $$$ ",
             "$                        $$",
             "$                          ",
             "$    a~~~        ####c     ",
             "$    addd bbbbbbb####c     ",
             "$$   aaaa  b   b  cccc     ",
             " $$  a  a  b   b  c  c   $$",
             "  $$$a  a  b   b  c  c  $$ ",
             "    $$$$$$$$$$$$$$$$$$$$$  "
        };

        Log.it( charsIn(seaStrs) );

        //Ob ob = new Ob('a',seaStrs );
    }

    Map<Int2D, Ob> posMap;// PosMap
    List<Ob> obList; // ObMap
    // Moving
    Int2D rec; // Rectangle
    // Fishes
    // GameStatus

    public Sea( String[] seaStrs ){

        rec = recFromStrs(seaStrs);

        obList = new LinkedList<Ob>();
        posMap = new HashMap<Int2D, Ob>();
        for( char ch : charsIn(seaStrs) ){
            Ob ob = new Ob(ch, seaStrs);
            obList.add( ob );
            for( Int2D pos : ob.getPoses() ) {
                posMap.put( pos , ob );
            }
        }

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

}
