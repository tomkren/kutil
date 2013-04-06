package kutil.functions;

import kutil.kobjects.Function;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;

/**
 *
 * @author Tomáš Křen
 */
public class ForceNumInputs implements FunctionImplemetation {

    private FunctionImplemetation puvodniFI;
    private String                puvodniVal;
    private int                   newNumArgs;
    
    
    public ForceNumInputs( FunctionImplemetation fi , String val , int desiredNumberOfInputs ) {
        
        puvodniFI  = fi;
        puvodniVal = val; 
        newNumArgs = desiredNumberOfInputs ;
    }
    
    public KObject[] compute( KObject[] objects ){
        
        StringBuilder sb = new StringBuilder("( ");
        sb.append( puvodniVal );
        for( KObject o : objects ){
            sb.append(" ");
            sb.append( o.toKisp2() );
        }        
        sb.append(" )");
        
        String kispStr = sb.toString();
        
        
        Function fun = new Function( new Expre( kispStr ) ) ;
        
        KObjectFactory.insertKObjectToSystem(fun, null);
        
        return new KObject[] {   fun   } ;        
        
    }

    public int numArgs(){
        return newNumArgs;
    }

    public int numOutputs(){
        return puvodniFI.numOutputs();
    }

    public String title(){
        return puvodniFI.title();
    }

    public int getTitleShift(){
        return 10;
    }
    
    

}
