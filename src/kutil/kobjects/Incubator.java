package kutil.kobjects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;

/**
 * Funkce incubator je bílá funkce s parametry, to znamená, že krom vstupní hodnoty
 * její chování ovlivňuje obsah jejího vnitřku; jejími parametry jsou funkce, které chceme
 * použít jako stavební díly vnitřního programu nové mouchy.
 * Tyto funkce jednoduše vložíme do vnitřku funkce incubator.
 * Na libovolný vstup pak incubator reaguje tak, že z těchto funkcí sestaví vnitřní program mouchy.
 * @author Tomáš Křen
 */
public class Incubator extends Function {

    public  Incubator(KAtts kAtts ) {
        super(kAtts);
        create();
    }

    public  Incubator(  Incubator i ){
        super( i );
        create();
    }

    @Override
    public KObject copy() {
        return new  Incubator(this);
    }

    private void create(){
        setType("incubator");
        resetVal("incubator");
    }


    private static final int maxLayers     = 6;
    private static final int maxInOneLayer = 6;



    public Fly getFly(){

        if( inside().isEmpty() ) return null;
        
        LinkedList<Function> topTerminals    = new LinkedList<Function>();
        LinkedList<Function> functions       = new LinkedList<Function>();
        LinkedList<Function> bottomTerminals = new LinkedList<Function>();
        
        for( KObject o : inside() ){
            if( o instanceof Function ){
                Function f = (Function)o;
                
                if( f.getImplementation().numArgs() == 0 ){
                    topTerminals.add(f);
                }else if( f.getImplementation().numOutputs() == 0 ){
                    bottomTerminals.add(f);
                }else{
                    functions.add(f);
                }                
            } 
        }
        
        if( topTerminals.isEmpty() || bottomTerminals.isEmpty() ){
            return null;
        }
        
        int numLayers;
        if( functions.isEmpty() ){
            numLayers = 2;
        }else{
            numLayers = rand().nextInt(maxLayers-2) + 3;
        }

        //Log.it("NumLayers: " + numLayers );
        Fly fly = (Fly) KObjectFactory.insertKObjectToSystem( new Fly() , null );

        int lastSumOutputs = -1;

        LinkedList<PortNode> outputs = null;
        LinkedList<PortNode> newOutputs = null;
        LinkedList<PortNode> inputs  = null;

        for( int i = 0 ; i<numLayers ; i++ ){

            LinkedList<Function> fs;

            if( i == 0 ){
                fs = topTerminals;
            }else if( i == numLayers - 1 ){
                fs = bottomTerminals;
            }else{
                fs = functions;
            }

            LinkedList<Function> layer = new LinkedList<Function>();
            int sumOutputs = 0;
            int sumInputs  = 0;

            if( i == 0 ){
                int numInLayer = rand().nextInt(maxInOneLayer) + 1;
                //Log.it("NumInLayer: " + numInLayer );

                LinkedList<PortNode> outList = new LinkedList<PortNode>();

                for( int j = 0; j < numInLayer ; j++ ){
                    Function f = randomFunction(fs);
                    layer.add( f );

                    int numOutputs = f.getImplementation().numOutputs();
                    sumOutputs    += numOutputs;

                    for( int k = 0 ; k < numOutputs ; k++ ){
                        outList.add( new PortNode(f, k) );
                    }
                }

                newOutputs = outList;

            } else{

                LinkedList<PortNode> outList = new LinkedList<PortNode>();
                LinkedList<PortNode> inList  = new LinkedList<PortNode>();

                while( true ){
                    Function f     = randomFunction(fs);
                    int numInputs  = f.getImplementation().numArgs();
                    sumInputs     += numInputs;

                    if( sumInputs > lastSumOutputs ) break;

                    layer.add( f );
                    int numOutputs = f.getImplementation().numOutputs();
                    sumOutputs    += numOutputs;

                    for( int k = 0 ; k < numInputs ; k++ ){
                        inList.add( new PortNode(f, k) );
                    }

                    for( int k = 0 ; k < numOutputs ; k++ ){
                        outList.add( new PortNode(f, k) );
                    }
                }

                outputs    = newOutputs;
                newOutputs = outList;
                inputs     = inList;

            }

            lastSumOutputs = sumOutputs;

            int j = 0;
            for( Function f : layer ){
                insert( f,fly, j*100 , i*100 );
                j++;
            }

            if( inputs != null ){

                Collections.shuffle( inputs, Global.random() );
                int numConnected = 0;

                for( PortNode n : outputs ){
                    Function f    = n.function;
                    int      port = n.port;

                    PortNode input;
                    if(  numConnected < inputs.size() ) {
                        input = inputs.get(numConnected);
                    }else if( inputs.size() > 0 ){
                        input = inputs.get( rand().nextInt( inputs.size() ) );
                    }else{
                        break;
                    }


//                  Log.it("Layer "+i+" "+"out: "+f.getImplementation().title() +" "+port+" "
//                         +"Lin:  "+input.function.getImplementation().title() +" "+input.port );

                    f.setTargetAndPort( port, input.function , input.port );

                    numConnected++;
                }

            }
        }
        return fly;
    }

    private Function insert( Function f , Fly fly , int x , int y ){
        
        KObjectFactory.insertKObjectToSystem(f , fly );
        f.setPos( new Int2D(x, y) );
        fly.directAdd(f);
        return f;
    }

    private Function randomFunction( LinkedList<Function> fs ){
        return (Function) fs.get( rand().nextInt( fs.size() ) ).copy();
    }

    private Random rand(){
        return Global.random();
    }
    
}

class PortNode {

    public Function function;
    public int      port;

    public PortNode( Function f , int p ){
        function = f;
        port     = p;
    }

}
