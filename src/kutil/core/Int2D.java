package kutil.core;

import java.awt.event.MouseEvent;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 * Třída reprezentující dvojici integeru, používaná v programu pro reprezentaci
 * souřadnic a vektorů.
 * @author Tomáš Křen
 */
public class Int2D {

    private int x, y;

    /**
     * Vytvoří nový Int2D na základě souřadnic.
     * @param x horizontální souřadnice
     * @param y vertikální souřadnice
     */
    public Int2D( int x , int y ){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o){
        if( o instanceof Int2D ){
            Int2D pos = (Int2D) o;
            return ( pos.x == x && pos.y == y );
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        return hash;
    }

    /**
     * Kopírovací konstruktor
     * @param i vzor
     */
    public Int2D( Int2D i ){
        x = i.x;
        y = i.y;
    }

    /**
     * Vrací kopii.
     */
    public Int2D copy(){
        return new Int2D(x, y);
    }

    public static final Int2D zero = new Int2D(0,0);

    /**
     * Vrací nový Int2D se sořadnicemi v počátku.
     */
    public static Int2D zero(){
        return new Int2D(0,0);
    }

    /**
     * Překládá textovou reprezentaci na Int2D.
     * Tato reprezentace je jednoduše dvě čísla oddělená bílím znakem.
     * @param str kód
     * @return kódovaná hodnota
     */
    public static Int2D parseInt2D( String str ){

        String[] arr = str.trim().split("\\s+", 2);

        if( arr.length != 2 ) return new Int2D(0,0);

        return new Int2D( Integer.parseInt(arr[0]) , Integer.parseInt(arr[1]) );
    }

    /**
     * Vrací x-ovou složku.
     */
    public int getX(){
        return x;
    }

    /**
     * Vrací y-ovou složku.
     */
    public int getY(){
        return y;
    }

    /**
     * Vrací absolutní hodnotu vektoru.(vzdálenost od počátku)
     */
    public double abs(){
        return Math.sqrt( x*x + y*y );
    }

    /**
     * Přenastavuje x-ovou složku.
     * @param newX nova x-ová složka
     */
    public void setX( int newX ){
        x = newX;
    }

    /**
     * Přenastavuje y-ovou složku.
     * @param newX nova y-ová složka
     */
    public void setY( int newY ){
        y = newY;
    }

    /**
     * Přenastavuje hodnotu na základě ROVector2f (typ používaný fyzikální simulací).
     * @param bodyPos hodnota v ROVector2f
     */
    public void set( ROVector2f bodyPos ){
        x = (int) bodyPos.getX();
        y = (int) bodyPos.getY();
    }

    /**
     * Přičítá k x-ové složce.
     * @param dx změna x-ové složky
     */
    public void adjustX( int dx ){
        x += dx;
    }

    /**
     * Přičítá k y-ové složce.
     * @param dy změna y-ové složky
     */
    public void adjustY( int dy ){
        y += dy;
    }

    /**
     * Vrací nový Int2D jehož složky jsou vynásobeny hodnotou q.
     * @param q kolikrát se zvětší složky
     * @return nový Int2D
     */
    public Int2D times( double q ){

        Int2D ret = new Int2D(x, y);
        ret.x = (int) Math.round(q * x);
        ret.y = (int) Math.round(q * y);
        return ret;
    }

    /**
     * Vrací nový Int2D vzniklý sečtením.
     * @param i sčítanec
     * @return součet
     */
    public Int2D plus( Int2D i ){

        Int2D ret = new Int2D(x,y);
        ret.x    += i.x;
        ret.y    += i.y;

        return ret;
    }

    /**
     * Vrací nový Int2D vzniklý odečtením od tohoto Int2D.
     * @param i menšitel
     * @return rozdíl
     */
    public Int2D minus( Int2D i ){

        Int2D ret = new Int2D(x,y);
        ret.x    -= i.x;
        ret.y    -= i.y;

        return ret;
    }

    /**
     * Vrací nový Int2D se složkami s opačným znamínkem než má původní.
     * @return
     */
    public Int2D negate(){
        return new Int2D(-x, -y);
    }


    /**
     * Převod do textového kódu.
     * @return
     */
    @Override
    public String toString() {
        return x + " " + y;
    }

    /**
     * Vrací zda byl zasažen obdélník kliknutím.
     * @param clickPos  pozice kliknutí
     * @param cornerPos levý horní roh obdélníku
     * @param width šířka obdélníku
     * @param height výška obdélníku
     * @return zda byl zasažen
     */
    public static boolean rectangeHit( Int2D clickPos , Int2D cornerPos , int width , int height ){

        if( clickPos.getX() < cornerPos.getX()          ) return false;
        if( clickPos.getY() < cornerPos.getY()          ) return false;
        if( clickPos.getX() > cornerPos.getX() + width  ) return false;
        if( clickPos.getY() > cornerPos.getY() + height ) return false;

        return true;
    }

    /**
     * Vrací zda byl zasažen obdélník kliknutím.
     * @param e mouseEvent vzniklý při kliknutí
     * @param pos levý horní roh obdélníku
     * @param width šířka obdélníku
     * @param height výška obdélníku
     * @return zda byl zasažen
     */
    public static boolean rectangeHit( MouseEvent e , Int2D pos , int width , int height ){

        if( e.getX() < pos.getX()          ) return false;
        if( e.getY() < pos.getY()          ) return false;
        if( e.getX() > pos.getX() + width  ) return false;
        if( e.getY() > pos.getY() + height ) return false;

        return true;
    }

    /**
     * Převádí pole Int2D do pole ROVector2f (typ použivaný fyz. simulátorem)
     * @param vs pole hodnot Int2D
     * @return pole hodnot ROVector2f
     */
    public static ROVector2f[] toROVector2f( Int2D[] vs ){

        ROVector2f[] ret = new ROVector2f[vs.length] ;

        for( int i=0 ; i< vs.length ; i++ ){
            ret[i] = new Vector2f( vs[i].getX(), vs[i].getY() );
        }

        return ret;
    }
    /**
     * Převádí Int2D doROVector2f (typ použivaný fyz. simulátorem)
     * @param vs hodnota Int2D
     * @return hodnota ROVector2f
     */
    public static Vector2f toROVector2f( Int2D int2D ){
        return new Vector2f( int2D.getX(), int2D.getY() );
    }

    /**
     * Převádí z ROVector2f  (typ použivaný fyz. simulátorem) do Int2D.
     * @param rov hodnota ROVector2f
     * @return hodnota v Int2D
     */
    public static Int2D fromROVector2f( ROVector2f rov ){
        return new Int2D( (int) rov.getX() , (int) rov.getY() );
    }


}
