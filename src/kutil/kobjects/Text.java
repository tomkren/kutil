package kutil.kobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import kutil.core.Int2D;
import kutil.kevents.KEvent;
import kutil.xml.Xml;
import kutil.xml.XmlText;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;

/**
 * Jediný objekt virtuálního světa, který nedědí po Basic, představující základní typ objektu.
 * V současné verzi programu je jeho spíš teoretická: při překladu XML reprezentace na
 * KObjecty jsou textová data položek právě tyto objekty. Přesněji a více do hloubky
 * viz text bakalářské práce. Jejich zajímavější podoba která by se objevovala v samotném GUI
 * zatím neni dodělaná. konstruktory Kobjektů k nim přistupují jako k textu který si dále
 * interpretují podle sebe.
 * @author Tomáš Křen
 */
public class Text implements KObject {

    private String str;
    private KObject parent;
    private Basic   basic;
    
    public Text(String text){
        str = text;
        basic = new Basic();
    }

    public LinkedList<KObject> inside(){
        return null;
    }
    
    public Basic getBasic(){
        return basic;
    }

    public KObject copy(){
        return new Text(str);
    }

    public void    resolveCopying(){}
    public void    resolveRenaming( String oldId , String newId ){}

    public void init(){}


    /**
     * Vrací zda je objekt součást guiStuff, čili nejde mazat a opouštět backspacem.
     */
    public boolean getIsGuiStuff(){
        return false;
    }

    public void parentInfo(KObject parent) {
        this.parent = parent;
    }


    public KObject parent(){
        return parent;
    }
    public void setParent( KObject newParent ){
        parent = newParent;
    }

    public Color   bgcolor(){return Color.white;}


    public String id(){
        return null;
    }

    public Int2D pos(){
        return Int2D.zero;
    }

    public String cmd(String cmd){ return "Neplatný příkaz."; }

    public World getWorld(){
        return null;
    }
    public Body    getBody(){
        return null;
    }
    public boolean isPhysical(){
        return false;
    }

    /**
     * Vrací zda je objekt movable, tzn. určuje zda je možno objektem hýbat myší.
     */
    public boolean getIsMovable(){
        return false;
    }

    public String get(){
        return str;
    }

    public Xml toXml(){
        return new XmlText(str);
    }

    public String toKisp(){
        return ""; //??
    }

    public String toKisp2(){
        return ""; //??
    }

    public void step(){ }

    public void    add     (KObject o){}
    public void    addFirst(KObject o){}
    public void    remove  (KObject o){}
    public void    remove  (){}
    public KObject popFirst(){return null;}


    @Override
    public String toString() {
        return str;
    }

    public void    drawInside ( Graphics2D g , Int2D center , int frameDepth ){}
    public void    drawOutside( Graphics2D g , Int2D center , int frameDepth ){}

    public void setHighlighted(boolean isHighlighted) {}
    public void    setPos( Int2D newPos ){}

    public void    event( KEvent kEvent ){}

    public boolean isHit( Int2D pos ){ return false; }

    public void drag( Int2D clickPos, Int2D delta, Frame f ){}
    public void click(Int2D clickPos) {}
    public void release(Int2D clickPos,KObject obj ){}


    public void delete(){}

    public void setPhysicalOff(){}
    public void setPhysicalOn(){}


}
