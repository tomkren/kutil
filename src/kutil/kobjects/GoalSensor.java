package kutil.kobjects;

import kutil.core.KAtts;

/**
 * Objekt virtualního světa reprezentující speciální funkci používanou uvnitř mouchy k indikaci toho, že
 * dospěla do cíle.
 * @author Tomáš Křen
 */
public class GoalSensor extends Function {

    /**
     * Vytvoří GoalSensor na základě kAtts.
     */
    public GoalSensor(KAtts kAtts ) {
        super(kAtts);
        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public GoalSensor( GoalSensor fg ){
        super( fg );
        create();
    }

    @Override
    public KObject copy() {
        return new GoalSensor(this);
    }

    private void create(){
        setType("goalSensor");
        resetVal("goalSensor");
    }

    private KObject lastBullet;

    /**
     * Zavolání této funkce způsobí předání bullet implementaci funkce.
     * @param bullet
     */
    public void fire( KObject bullet ){
        lastBullet = bullet;
        handleInput( bullet , 0);
    }

    /**
     * Vrací poslední bullet.
     */
    public KObject getLastBullet(){
        return lastBullet;
    }



}
