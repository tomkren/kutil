package kutil.kobjects;

import kutil.items.StringItem;
import kutil.core.KAtts;
import kutil.shapes.SymbolShape;

/**
 * Objekt virtuálního světa reprezentující symbol (textový řetězec bez mezer).
 * @author Tomáš Křen
 */
public class Symbol extends Basic {

    private StringItem val;

    public Symbol( KAtts kAtts ){
        super( kAtts );
        val = items().addString(kAtts , "val" , "Mu!" );
        create();
    }

    public Symbol( String str ){
        super();
        val = items().addString( "val" , str );
        create();
    }

    public Symbol( Symbol s ){
        super(s);
        val = items().addString( "val" , s.val.get() );
        create();
    }

    @Override
    public String toKisp() {
        return val.get();
    }

    @Override
    public KObject copy() {
        return new Symbol( this );
    }

    private void create(){
        setType( "symbol" );
        setPhysical(true);
        setAttached(false);
        setShape( new SymbolShape(this) );
    }


    public String get(){
        return val.get();
    }

    public int getWidth(){
        return 7*val.get().length()+16;
    }


}