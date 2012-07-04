package kutil.core;

/**
 * Třída zajištující vypisování na standardní výstup pro účely ladění.
 * @author Tomáš Křen
 */
public class Log {

    /**
     * Vypíše argument na standardní výstup.
     * Slouží k obalení ladících hlášek.
     * @param o
     */
    public static void it( Object o ){

        System.out.println(o);

    }

}
