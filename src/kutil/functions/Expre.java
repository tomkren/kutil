package kutil.functions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import kutil.core.Global;
import kutil.core.Log;
import kutil.kobjects.Function;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;



/**
 *
 * @author Tomáš Křen
 */


//!!!!!!!!!!!!!!!!!!!! TODO : aby to umělo :   const (+ 1) 9


public class Expre implements FunctionImplemetation {

    private LC.Expr expr;
    //private Function function;
    
    private String title;
    
    public Expre( String str  ){
        expr = LC.reduceAll_( LC.fromStr(str) );
        //function = f;
        
        title = expr.toString();
    }
    
    private Expre( LC.Expr e ){
        expr = e;
        title = e.toString();
    }
    
    
    public String title(){
        return title;
    }

    public int getTitleShift(){
        return 0;
    }

    public int numArgs() {
        return 1;
    }

    public int numOutputs() {
        return 1;
    }
    
    public static FunctionImplemetation mkFunctionImplemetation( String str ){
        LC.Expr newExpr = LC.reduceAll_( LC.fromStr(str) );
        
        if( newExpr instanceof LC.Val ){
            return new FakeImplementation( ((LC.Val)newExpr).kObject );
        }
        
        return new Expre( newExpr );
    }
    
    public FunctionImplemetation dosadKObject( KObject o ){
        LC.Val  oVal     = new LC.Val( o.toKisp2() );
        LC.Expr exprKlon = LC.klon(expr);
        LC.Expr newExpr  = LC.reduceAll_( new LC.App( exprKlon , oVal ) );
        
        if( newExpr instanceof LC.Val ){
            return new FakeImplementation( ((LC.Val)newExpr).kObject );
        }
        
        return new Expre( newExpr );   
    }
    
    public KObject[] compute( KObject[] objects ) {
        
        LC.Expr acc = LC.klon(expr);
        
        for( KObject o : objects ){
            LC.Val oVal = new LC.Val( o.toKisp2() );
            acc = new LC.App(acc, oVal);
        }
        
        acc = LC.reduceAll_(acc);
        
        if( acc instanceof LC.Val ){
            return new KObject[] { ((LC.Val)acc).kObject } ;
        }
        
        Function fun = new Function( new Expre( acc ) ) ;
        
        KObjectFactory.insertKObjectToSystem(fun, null);
        
        return new KObject[] {   fun   } ; 
        
    }
    
}


class LC {

    public static void main( String[] args ){
        
        Global.init();
        
        //Expr expr = LambdaCalculus.fromStr( "(\\ (x y) (x) )" );
        //Expr expr = LambdaCalculus.fromStr( "(\\ x (inc x) ) 41" );
        //Expr expr = LambdaCalculus.fromStr( "(\\ (x y) (+ x y) ) 23 42" );
        //Expr expr = LC.fromStr( "head '(23 42)" );
        Expr expr = LC.fromStr("(\\ x ( ( const const ) x  ( (\\ x ( ( const Just ) x  ( id x )  ) ) x )  ) )");
        //Expr expr = LC.fromStr( "plus3 1 2 3" );
        //Expr expr = LambdaCalculus.fromStr( "(\\ (x y) (y x 1) ) + (\\ (a b) (a 2 b) )" );
        
        
        Log.it("EXPR: " + expr ); 
        Log.it("redukovaný  : " + reduceAll(expr)  ); //-- když to tam je tak se to vysrere to nasledujici, protože Val se měnej
        //Log.it("redukovaný  : " + reduceAll(expr)  );
        
        
//        for( String str : cutToParts("1 2 () (1) (1 2) '(1 2 3)") ){
//            Log.it(str);
//        }
        
    }

    
    public static Expr klon( Expr expr ){
        if( expr instanceof App ){
            App app = (App) expr;
            return new App( klon(app.p) , klon(app.q) );
        }
        if( expr instanceof Lam ){
            Lam lam = (Lam) expr;
            return new Lam(lam.var , klon(lam.body) );
        }
        if( expr instanceof Var ){
            Var var = (Var) expr;
            return new Var( var.var );
        }
        if( expr instanceof Val ){
            Val val = (Val) expr;
            return new Val( val.kObject.toKisp2() );
        }
        return null;
    }
    
