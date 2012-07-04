package kutil.functions;

import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;

/**
 * FunctionImplementace převádějící unární implementaci na konstantu, používaje
 * částečnou aplikaci funkce (v současnosti se tato třída v programu ale nepoužívá).
 * @author Tomáš Křen
 */
public class ConstantFromUnar extends UnarImplementation {

    UnarImplementation ui;
    KObject arg;

    public ConstantFromUnar( UnarImplementation ui , KObject arg , String title , int titleShift ){
        super(title, titleShift);
        this.ui  = ui;
        this.arg = arg;
    }

    @Override
    public KObject compute(KObject o) {
        KObject argCopy = KObjectFactory.insertKObjectToSystem(arg.copy(), null);
        return ui.compute( argCopy );
    }



}
