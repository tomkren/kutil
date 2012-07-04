package kutil.core;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import kutil.kobjects.KObject;
import kutil.kobjects.Time;

/**
 * Scheduler je jednoduchý plánovač, který můžeme chápat jako kořen celé hierarchie KObjectů,
 * který volá u nejvyšších KObjectů z hierarchie metodu step().
 * Toto volání pak kaskádovitě projde celou hierarchií a má za důsledek všechny
 * akce provedené v tomto kroku běhu virtuálního světa.
 * Všechny akce pak jsou projevem jednotlivých KObjectů,
 * které mají možnost manipulovat i se strukturou hierarchie.
 * @author Tomáš Křen
 */
public class Scheduler {

    private PriorityQueue<CalendarEvent> calendar; // kalendář, podle kterého se plánuje
    private KAtts loaded; // celá hierarchie KObjektu zabalená do třídy KAtts,
                          // nejvyšší objekty herarchie jsou pod tagem "kutil"
                          // (což odpovídá kořenovému elementu XML souborů)

    private static final boolean logXml = false; // zda logovat načtenou hierarchii
                                                 // převézt zpět na XML a vypsat na std výstup
    /**
     * Vytvořit nový scheduler.
     * @param l kopletí hierarchie KObjectů zabalená do třídy KAtts
     */
    public Scheduler( KAtts l ){
        loaded = l;
        init( loaded );
    }

    private void init( KAtts kAtts ){
        calendar = new PriorityQueue<CalendarEvent>( 3, new CalendarEventComparator() );

        for( KObject o : kAtts.getList("times") ){
            calendar.add( new CalendarEvent( (Time)o ) );
        }

        if( logXml ) Log.it( getXMLString(kAtts) );

        Global.rucksack().setScheduler(this);

        start();
    }

    /**
     * Ukončí běh programu.
     */
    public void terminate( ){
        if( logXml ) Log.it( getXMLString(loaded) );
        System.exit(0);
    }

    /**
     * Vrátí seznam vršků hierarchie KObjectů.
     * @return
     */
    public LinkedList<KObject> getKObjects(){
        return loaded.getList("kutil");
    }

    /**
     * Smyčka běhu programu.
     */
    private void start( ) {


        while( true ){
            long zacatekAkce = time();
            if( calendar.isEmpty() ) break;

            //vyndáme co bylo naplánováno na teď.
            CalendarEvent ce = calendar.poll();
            Time time = ce.time;

            //pokud tento time byl už vyčerpán, neděláme nic a přeskočíme zbytek cyklu
            //abychom mohli vybrat další událost v pořadí
            if( time.isFinished() ){
                continue;
            }

            //snížíme iterátor času
            time.decrementIterations();
            //zde se provádí step() -> to je raison d'être celého scheduleru
            time.step();

            //naplánujeme další událost tohoto time
            calendar.add( new CalendarEvent( time , ce.when + time.getPeriod() ) );
            //spočteme jak dlouho nám trvala tato událost
            long trvalo = time() - zacatekAkce ;

            //pokud už není nic v kalendáři, žádná událost - opustíme smyčku
            if( calendar.isEmpty() ) break;

            //na dobu, než je naplánována další událost usneme
            sleep( calendar.peek().when - (ce.when + trvalo) );
        }
    }

    /**
     * Bezpečný sleep (pro záporné hodnoty nespí).
     * @param miliseconds
     */
    private static void sleep( long miliseconds ){
        if( miliseconds <= 0 ) { return;}

        try {
            Thread.sleep( miliseconds );
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private static long time(){
        return System.nanoTime()/1000000L;
    }

    
    /**
     * Převede KAtts (v němž může být i celá hierarchie) do XML reprezentace.
     * @param kAtts balíček otagovaných objektů
     * @return string XML reprezentace
     */
    public static String getXMLString( KAtts kAtts ){

        StringBuilder sb = new StringBuilder();
        sb.append("<kutil>\n");

        for( KObject o :  kAtts.getList("kutil")){
            sb.append( o.toXml().toString() );
        }

        sb.append("</kutil>");
        return sb.toString();
    }

}

/**
 * Třída reprezentující událost kalendáře
 * @author Tomáš Křen
 */
class CalendarEvent {

    public long when; //čas kdy má být uzkutečněna tato udalost
    public Time time; //KObject time

    public CalendarEvent( Time t ){
        time = t;
        when = t.getPeriod();
    }

    public CalendarEvent( Time t , long w ){
        time = t;
        when = w;
    }
}

/**
 * Porovnávač Událostí - porovnáva která je dřív.
 * @author Tomáš Křen
 */
class CalendarEventComparator implements Comparator<CalendarEvent>{

    @Override
    public int compare(CalendarEvent x, CalendarEvent y)
    {
        if (x.when < y.when ){ return -1; }
        if (x.when > y.when ){ return  1; }
        return 0;
    }
}
