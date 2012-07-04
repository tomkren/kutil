package kutil.kobjects;

import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.shapes.OutShape;


/**
 * Objekt virtuálního světa zajištující převod "signálu", který může poslat funkci na objekt virtuálního světa.
 * Tento výstupní KObjekt pošle pod sebe (KObjekt z out vypadne).
 * @author Tomáš Křen
 */
public class Out extends Basic implements Inputable {


    private static final Int2D shift    = new Int2D(16,40);
    private static final Int2D arrowEnd = new Int2D(16,-1);


    public Out( KAtts kAtts ){
        super( kAtts );
        create();
    }
    
    public Out(Out o){
        super(o);
        create();
    }

    public Out(){
        super();
        create();
    }

    @Override
    public KObject copy() {
        return new Out(this);
    }

    private void create(){
        setType("out");
        setShape( new OutShape() );
    }

    public void handleInput( KObject input , int port ){
        if( input == null ) return;

        input.setPos( pos().plus( shift ) );
        parent().add(input);
        input.setParent( parent() );
    }

    public void handleOutsideInput( KObject input , int port ){
    }

    public Int2D getArrowEnd(int i) {
        return pos().plus(arrowEnd);
    }

    @Override
    public void click(Int2D clickPos) {
        if(topArrowBarHit(clickPos)){
            topArrowBar();
        }
        super.click(clickPos);
    }

    @Override
    public void drag(Int2D clickPos, Int2D delta,Frame f) {
        if(topArrowBarHit(clickPos)){
            topArrowBar();
        }
        super.drag(clickPos,delta, f);
    }

    private void topArrowBar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private boolean topArrowBarHit(Int2D clickPos){
        return clickPos.minus( pos() ).getY() < 9 ;
    }



}
