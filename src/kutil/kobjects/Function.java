package kutil.kobjects;

import kutil.functions.BinarImplementation;
import kutil.functions.Kisp;
import kutil.functions.FunctionImplemetation;
import kutil.functions.NularImplementation;
import kutil.functions.UnarBinarImplementation;
import kutil.functions.UnarImplementation;
import kutil.functions.UnarNularImplementation;
import kutil.items.StringItem;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.functions.*;
import kutil.shapes.BinarShape;
import kutil.shapes.FunctionShape;
import kutil.shapes.NularShape;
import kutil.shapes.UnarBinarShape;
import kutil.shapes.UnarNularShape;
import kutil.shapes.UnarShape;
import kutil.shapes.TernarShape;


/**
 * Objekt virtuálního světa reprezentující funkci.
 * Konkrétní funkce vznikají nejčastěji jako instance této třídy.
 * Některé funkce se speciálním chováním jsou instance tříd dědících po
 * této třídě.
 * @author Tomáš Křen
 */
public class Function extends Basic implements Inputable,Outputable {
 
    private StringItem      targetString; // textový kód rerezentující informace o tom, do jakých vstupů
                                          // jakých objektů vedou výstupy z této funkce
    private StringItem      insideTargetString; // textový kod reprezentující vztah vstupů a vnitřních objektů
                                                // využívaný černou funkcí
    private StringItem      outsideTargetString; // textový kód reprezentující vztah výstupů a rodičem, který
                                                 // je funkce, využívaný funkcí která je uvnitř
                                                 // černé funkce jak výstup
    private StringItem      val; // textová Kispová reprezentace implementace této funkce

    /**
     * Podporované typy funkcí, typem myslíme počet vstupů a výstupů.
     */
    public enum FType { nular, unarNular , unar, binar, ternar ,unarBinar , other }

    private FType fType; //typ této funkce

    private Inputable[]     targets;     // pole cílových objektů (do kterých výstupy)
                                         // indexované výstupními porty
    private int[]           targetPorts; // pole vstupních portů odpovídající jednotlivým vstupům

    private Inputable[]     insideTargets;    // pole cílových vnitřních objektů (používáne černou funkcí)
    private int[]           insideTargetPorts;// pole jim odpovídajících vstupních portů

    private Inputable[]     outsideTargets;     //pole venkovních cílových objektů (používane vnořenými fcemi)
    private int[]           outsideTargetPorts; //pole jim odpovídajícících vstupních portů

    private FunctionImplemetation implementation; // implementace této funkce, odvozená z hodnoty val

    private KObject[]       slots; // sloty odpovídající jednotlivým vstupům
                                   // umoňují počkat na přítomnost všech vstupních hodnot funkce
    private int             numFreeSlots; // počet ještě neobsazených slotů

    private boolean         isUserDefined ; //určuje, zda je funkce černá
                                            // (tzn uživatelsky definovaná vnitřní strukturou)

    private static final Int2D arrowEndUnar = new Int2D(16,-1);
    private static final Int2D[] arrowEndsBinar = new Int2D[]{ new Int2D(16,-1),
                                                               new Int2D(49,-1) };
    private static final Int2D[] arrowEndsTernar = new Int2D[]{ new Int2D(16,-1),
                                                                new Int2D(49,-1),
                                                                new Int2D(82,-1) };
    private static final Int2D arrowEndUB = new Int2D(33,-1);


    /**
     * Vytvoří novou funkci podle kAtts.
     * @param kAtts strukturovaný vstupní argument obsahující vstupní data
     */
    public Function( KAtts kAtts ){
        super( kAtts );
        targetString        = items().addString( kAtts, "target"        , null );
        insideTargetString  = items().addString( kAtts, "insideTarget"  , null );
        outsideTargetString = items().addString( kAtts, "outsideTarget" , null );
        val                 = items().addString( kAtts, "val"           , null );
        create(null);
    }

