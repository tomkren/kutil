package kutil.kobjects;

import kutil.items.DoubleItem;
import kutil.items.IntegerItem;
import kutil.core.KAtts;

/**
 * Objekt virtuálního světa určený pro to být na vrcholu hierarchie nad nímž je už jen Scheduler
 * (který je vrškem hierarche spíš metaforicky, protože scheduler není KObject, pouze řídí běh).
 * Určuje UPS a případně konečný počet iterací.
 * @author Tomáš Křen
 */
public class Time extends Basic {

    private DoubleItem  ups        ; // updates per second
    private IntegerItem iterations ; // kolik se má udělat ještě iterací

    private long        period     ; // milliseconds
    
    public Time(KAtts kAtts ){
        super( kAtts );

        setType("time");

        ups        = items().addDouble ( kAtts , "ups"        , 80.0 );
        iterations = items().addInteger( kAtts , "iterations" , null ); //null znamená nekonečno

        period = Math.round( (double)( 1000.0 / ups.get() ) ) ;

        
    }

    public double getUPS(){
        return ups.get();
    }

    /**
     * Vrací periodu v milisekundách.
     */
    public long getPeriod(){
        return period;
    }

    public long getIterations(){
        return iterations.get();
    }

    public void decrementIterations(){
        if( iterations.get() == null ) return;
        iterations.set( iterations.get() - 1 );
    }

    public boolean isFinished(){

        if     ( iterations.get() == null ) return false;
        return ( iterations.get() <= 0    );

    }


}
