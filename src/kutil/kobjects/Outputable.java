package kutil.kobjects;

/**
 * Rozhraní umožnující nastavovat funkcím (a Kobjektu in) s jakým vstupem je jejich
 * výstup propojen.
 * @author Tomáš Křen
 */
public interface Outputable {

    public void setTargetAndPort( int fromPort , Inputable t , int p );

}
