package kutil.kobjects;

import kutil.core.Global;
import kutil.core.KAtts;
import kutil.core.Log;
import kutil.core.WebSlot;

/**
 * Speciální typ funkce zajišťující komunikaci se serverem pro posílaní KObjektů skrz
 * internet.
 * @author Tomáš Křen
 */
public class WebOutput extends Function {

    private long lastCheck;

    public WebOutput(KAtts kAtts ) {
        super(kAtts);
        create();
    }

    public WebOutput( WebOutput wo ){
        super( wo );
        create();
    }

    @Override
    public void step() {
        super.step();

        if( Global.rucksack().isSimulationRunning() ){
            if( time() - lastCheck > 1000 ){
                lastCheck = time();

                //setWebSlot("kutil", "<object type=\"lol\">"+i+"</object>" );

                String webXML = WebSlot.getWebSlot("kutil");

                if( !webXML.equals("") ){
                    WebSlot.setWebSlot_get("kutil", "");

                    KObject o = KObjectFactory.newKObject(webXML).copy();

                    //Log.it(  );
                    fire( o );
                }
            }
        }
    }



    @Override
    public KObject copy() {
        return new WebOutput(this);
    }

    private void create(){
        setType("webOutput");
        resetVal("webOutput");

        lastCheck = time();
    }

    private KObject lastBullet;

    public void fire( KObject bullet ){
        lastBullet = bullet;
        handleInput( bullet , 0);
    }

    public KObject getLastBullet(){
        return lastBullet;
    }

    private static long time(){
        return System.currentTimeMillis();
        //return System.nanoTime()/1000000L; //v milisekundach
    }



}