    /**
     * Vytvoří novou funkci podle Kispové hodnoty.
     * @param kAtts strukturovaný vstupní argument obsahující vstupní data
     */
    public Function( String value ){
        super( );
        targetString        = items().addString( "target"        , null  );
        insideTargetString  = items().addString( "insideTarget"  , null  );
        outsideTargetString = items().addString( "outsideTarget" , null  );
        val                 = items().addString( "val"           , value );
        create(null);
    }


    /**
     * Kopírovací konstruktor.
     */
    public Function( Function f ){
        super( f );
        targetString        = items().addString( "target"        , f.targetString.get()        );
        insideTargetString  = items().addString( "insideTarget"  , f.insideTargetString.get()  );
        outsideTargetString = items().addString( "outsideTarget" , f.outsideTargetString.get() );
        val                 = items().addString( "val"           , f.val.get()                 );
        create(null);
    }

    public Function( Expre expre  ){
        super( );
        targetString        = items().addString( "target"        , null  );
        insideTargetString  = items().addString( "insideTarget"  , null  );
        outsideTargetString = items().addString( "outsideTarget" , null  );
        val                 = items().addString( "val"           , expre.title() );

        setPhysical( true );
        
        create(expre);
    }
    

    @Override
    public KObject copy() {
        return new Function( this );
    }

    private void create(FunctionImplemetation fi){
        setType( "function" );
        resolveImplementation(fi);
    }

    @Override
    public String toKisp() {

        //TODO je to tu dost arbitrárně, jen aby to nedávalo uplně divný věci
        // konkrétně první dvě možnosti: isUserDefined & recursion

        if( this instanceof Recursion ){
            return "recursion";
        }
        if( isUserDefined ){
            return super.toKisp();
        }
        return val.get();
    }

    @Override
    public String toKisp2() {
        return toKisp();
    }
    
    

    /**
     * Na základě položky val určí implementaci funkce, tvar funkce a typ funkce.
     */
    private void resolveImplementation(FunctionImplemetation fi){
        isUserDefined = ( !inside().isEmpty() && val.get() == null ) ;

        implementation = ( fi != null ? fi : Kisp.newImplementation( val.get() , this ) ) ;
        
        if (implementation instanceof Lambda || implementation instanceof Expre ){
            switch( implementation.numArgs() ){
                case 1  : fType = FType.unar;   break;
                case 2  : fType = FType.binar;  break;
                case 3  : fType = FType.ternar; break;
                default : fType = FType.other;  break;
            }
        }
        else if( implementation instanceof UnarImplementation      ){ fType = FType.unar;      }
        else if( implementation instanceof BinarImplementation     ){ fType = FType.binar;     }
        else if( implementation instanceof TernarImplementation    ){ fType = FType.ternar;    }
        else if( implementation instanceof UnarBinarImplementation ){ fType = FType.unarBinar; }
        else if( implementation instanceof NularImplementation     ){ fType = FType.nular;     }
        else if( implementation instanceof UnarNularImplementation ){ fType = FType.unarNular; }
        else                                                        { fType = FType.other;     }

        resetSlots();
        
        switch( fType ){
            case unar      : setShape( new UnarShape(this,isUserDefined) ); break;
            case binar     : setShape( new BinarShape(this)              ); break;
            case ternar    : setShape( new TernarShape(this)             ); break;
            case unarBinar : setShape( new UnarBinarShape(this)          ); break;
            case nular     : setShape( new NularShape(this)              ); break;
            case unarNular:  setShape( new UnarNularShape(this)          ); break;
            default        : setShape( new FunctionShape(this)           ); break;
        }
    }

    /**
     * Umožňuje předefinovat implemetaci funkce pomcí nového Kispového zápisu.
     * @param str zápis implemetace funkce v Kispu
     */
    public void resetVal( String str ){
        val.set(str);
        resolveImplementation(null);
    }

    @Override
    public void init() {
        super.init();

        resetTargets();
        resetInsideTargets();
        resetOutsideTargets();
    }

    @Override
    public void resolveRenaming(String oldId, String newId) {
        super.resolveRenaming(oldId, newId);

        targetString.set( repairTarget(oldId, newId, targetString.get() ) );
        resetTargets();

        insideTargetString.set( repairTarget(oldId, newId, insideTargetString.get() ) );
        resetInsideTargets();

        outsideTargetString.set( repairTarget(oldId, newId, outsideTargetString.get() ) );
        resetOutsideTargets();
    }

