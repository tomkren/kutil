package kutil.kobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import kutil.core.Int2D;
import kutil.kevents.KEvent;
import kutil.xml.Xml;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;

/**
 * Tento interface musí implementovat každý objekt virtuálního světa.
 * Typicky však nedoporučujeme ho implementovat ručně, místo toho raději doporučujeme
 * dědit od třídy Basic.
 * @author Tomáš Křen
 */
public interface KObject {

    /**
     * Vrací kopii tohoto objektu.
     */
    public KObject copy();
    /**
     * Úplně smaže objekt.
     */
    public void    delete();
    /**
     * Pokud objekt potřebuje zareagovat na to, že byl zkopírován, dělá to v této metodě.
     */
    public void    resolveCopying();
    /**
     * Tato metoda je volána pokud došlo ke změnění id nějakého objektu.
     * Pokud na to potřebujuje zareagovat, reaguje na to v této metodě.
     */
    public void    resolveRenaming( String oldId , String newId );
    /**
     * Vykoná jeden krok tohoto objektu.
     */
    public void    step();
    /**
     * Převádí objekt do XML reprezentace.
     */
    public Xml     toXml();
    /**
     * Vrací Kispovou reprezentaci tohoto objektu.
     */
    public String  toKisp();
    /**
     * Vrací seznam vnitřních objektů tohoto objektu.
     */
    public LinkedList<KObject> inside();
    /**
     * Voláním této metody se dokončí druhá fáze inicializace objektu.
     */
    public void    init();
    /**
     * Tato metoda je volána při vytváření objektu pro informovaní objektu o jeho rodiči.
     */
    public void    parentInfo( KObject parent );
    /**
     * Vrací rodičovský objekt tohoto objektu (tzn. objekt součástí jehož vnitřku je tento objekt).
     */
    public KObject parent();
    /**
     * Vrací id objektu.
     */
    public String  id();
    /**
     * Vrací pozici tohoto objektu.
     */
    public Int2D   pos();
    /**
     * Vrací barvu pozadí vnitřku tohoto objektu.
     */
    public Color   bgcolor();
    /**
     * Provede textový příkaz na tomto objektu.
     * @param cmd text příkazu
     * @return vrací textovou odpověď na daný příkaz
     */
    public String  cmd( String cmd );
    /**
     * Přidá daný objekt do vntřku (na konec) tohoto objektu.
     * @param o objekt k přidání
     */
    public void    add     (KObject o);
    /**
     * Přidá daný objekt do vntřku (na začátek) tohoto objektu.
     * @param o objekt k přidání
     */
    public void    addFirst(KObject o);
    /**
     * Odstraní daný objekt z vntřku tohoto objektu.
     * @param o objekt k vymazání
     */
    public void    remove  (KObject o);
    /**
     * Odstraní objekt ze svého rodiče.
     */
    public void    remove  ();
    /**
     * Smaže první objekt z vnitřku.
     * @return sazaný objekt
     */
    public KObject popFirst();
    /**
     * Vykreslí vnitřek objektu.
     * @param g Graphics2D do které chceme vykreslit
     * @param center pozice středu vykreslování
     * @param frameDepth hloubka vykreslení (zabraňuje vykreslení nekonečné smyčky frámů)
     */
    public void    drawInside   ( Graphics2D g , Int2D center , int frameDepth );
    /**
     * Vykreslí vnější podobu objektu na základě jeho tvaru.
     * @param g Graphics2D do které chceme vykreslit
     * @param center pozice středu vykreslování
     * @param frameDepth hloubka vykreslení (zabraňuje vykreslení nekonečné smyčky frámů)
     */
    public void    drawOutside  ( Graphics2D g , Int2D center , int frameDepth );
    /**
     * Nastaví zda je objekt označen.
     */
    public void    setHighlighted( boolean isHighlighted );
    /**
     * Nastaví pozici tohoto objektu.
     */
    public void    setPos( Int2D newPos );
    /**
     * Přenastaví hodnotu rodičovského objektu.
     */
    public void    setParent( KObject newParent );
    /**
     * Reaguje na událost uvnitř objektu.
     * @param kEvent událost, na kterou se má reagovat
     */
    public void    event( KEvent kEvent );
    /**
     * Vrací, zda je objekt zasažen kliknutím.
     * @param clickPos pozice kliknutí
     * @return zda byl objekt zasažen
     */
    public boolean isHit( Int2D pos );
    /**
     * Reakce na táhnutí myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice začátku táhnutí
     * @param delta vektor táhnutí (kolik se táhlo)
     */
    public void    drag ( Int2D clickPos, Int2D delta , Frame f );
    /**
     * Reakce na kliknutí myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice kliknutí
     */
    public void    click( Int2D clickPos );
    /**
     * Reakce na pustění tlačítka myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice puštění
     */
    public void    release( Int2D clickPos );
    /**
     * Vrací svět fyzikální simulace tohoto objektu.
     * Tento svět fyzikální simulace simuluje chování
     * vnitřních fyzických objektů tohoto objektu.
     */
    public World   getWorld();
    /**
     * Vrací těleso fyzikální simulace odpovídající tomuto objektu.
     */
    public Body    getBody();
    /**
     * Vrací z da se jedná o fyzický předmět.
     */
    public boolean isPhysical();
    /**
     * Vrací zda je objekt součást guiStuff, čili nejde mazat a opouštět backspacem.
     */
    public boolean getIsGuiStuff();
    /**
     * Vrací zda je objekt movable, tzn. určuje zda je možno objektem hýbat myší.
     */
    public boolean getIsMovable();

    
    public void setPhysicalOff();

    public void setPhysicalOn();

}
