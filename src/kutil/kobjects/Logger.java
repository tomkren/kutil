package kutil.kobjects;

import kutil.items.StringItem;
import kutil.core.KAtts;
import kutil.core.Log;

/**
 * Testovací KObjekt, prakticky nepoužívaný. Při svém kroku vypíše na standarní výstup zprávu.
 * @author Tomáš Křen
 */
public class Logger extends Basic {

    private StringItem msg;

    public Logger(KAtts kAtts ){
        super( kAtts );

        setType("logger");

        msg = items().addString(kAtts , "msg", "Hello World!" );
    }

    @Override
    public void step() {
        Log.it(msg);
        super.step();
    }

}