    public static Expr reduceAll( Expr expr ){
        return reduceAll_( klon(expr) );
    }
    
    public static Expr reduceAll_( Expr expr ){
        
        Log.it("redukuju : "+ expr);
        
        Expr reducedExpr = reduceOne_(expr);
        if( reducedExpr == null ){
            return expr;
        } else {
            return reduceAll_( reducedExpr );
        }
    }
    
    private static Expr reduceOne_( Expr expr ){
        
        if( expr instanceof App ){
            
            App app = (App) expr;
            
            if( app.p instanceof Lam ){
                
                Lam lam = (Lam) app.p ;
                return substitute( lam.body , lam.var , app.q );  
                
            } else {
            
                if( app.p instanceof Val && app.q instanceof Val ){

                    Val pVal = (Val) app.p;
                    Val qVal = (Val) app.q;

                    if( pVal.kObject instanceof Function ){
                      
                        FunctionImplemetation fi = ((Function) pVal.kObject).getImplementation();
                        
                        if( fi instanceof UnarImplementation ){                           
                            return new Val( ((UnarImplementation) fi).compute( qVal.kObject ) );
                        }
                    }
                } else if( app.p instanceof App && app.q instanceof Val ){
                    
                    App pApp = (App) app.p;
                    Val qVal = (Val) app.q;
                    
                    if( pApp.p instanceof Val && pApp.q instanceof Val ){
                        
                        Val ppVal = (Val) pApp.p ;
                        Val pqVal = (Val) pApp.q ;
                                                
                        if( ppVal.kObject instanceof Function ){
                            FunctionImplemetation fi = ((Function) ppVal.kObject).getImplementation();
                            
                            if( fi instanceof BinarImplementation ){                           
                                return new Val( ((BinarImplementation) fi).compute( pqVal.kObject , qVal.kObject ) );
                            }
                        }
                    } else if( pApp.p instanceof App && pApp.q instanceof Val ){
                        
                        App ppApp = (App) pApp.p ;
                        Val pqVal = (Val) pApp.q ;
                        
                        if( ppApp.p instanceof Val && ppApp.q instanceof Val ){
                            
                            Val pppVal = (Val) ppApp.p ;
                            Val ppqVal = (Val) ppApp.q ;
                            
                            if( pppVal.kObject instanceof Function ){
                                FunctionImplemetation fi = ((Function) pppVal.kObject).getImplementation();
                            
                                if( fi instanceof TernarImplementation ){                           
                                    return new Val( ((TernarImplementation) fi).compute( ppqVal.kObject , pqVal.kObject , qVal.kObject ) );
                                }
                            }
                            
                        }
                        
                    }
                    
                }
                
                
                Expr pReduced = reduceOne_(app.p);

                if( pReduced != null ){

                    return new App( pReduced , app.q );

                } else {

                    Expr qReduced = reduceOne_(app.q);

                    if( qReduced != null ){

                        return new App( app.p , qReduced );

                    } else {

                        return null;

                    }
                }
            }
        } else if( expr instanceof Lam ){
            
            Lam lam = (Lam) expr ;
            
            Expr bodyReduced = reduceOne_( lam.body );
            
            if( bodyReduced != null ){
                
                return new Lam( lam.var , bodyReduced );
                
            } else {
                return null ;
            }
            
            
        } else {    
            return null;
        }
        
        
    }
    
    private static Expr substitute( Expr expr , String varName , Expr newValue ){
        
        if( expr instanceof App ){
            App app = (App) expr;
            return new App( substitute(app.p , varName, newValue) , 
                            substitute(app.q , varName, newValue) );
        }
        
        if( expr instanceof Lam ){
            Lam lam = (Lam) expr;
            
            if( lam.var.equals(varName) ){
                return expr;
            }
            else {
                return new Lam( lam.var , substitute( lam.body , varName , newValue) );
            }
                    
        }
        
        if( expr instanceof Var ){
            Var var = (Var) expr;
            if( var.var.equals(varName) ){
                return newValue ;
            }
        }
        
        return expr;
    }
    
    
    public static Expr fromStr( String str ){
        return fromStr_( str , new LinkedList<String>() );
    }
    
