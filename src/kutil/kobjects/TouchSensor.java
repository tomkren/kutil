package kutil.kobjects;

import kutil.core.KAtts;

/**
 * Objekt virtualního světa reprezentující speciální funkci používanou uvnitř mouchy k indikaci toho, že
 * došlo ke kolizi této mouchy s jiným objektem.
 * @author Tomáš Křen
 */
public class TouchSensor extends Function {

    public TouchSensor(KAtts kAtts ) {
        super(kAtts);
        create();
    }

    public TouchSensor( TouchSensor ts ){
        super( ts );
        create();
    }

    @Override
    public KObject copy() {
        return new TouchSensor(this);
    }

    private void create(){
        setType("touchSensor");
        resetVal("touchSensor");
    }

    private KObject lastBullet;

    public void fire( KObject bullet ){
        lastBullet = bullet;
        handleInput( bullet , 0);
    }

    public KObject getLastBullet(){
        return lastBullet;
    }



}