    /**
     * Umožňuje opravit target-kód na základě změny id na nějaké jiné.
     * @param oldId původní id objektu
     * @param newId nové id objektu
     * @param targetStr puvodní target-kód, který chceme aktualizovat
     * @return aktualizovaný target-kód
     */
    public static String repairTarget(String oldId, String newId , String targetStr ){

        if( targetStr == null )    return null;
        if( targetStr.equals("") ) return "";

        StringBuilder sb = new StringBuilder();

        String[] ts = targetStr.split(" ");

        for( int i=0 ; i<ts.length ; i++ ){

            if( ts[i].equals("null") ){
                sb.append("null");
            } else {
                String[] part = ts[i].split(":");

                if( part[0].equals(oldId) ){
                    sb.append( newId );
                }else{
                    sb.append(part[0]);
                }
                sb.append(":");
                sb.append(part[1]);
            }

            if( i != ts.length-1 ) sb.append(" ");
        }

        return sb.toString();
    }

    @Override
    public void resolveCopying() {
        super.resolveCopying();

        if( isCopied() ){

            String newTargetString = Global.idChangeDB().repairTargetString( targetString.get() ) ;
            targetString.set( newTargetString );
            resetTargets();

            String newInsideTargetString = Global.idChangeDB().repairTargetString( insideTargetString.get() ) ;
            insideTargetString.set( newInsideTargetString );
            resetInsideTargets();

            String newOutsideTargetString = Global.idChangeDB().repairTargetString(outsideTargetString.get()) ;
            outsideTargetString.set( newOutsideTargetString );
            resetOutsideTargets();

        }
    }

    private void resetTargets(){

        if( targetString.get() == null ){
            targets     = new Inputable[0];
            targetPorts = new int      [0];
            return;
        }

        String[] ts = targetString.get().split(" ");

        targets     = new Inputable[ts.length];
        targetPorts = new int      [ts.length];

        for( int i=0 ; i<ts.length ; i++ ){

            if( ts[i].equals("null") ){
                targets[i]     = null;
                targetPorts[i] = -1;
            }

            String[] parts = ts[i].split(":");

            KObject o = idDB().get(parts[0]);
            if( o instanceof Inputable ){
                targets[i]     = (Inputable) o;
                targetPorts[i] = Integer.parseInt(parts[1]);
            }
        }
    }

    private void resetInsideTargets(){
        
        if( insideTargetString.get() == null ){
            insideTargets     = new Inputable[0];
            insideTargetPorts = new int      [0];
            return;
        }

        String[] ts = insideTargetString.get().split(" ");

        insideTargets     = new Inputable[ts.length];
        insideTargetPorts = new int      [ts.length];

        for( int i=0 ; i<ts.length ; i++ ){

            if( ts[i].equals("null") ){
                insideTargets[i]     = null;
                insideTargetPorts[i] = -1;
            }

            String[] parts = ts[i].split(":");

            KObject o = idDB().get(parts[0]);
            if( o instanceof Inputable ){
                insideTargets[i]     = (Inputable) o;
                insideTargetPorts[i] = Integer.parseInt(parts[1]);
            }
        }
    }

    private void resetOutsideTargets(){
        
        if( outsideTargetString.get() == null ){
            outsideTargets     = new Inputable[0];
            outsideTargetPorts = new int      [0];
            return;
        }

        String[] ts = outsideTargetString.get().split(" ");

        outsideTargets     = new Inputable[ts.length];
        outsideTargetPorts = new int      [ts.length];

        for( int i=0 ; i<ts.length ; i++ ){

            if( ts[i].equals("null") ){
                outsideTargets[i]     = null;
                outsideTargetPorts[i] = -1;
            }

            String[] parts = ts[i].split(":");

            KObject o = idDB().get(parts[0]);
            if( o instanceof Inputable ){
                outsideTargets[i]     = (Inputable) o;
                outsideTargetPorts[i] = Integer.parseInt(parts[1]);
            }
        }
    }