    private static List<String> cutToParts( String str ){
        List<String> parts = Kisp.cutToParts(str);
        List<String> ret = new LinkedList<String>();
        boolean lastWasQuote = false;
        
        for( String part : parts ){
            if( part.equals("'") ){
                lastWasQuote = true ;
            } else {
                if( ! lastWasQuote ){
                    ret.add(part);
                } else {
                    ret.add( "'" + part );
                    lastWasQuote = false ;
                }
            } 
        }
        return ret;
    }
    
    
    private static Expr fromStr_( String str , List<String> localVars ){
        
        List<String> parts = cutToParts(str);
        
        
        if( parts.get(0).equals("\\") ){
            //Log.it("Bude to Lam");
            
            if( parts.size() != 3 ){
                Log.it("ERR: lambda musi mít tři party");
                return null;
            }
            
            return mkLam( parts.get(1) , parts.get(2) , localVars );
        } 
        
        if( parts.size() > 1 ){
            //Log.it("Bude to App");
            return mkApp( parts , localVars );
        } 
        
        String part0 = parts.get(0);
        
        if( localVars.contains( part0 ) ){
            //Log.it("Bude to Var");
            
            //Log.it(part0);
            
            return new Var(part0);
        }
        
        //Log.it("Bude to val!");
            
        //Log.it(part0);
        
        return new Val(part0);
    }
    
    private static Expr mkApp( List<String> parts , List<String> localVars ){
        
        String part0 = parts.get(0);
        
        Expr retExpr = fromStr_( part0 , localVars );
           
        for( int i = 1 ; i < parts.size() ; i++ ){
            String part = parts.get(i);
            //Log.it(part);
            Expr expr = fromStr_(part, localVars);
            retExpr = new App( retExpr , expr );
        }

        return retExpr;
    }
    
    private static Expr mkLam( String varsStr , String bodyStr , List<String> localVars ){
        
        List<String> vars = cutToParts( varsStr ) ;        

        for( String var : vars ){
            //Log.it(var);

            localVars.add(var);
        }

        //Log.it(bodyStr);

        Expr retExpr = fromStr_(bodyStr, localVars) ;

        for( int i = vars.size()-1 ; i>=0 ; i-- ){
            String var = vars.get(i);
            localVars.remove(var);

            retExpr = new Lam(var, retExpr);

        }

        return retExpr;
    }
    
    public static interface Expr {  } 
    
    static class App implements Expr {
        Expr p;
        Expr q;

        public App( Expr e1 , Expr e2 ){
            p = e1;
            q = e2;
        }
        
        @Override
        public String toString() {
            return "( " + p.toString() + " " + q.toString() + " )";
        }
        
        
    }

    static class Lam implements Expr {
        String var;
        Expr   body;
        
        public Lam( String v , Expr b ){
            var  = v;
            body = b;
        }
        
        @Override
        public String toString() {
            
            boolean dontDrawParens = body instanceof App || body instanceof Lam ;
            String bodyStr = dontDrawParens ? body.toString() : "( " + body.toString() + " )" ;
            
            return "(\\ " + var + " " + bodyStr + " )";
        }
    }

    static class Var implements Expr {
        String var;

        public Var( String v ){
            var = v;
        }
        
        @Override
        public String toString() {
            return var;
        }
        
        
    }

    static class Val implements Expr {
        KObject kObject;

        public Val( String str ){
            

            Log.it("vytvařim ze str : "+str);
            kObject = Global.rucksack().mkKObjectByString(str);
        }

        public Val( KObject o ){
            kObject = o;
        }
        
        @Override
        public String toString() {
            
            return kObject.toKisp2();
        }
        
        
    }
    
    
}


