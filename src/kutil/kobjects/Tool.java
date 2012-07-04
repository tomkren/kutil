package kutil.kobjects;

import java.awt.Color;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;

/**
 * Objekt virtuálního světa sloužící jako tlačítko zobrazující objekt který se zkopíruje na
 * kurzor myši po kliknutí na toto tlačítko.
 * @author Tomáš Křen
 */
public class Tool extends Frame {

    public Tool( KAtts kAtts ){
        super(kAtts);

        create();
    }

    private void create(){
        setType("tool");
        setBgcolor(Color.gray);
        setDecorColor(Color.darkGray);
        setShowZeroPoint(false);
        setStepInside(false);
        setIsGuiStuff(true);
    }

    private void copyTool(  ){
        Global.rucksack().copyToCursor(inside().getFirst() );
    }

    @Override
    public void step() {
    }



    @Override
    public void click(Int2D clickPos) {
        if( isDecorHit(clickPos) ){
            super.click(clickPos);
        }
        else{
            copyTool();
        }
    }

    @Override
    public void drag(Int2D clickPos, Int2D delta,Frame f) {

        if( isDecorHit(clickPos) ){
            super.drag(clickPos , delta,f);
        }
        else{
            copyTool();
        }
    }





}
