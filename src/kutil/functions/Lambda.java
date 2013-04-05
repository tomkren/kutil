package kutil.functions;

import kutil.kobjects.Function;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;

/**
 * Třída používaná pro překlad Kispových lambda výrazů do objektu
 * implementujícího rozhrani FunctionImplemetation.
 * @author Tomáš Křen
 */
public class Lambda implements FunctionImplemetation {

    private String body; // tělo výrazu
    private Function f;  // reference na funkci, ve které nakonec bude figurovat jako implementace

    private String[] vars; //pole proměnných výrazu
    
    private String title; // titulek
    private int    titleShift; //posun titulku

    /**
     * Vytváří nový výraz.
     * @param var podvýraz seznam proměnných/ proměnná
     * @param body telo vrazu
     * @param f funkce v které bude figurovat (tzn KObject)
     */
    public Lambda( String var , String body ,Function f ){
        title = "\\ " + var + " " + body  ;
        titleShift = 0;

        this.body = body ;
        this.f    = f    ;

        vars = Kisp.iqTrim(var).split(" ");
    }
    /**
     * zpřístupní titulek
     * @return titulek
     */
    public String title(){
        return title;
    }
    /**
     * Posun titulku na x-ové ose pro vycentrování.
     * @return titulek
     */
    public int getTitleShift(){
        return titleShift;
    }
    /**
     * Počet vstupů.
     * @return počet vstupů
     */
    public int numArgs() {
        return vars.length;
    }
    /**
     * Počet výstupů.
     * @return počet výstupů
     */
    public int numOutputs() {
        return 1;
    }

    /**
     * Dosadí do lambda výrazu.
     * @param o co dosazujeme
     * @return nová FunctionImplemetation
     */
    public FunctionImplemetation dosadKObject( KObject o ){

        String expr = body.replaceAll( vars[0] , "( ' "+o.toKisp()+" )" );

        if( vars.length > 1 ){

            StringBuilder sb = new StringBuilder();

            for( int i = 1 ; i< vars.length ; i++ ){
                sb.append( vars[i] );
                sb.append(" ");
            }

            expr = "\\ ( " + sb.toString() + ") "+expr ;

        }
        
        FunctionImplemetation fi
         = Kisp.newImplementation( expr , f );

        return fi;
    }

    /**
     * Nejdůležitější metoda: samotný výpočet.
     * @param objects pole KObjektů v roli vstupních hodnot
     * @return pole KObjektů v roli výstupních hodnot
     */
    public KObject[] compute(KObject[] objects ) {

        FunctionImplemetation fi = this;

        for( int i=0 ; i< objects.length  ;i++ ){
            fi = ((Lambda)fi).dosadKObject(objects[i]);
        }

        if( fi instanceof FakeImplementation ){

            KObject ret = ((FakeImplementation)fi).get();

            KObjectFactory.insertKObjectToSystem(ret, null);

            return new KObject[] {ret};
        }

        KObject ret = new Function( fi.title() );

        KObjectFactory.insertKObjectToSystem(ret, null);

        return new KObject[] {ret};
    }




    
}
