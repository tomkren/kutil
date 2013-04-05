package kutil.functions;

import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;

/**
 * FunctionImplementace převádějící binární implementaci na unární, tato třída se využívá při
 * částečné aplikaci funkce.
 * @author Tomáš Křen
 */
public class BinarFromTernar extends BinarImplementation{

    TernarImplementation ter;
    KObject arg1;

    public BinarFromTernar( TernarImplementation ter , KObject arg1 , String title , int titleShift ){
        super(title, titleShift);
        this.ter   = ter;
        this.arg1 = arg1;
    }

    @Override
    public KObject compute(KObject arg2,KObject arg3) {
        
        KObject arg1copy = KObjectFactory.insertKObjectToSystem(arg1.copy(), null);
        return ter.compute(arg1copy, arg2 , arg3);
    }


}