    /**
     * Umožňuje zmenit celý target-kód.
     */
    public void changeTargets( String str ){
        targetString.set(str);
        resetTargets();
    }
    /**
     * Umožňuje zmenit celý inside-target-kód.
     */
    public void changeInsideTargets( String str ){
        insideTargetString.set(str);
        resetInsideTargets();
    }
    /**
     * Umožňuje zmenit celý outside-target-kód.
     */
    public void changeOutsideTargets( String str ){
        outsideTargetString.set(str);
        resetOutsideTargets();
    }

    /**
     * Umožňuje zmenit konkrétní target a jeho vstupní port pro konkrétní výstupní port.
     * @param fromPort výstupní port této funkce
     * @param t cílový objekt
     * @param p vstupní port v rámci cílového objektu
     */
    public void setTargetAndPort(int fromPort, Inputable t, int p) {
        
        String[] ts;
        if( targetString.get() != null ){
            ts = targetString.get().split(" ");
        }
        else{
            ts = new String[0];
        }

        if( ts.length <= fromPort ){
            String[] ts2 = new String[fromPort+1];
            System.arraycopy(ts, 0, ts2, 0, ts.length);
            for( int i=ts.length ; i<ts2.length ; i++ ){
                ts2[i] = "null";
            }
            ts = ts2;
        }

        if( t != null ){
            ts[fromPort] = t.id() + ":" + p;
        }else{
            ts[fromPort] = "null";
        }
        

        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i<ts.length ; i++ ){
            sb.append( ts[i] );
            if( i != ts.length-1 ){
                sb.append(" ");
            }
        }

