package kutil.kobjects;

import kutil.core.Global;
import kutil.core.KAtts;

/**
 * Speciální funkce, která v pravidelném intervalu vrací výstup.
 * Přesněji: jako vstup přijímá číslo, potom každou vteřinu vrátí
 * číslo počínaje číslem 1 a konče číslem přijatým na vstupu.
 * @author Tomáš Křen
 */
public class Clock extends Function {

    /**
     * Vytvoří Clock podle KAtts
     */
    public  Clock(KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public  Clock(  Clock s ){
        super( s );
        create();
    }


    @Override
    public KObject copy() {
        return new  Clock(this);
    }

    private void create(){
        setType("clock");
        resetVal("clock");
    }

    private int iterations;
    private int i;
    private long lastCheck  ;
    private boolean isRunning = false;

    @Override
    public void step() {
        super.step();

        if( Global.rucksack().isSimulationRunning() && isRunning ){
            if( time() - lastCheck > 1000 ){
                lastCheck = time();
                handleInput( new Num(i)  , 0);
                i++;
                if( i > iterations ){
                    isRunning = false;
                }
            }
        }
    }

    /**
     * Metoda volaná implementací funkce Clock.
     * Zajišťuje správné provázání obou věcí.
     */
    public KObject getResponse( KObject o ){

        if( isRunning ){
            return o;

        }else{

            if( o instanceof Num ){
                isRunning = true;
                lastCheck = 0;
                iterations = ((Num)o).get();
                i = 1;

                if( iterations < 1 ) isRunning = false;
            }

            return null;
        }
    }

    private static long time(){
        return System.currentTimeMillis();
    }



}
