package kutil.functions;

import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;

/**
 * FunctionImplementace převádějící binární implementaci na unární, tato třída se využívá při
 * částečné aplikaci funkce.
 * @author Tomáš Křen
 */
public class UnarFromBinar extends UnarImplementation{

    BinarImplementation bi;
    KObject arg1;

    public UnarFromBinar( BinarImplementation bi , KObject arg1 , String title , int titleShift ){
        super(title, titleShift);
        this.bi   = bi;
        this.arg1 = arg1;
    }

    @Override
    public KObject compute(KObject arg2) {
        
        KObject arg1copy = KObjectFactory.insertKObjectToSystem(arg1.copy(), null);
        return bi.compute(arg1copy, arg2);
    }


}