        //Log.it("["+ id() +"] new targetString : >"+ sb +"<" );
        targetString.set( sb.toString() );
        resetTargets();
    }

    /**
     * Vynuluje sloty.
     */
    public void resetSlots(){
        numFreeSlots = implementation.numArgs();
        slots = new KObject[ numFreeSlots ];
    }


    private boolean   isCalledByCall = false;
    private KObject[] haxCallRet     = null;

    /**
     * Slouží k přímému zavolání funkce.
     * @param inputs pole vstupních objektů, odpovídající jednotlivým vstupům
     * @return pole výstupů, odpovídající jednotlivým výstupům
     */
    public KObject[] call( KObject[] inputs ){

        isCalledByCall = true;
        //---

        doBeforeComputation( );

        if( implementation instanceof Call ){
            call_before( inputs[0] , inputs[1] );
        }
        else if( implementation instanceof UnarCall ){
            call_before( ((UnarCall)implementation).getArg() , inputs[0] );
        }

        if( insideTargetString.get() == null ){


            KObject[] ret = null;

            if( implementation instanceof Eval ){
                FunctionImplemetation fi
                    = Kisp.newImplementation(inputs[0].toKisp(), this);
                ret = fi.compute( new KObject[]{inputs[1]} );
            } else if( implementation instanceof UnarEval ){
                FunctionImplemetation fi
                    = Kisp.newImplementation
                      ( ((UnarEval)implementation).getArg().toKisp() , this);
                ret = fi.compute( new KObject[]{inputs[0]} );
            }
            else{
                ret = implementation.compute( inputs );
            }

            isCalledByCall = false;
            return ret;

        } else {

            haxCallRet = new KObject[ implementation.numOutputs() ];

            if( implementation instanceof Call ){
                insideTargets[0].handleInput( inputs[1] , insideTargetPorts[0] );
            }
            else if( implementation instanceof UnarCall ){
                insideTargets[0].handleInput( inputs[0] , insideTargetPorts[0] );
            }

            else {
                for( int i=0 ; ( i < insideTargets.length && i < inputs.length )  ; i++ ){
                    if( insideTargets[i] != null ){
                        insideTargets[i].handleInput( inputs[i] , insideTargetPorts[i] );
                    }
                }
            }
        }

        //---
        isCalledByCall = false;

        return haxCallRet;
    }


    /**
     * Tato funkce je volána pokud chceme předat funkci na konkrétní vstup nějaký objekt jako vstupní hodnotu.
     * @param input vstupní hodnota
     * @param port  vstupní port (číslo vstupu, počínaje 0)
     */
    public void handleInput( KObject input , int port ){

        if( input == null ){
            return;
        }

        if( slots.length > 0 ){
            if( slots[port] == null ){
                numFreeSlots --;
            }

            slots[port] = input;

            if( numFreeSlots > 0 )    return;
        }

        doBeforeComputation( );

        if( implementation instanceof Call ){
            call_before( slots[0] , slots[1] );
        }
        else if( implementation instanceof UnarCall ){
            call_before( ((UnarCall)implementation).getArg() , slots[0] );
        }

        if( insideTargetString.get() == null ){


            KObject[] ret = null;


            if( implementation instanceof Eval ){
                FunctionImplemetation fi 
                    = Kisp.newImplementation(slots[0].toKisp(), this);
                ret = fi.compute( new KObject[]{slots[1]} );
            } else if( implementation instanceof UnarEval ){
                FunctionImplemetation fi 
                    = Kisp.newImplementation
                      ( ((UnarEval)implementation).getArg().toKisp() , this);
                ret = fi.compute( new KObject[]{slots[0]} );
            }

            else{
                ret = implementation.compute( slots );
            }

            if( outsideTargetString.get() == null ){

                for( int i=0 ; i<targets.length ; i++ ){
                    if( targets[i] != null ){
                        targets[i].handleInput( ret[i] , targetPorts[i] );
                    }
                }
                resetSlots();

            } else {

                for( int i=0 ; i<outsideTargets.length ; i++ ){
                    if( outsideTargets[i] != null ){
                        outsideTargets[i].handleOutsideInput( ret[i] , outsideTargetPorts[i] );
                    }
                }
                resetSlots();
            }
        } else {

            if( implementation instanceof Call ){
                insideTargets[0].handleInput( slots[1] , insideTargetPorts[0] );
            }
            else if( implementation instanceof UnarCall ){
                insideTargets[0].handleInput( slots[0] , insideTargetPorts[0] );
            }

            else {
                for( int i=0 ; ( i < insideTargets.length && i < slots.length )  ; i++ ){
                    if( insideTargets[i] != null ){
                        insideTargets[i].handleInput( slots[i] , insideTargetPorts[i] );
                    }
                }
            }
            resetSlots();        
        }
    }

    private void call_before( KObject name , KObject input ){

        if( name instanceof Symbol ){
            Symbol id = (Symbol) name ;

            KObject o = Global.idDB().get( id.get() );

            if( o != null && o instanceof Function ){
                Function f = ((Function) o.copy() );

                KObjectFactory.insertKObjectToSystem( f , this );
                this.add( f );
                f.setPos( new Int2D(200, 200) );

                this.changeInsideTargets( f.id()+":0" );
                f.changeOutsideTargets( this.id()+":0" );
                f.changeTargets( null );

            }
        }
    }

    private void call_after( ){
        changeInsideTargets( null );
        clearAfterAdding();
    }

    /**
     * Vrací vnitřní targety.
     */
    public Inputable[] getInsideTargets(){
        return insideTargets;
    }

    /**
     * Override toto pokud chceme udělat něco před samotným výpočtem.
     * (HAX) využívá třída Recursion.
     */
    public void doBeforeComputation( ){
    }


    /**
     * Override toto pokud chceme udělat něco po samotném výpočtu.
     * (HAX) využívá třída Recursion.
     */
    public void doAfterComputation(){
    }


    /**
     * Analogická funkce k funkci handleInput, kterou využívá černá funkce,
     * pokud chce předat něco svým vnitřním objektům.
     * @param input
     * @param port
     */
    public void handleOutsideInput( KObject input , int port ){

        if( isCalledByCall ){
            hax_handleOutsideInput(input, port);
            return;
        }

        if (outsideTargetString.get() == null) {
            if (targets[port] != null) {
                targets[port].handleInput(input, targetPorts[port]);
            }
        } else {
            if (outsideTargets[port] != null) {
                outsideTargets[port].handleOutsideInput( input , outsideTargetPorts[port] );
            }
        }

        doAfterComputation();
        if( implementation instanceof Call || implementation instanceof UnarCall ){
            call_after();
        }
    }

    private void hax_handleOutsideInput( KObject input , int port ){

        haxCallRet[port] = input;

        doAfterComputation();
        if( implementation instanceof Call || implementation instanceof UnarCall ){
            call_after();
        }
    }

    /**
     * Vrací počet cílových objektů této funke.
     */
    public int getNumTargets(){
        return targets.length;
    }

    /**
     * Vrací implemetaci této funkce.
     * @return
     */
    public FunctionImplemetation getImplementation(){
        return implementation;
    }

    /**
     * Určuje, zda má byt vykreslena čára propojující i-tý výstup s odpovídajícím targetem.
     */
    public boolean doDrawArrow(int i){
        if( targets.length <= i ) return false;
        if( targets[i] == null ) return false;
        return ( targets[i].parent() == parent() );
    }

    /**
     * Vrací pozici vstupu i-tého cílového objektu.
     * Aby se vědělokam se mají vykreslit konce spojující čáry.
     */
    public Int2D getTargetArrowEnd(int i){
        if( targets == null ) return pos();
        return targets[i].getArrowEnd( targetPorts[i] );
    }

    /**
     * Automaticky přidá ke všem vstupům, respektive výstupům, odpovídající in objekt, respektive out objekt.
     * To umožnuje aby do funkce hned mohli padat vstupy a rovnou vypadávali výstupy.
     */
    public void addInAndOut(){
        KObject p = parent();
        if( p instanceof Basic ){
            Basic parent = (Basic) p;

            for( int i = 0 ; i < implementation.numArgs() ; i++ ){
                In in = new In();
                
                KObjectFactory.insertKObjectToSystem(in, parent);
                parent.add(in);

                in.setPos( pos().plus(new Int2D( inX(i) , -50)) );
                in.setTargetAndPort(0, this, i);
            }
            
            for( int i = 0 ; i < implementation.numOutputs() ; i++ ){

                if( targets.length > i ){
                    if( targets[i] != null ) continue;
                }

                Out out = new Out();
                
                KObjectFactory.insertKObjectToSystem(out, parent);
                parent.add(out);

                out.setPos( pos().plus(new Int2D( outX(i) , 80 ) ) );
                setTargetAndPort(i, out, 0);
            }

        }
    }

    private static int   inXUnar       = 0 ;
    private static int   inXUnarBinar  = 17 ;
    private static int[] inXBinar      = new int[] { -10 , 43 };
    private static int[] inXTernar     = new int[] { -10 , 33 , 76 };
    private static int   outXTernar    = 33;


    private int inX(int i) {

        switch( fType ){
            case unar      : return inXUnar      ;
            case binar     : return inXBinar [i] ;
            case ternar    : return inXTernar[i] ;
            case unarBinar : return inXUnarBinar ;
            case unarNular : return inXUnar      ;
            default        : return i*40;
        }

    }

    private int outX(int i) {

        switch( fType ){
            case unar      : return inXUnar      ;
            case binar     : return inXUnarBinar ;
            case ternar    : return outXTernar   ;
            case unarBinar : return inXBinar[i]  ;
            case nular     : return inXUnar      ;
            default        : return i*40;
        }

    }


    @Override
    public void click(Int2D clickPos) {
        switch( fType ){
            case unar      : clickUnar  (clickPos); break;
            case binar     : clickBinar (clickPos); break;
            case ternar    : clickTernar(clickPos); break;
            case unarBinar : clickUB    (clickPos); break;
            case nular     : clickNular (clickPos); break;
            case unarNular : clickUN    (clickPos); break;
            default        : super.click(clickPos); break;
        }
    }

    @Override
    public void drag(Int2D clickPos, Int2D delta , Frame f) {
        switch( fType ){
            case unar      : dragUnar  (clickPos, delta ,f); break;
            case binar     : dragBinar (clickPos, delta ,f); break;
            case ternar    : dragTernar(clickPos, delta ,f); break;
            case unarBinar : dragUB    (clickPos, delta ,f); break;
            case nular     : dragNular (clickPos, delta ,f); break;
            case unarNular : dragUN    (clickPos, delta ,f); break;
            default        : super.drag(clickPos, delta ,f); break;
        }
    }

    /**
     * Pro i-tý vstup vrací pozici pro vykreslení konce čáry na tento vstup.
     */
    public Int2D getArrowEnd(int i) {

        switch( fType ){
            case unar      : return pos().plus(arrowEndUnar);
            case binar     : return pos().plus(arrowEndsBinar[i]);
            case ternar    : return pos().plus(arrowEndsTernar[i]);
            case unarBinar : return pos().plus(arrowEndUB);
            // nular nemá arrowEnd
            case unarNular : return pos().plus(arrowEndUnar);
            default        : return pos().plus(arrowEndUnar);
        }

    }

    /*
     * Dale pokračují funkce řešíšící různost chování funkcí různého typu.
     */

    //------- unar ----------------------------------------------

    private void clickUnar(Int2D clickPos) {
        if( topArrowBarHitUnar(clickPos) ){
            topArrowBarUnar();
        }
        else if(bottomArrowBarHitUnar(clickPos)){
            bottomArrowBarUnar();
        }
        super.click(clickPos);
    }

    private void dragUnar(Int2D clickPos, Int2D delta,Frame f) {
        if( topArrowBarHitUnar(clickPos) ){
            topArrowBarUnar();
        }
        else if(bottomArrowBarHitUnar(clickPos)){
            //bottomArrowBar();
        }
        super.drag(clickPos,delta,f);
    }

    private void topArrowBarUnar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private void bottomArrowBarUnar(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }

    private boolean topArrowBarHitUnar(Int2D clickPos){
        return clickPos.minus( pos() ).getY() < 10 ;
    }

    private boolean bottomArrowBarHitUnar(Int2D clickPos){
        return clickPos.minus( pos() ).getY() > 40 ;
    }

    //------- binar ------------------------------------


    private void clickBinar(Int2D clickPos) {
        if( topArrowBarHit0Binar(clickPos) ){
            topArrowBar0Binar();
        }
        else if( topArrowBarHit1Binar(clickPos) ){
            topArrowBar1Binar();
        }
        else if(bottomArrowBarHitBinar(clickPos)){
            bottomArrowBarBinar();
        }
        super.click(clickPos);
    }

    private void dragBinar(Int2D clickPos, Int2D delta,Frame f) {
        if( topArrowBarHit0Binar(clickPos) ){
            topArrowBar0Binar();
        }
        else if( topArrowBarHit1Binar(clickPos) ){
            topArrowBar1Binar();
        }
        else if(bottomArrowBarHitBinar(clickPos)){
            //bottomArrowBar();
        }
        super.drag(clickPos,delta,f);
    }

    private void topArrowBar0Binar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private void topArrowBar1Binar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 1);
            Global.rucksack().resetFrom();
        }
    }

    private void bottomArrowBarBinar(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }

    private boolean topArrowBarHit0Binar(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() < 10 && p.getX() < 33 ;
    }

    private boolean topArrowBarHit1Binar(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() < 10 && p.getX() > 33 ;
    }

    private boolean bottomArrowBarHitBinar(Int2D clickPos){
        return clickPos.minus( pos() ).getY() > 40 ;
    }


    //------- ternar ------------------------------------


    private void clickTernar(Int2D clickPos) {
        if( topArrowBarHit0Ternar(clickPos) ){
            topArrowBar0Ternar();
        }
        else if( topArrowBarHit1Ternar(clickPos) ){
            topArrowBar1Ternar();
        }
        else if( topArrowBarHit2Ternar(clickPos) ){
            topArrowBar2Ternar();
        }
        else if(bottomArrowBarHitTernar(clickPos)){
            bottomArrowBarTernar();
        }
        super.click(clickPos);
    }

    private void dragTernar(Int2D clickPos, Int2D delta,Frame f) {
        if( topArrowBarHit0Ternar(clickPos) ){
            topArrowBar0Ternar();
        }
        else if( topArrowBarHit1Ternar(clickPos) ){
            topArrowBar1Ternar();
        }
        else if( topArrowBarHit2Ternar(clickPos) ){
            topArrowBar2Ternar();
        }
        else if(bottomArrowBarHitTernar(clickPos)){
            //bottomArrowBar();
        }
        super.drag(clickPos,delta,f);
    }

    private void topArrowBar0Ternar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private void topArrowBar1Ternar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 1);
            Global.rucksack().resetFrom();
        }
    }

    private void topArrowBar2Ternar(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 2);
            Global.rucksack().resetFrom();
        }
    }

    private void bottomArrowBarTernar(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }

    private boolean topArrowBarHit0Ternar(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() < 10 && p.getX() < 33 ;
    }

    private boolean topArrowBarHit1Ternar(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() < 10 && p.getX() > 33 && p.getX() < 67 ;
    }

    private boolean topArrowBarHit2Ternar(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() < 10 && p.getX() > 67 ;
    }

    private boolean bottomArrowBarHitTernar(Int2D clickPos){
        return clickPos.minus( pos() ).getY() > 40 ;
    }

    // ---- unarBinar ----------------------------------------------------


    private void clickUB(Int2D clickPos) {
        if( topArrowBarHitUB(clickPos) ){
            topArrowBarUB();
        }
        else if( bottomArrowBarHit0UB(clickPos) ){
            bottomArrowBar0UB();
        }
        else if( bottomArrowBarHit1UB(clickPos) ){
            bottomArrowBar1UB();
        }
        super.click(clickPos);
    }

    private void dragUB(Int2D clickPos, Int2D delta,Frame f) {
        if( topArrowBarHitUB(clickPos) ){
            topArrowBarUB();
        }
        else if( bottomArrowBarHit0UB(clickPos) ){
            //bottomArrowBar0();
        }
        else if( bottomArrowBarHit1UB(clickPos) ){
            //bottomArrowBar1();
        }
        super.drag(clickPos,delta,f);
    }

    private void topArrowBarUB(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private void bottomArrowBar0UB(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }

    private void bottomArrowBar1UB(){
        setTargetAndPort(1, null, -1);
        Global.rucksack().setFrom(this,1);
    }


    private boolean topArrowBarHitUB(Int2D clickPos){
        return clickPos.minus( pos() ).getY() < 10;
    }

    private boolean bottomArrowBarHit0UB(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() > 40 && p.getX() < 25 ;
    }

    private boolean bottomArrowBarHit1UB(Int2D clickPos){
        Int2D p = clickPos.minus( pos() );
        return p.getY() > 40 && p.getX() > 25 ;
    }

    //------- nular ----------------------------------------------

    private void clickNular(Int2D clickPos) {
        if(bottomArrowBarHitNular(clickPos)){
            bottomArrowBarNular();
        }
        super.click(clickPos);
    }

    private void dragNular(Int2D clickPos, Int2D delta,Frame f) {
        if(bottomArrowBarHitNular(clickPos)){
            //bottomArrowBar();
        }
        super.drag(clickPos,delta,f);
    }

    private void bottomArrowBarNular(){
        setTargetAndPort(0, null, -1);
        Global.rucksack().setFrom(this,0);
    }


    private boolean bottomArrowBarHitNular(Int2D clickPos){
        return clickPos.minus( pos() ).getY() > 30 ;
    }

    //------- unarNular ------------------------------------------

    private void clickUN(Int2D clickPos) {
        if( topArrowBarHitUN(clickPos) ){
            topArrowBarUN();
        }
        super.click(clickPos);
    }

    private void dragUN(Int2D clickPos, Int2D delta,Frame f) {
        if( topArrowBarHitUN(clickPos) ){
            topArrowBarUN();
        }
        super.drag(clickPos,delta,f);
    }

    private void topArrowBarUN(){
        Outputable from     = Global.rucksack().getFrom();
        int        fromPort = Global.rucksack().getFromPort();

        if( from != null ){
            from.setTargetAndPort(fromPort, this, 0);
            Global.rucksack().resetFrom();
        }
    }

    private boolean topArrowBarHitUN(Int2D clickPos){
        return clickPos.minus( pos() ).getY() < 10 ;
    }


}
