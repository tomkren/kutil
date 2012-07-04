package kutil.kobjects;

import kutil.core.Int2D;
import kutil.items.StringItem;
import kutil.core.KAtts;
import kutil.items.IntegerItem;
import kutil.shapes.CommentShape;

/**
 * Objekt virtuálního světa sloužící jako komentář/průvodce tímto světem.
 * Vypadá jako inženýr houbička a má u pusy bublinu s komentářem.
 * @author Tomáš Křen
 */
public class Comment extends Basic {

    private StringItem  val;
    private IntegerItem actualPage;

    /**
     * Vytvoří Comment podle kAtts.
     * Přidává položku val, reprezentující text komentáře.
     */
    public Comment( KAtts kAtts ){
        super( kAtts );
        val        = items().addString(kAtts , "val" , null );
        actualPage = items().addInteger(kAtts , "actualPage", 0);
        create();
    }

    /**
     * Vytvoří Comment podle řetězce reprezentujícího text komentáře.
     */
    public Comment( String str ){
        super();
        val        = items().addString(  "val"        , str );
        actualPage = items().addInteger( "actualPage" , 0   );
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Comment( Comment c ){
        super(c);
        val        = items().addString(  "val"        , c.val.get() );
        actualPage = items().addInteger( "actualPage" , c.actualPage.get() );
        create();
    }

    @Override
    public KObject copy() {
        return new Comment( this );
    }


    private Page[] pages;
    


    private void create(){
        setType( "comment" );
        setShape( new CommentShape(this) );

        resetVal( val.get() );
    }

    private void resetVal( String str ){

        val.set(str);

        if( isSilent() ){
            return;
        }

        String[] ps = val.get().split("##");

        pages = new Page[ps.length];
        for( int i = 0 ; i < ps.length ; i++ ){
            pages[i] = new Page( ps[i] , i+1 , ps.length );
        }

    }

    @Override
    public void click(Int2D clickPos) {
        super.click(clickPos);
        actualPage.set( (actualPage.get()+1)%pages.length );
    }



    /**
     * Vrací i-tou řádku textu komentáře.
     */
    public String line(int i){
        return pages[actualPage.get()].line(i);
    }

    /**
     * Vrací délku nejdelší řádky.
     */
    public int longestLine(){
        return pages[actualPage.get()].longestLine();
    }

    /**
     * Vrací počet řádek.
     */
    public int numLines(){
        return pages[actualPage.get()].numLines();
    }

    public boolean isSilent(){
        return val.get()==null;
    }

}

class Page {

    private String[] lines;
    private int      longestLine;
    private int      pageNumber;
    private int      numPages ;

    public Page( String str , int pageNumber , int numPages ){

        lines = str.split("#");
        this.pageNumber = pageNumber;
        this.numPages   = numPages;
        
        longestLine = 0;

        for( int i = 0 ; i < lines.length ; i++ ){
            lines[i] = lines[i].trim();
            if( longestLine < lines[i].length() ) longestLine = lines[i].length() ;
        }
    }

    public String line( int i ){
        if( i == lines.length   ) return "" ;
        if( i == lines.length+1 ) return pageNumber + "/" + numPages ;

        return lines[i];
    }
    public int numLines(){
        return (numPages > 1 ? lines.length+2 : lines.length );
    }
    public int longestLine(){
        return longestLine;
    }

}
