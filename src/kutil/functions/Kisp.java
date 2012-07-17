package kutil.functions;


import java.util.LinkedList;
import java.util.Stack;
import kutil.kobjects.Basic;
import kutil.kobjects.Bool;
import kutil.kobjects.Box;
import kutil.kobjects.Direction;
import kutil.kobjects.Fly;
import kutil.kobjects.Function;
import kutil.kobjects.GoalSensor;
import kutil.kobjects.In;
import kutil.kobjects.Incubator;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;
import kutil.kobjects.Num;
import kutil.kobjects.Out;
import kutil.kobjects.Slot;
import kutil.kobjects.Clock;
import kutil.kobjects.Symbol;
import kutil.kobjects.TouchSensor;
import kutil.kobjects.WebOutput;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.Log;
import kutil.core.WebSlot;

/**
 * Třída sloužící jako balík statických funkcí zastřešující převod Kispových výrazů
 * na objekty implementující rozhraní FunctionImplementation.
 * Dále pak tento soubor obsahuje většinu definic FunctionImplementací používaných v programu.
 * Uvnitř těla statické metody newAtomImplementation(...)
 * je možno přiřadit symbol ke konkrétní FunctionImplementation.
 * 
 * @author Tomáš Křen
 */
public class Kisp {

    /**
     * Metoda zjistí zda str nění symbol nějaké základní funkce Kispu.
     * V této metodě se registrují všechny symboly funkcí Kispu ke svým implementacím!
     * @param str potencialní symbol základní funkce
     * @param f reference na funkci, která bude nakonec držet danou implementaci
     * @return implementace funkce daná tím symbolem
     */
    private static FunctionImplemetation newAtomImplementation( String str , Function f){


        if( "id"         .equals(str) ) return new Id();       // identita

        if( "inc"        .equals(str) ) return new Inc();      // přičti 1
        if( "dec"        .equals(str) ) return new Dec();      // odečti 1
        if( "+"          .equals(str) ) return new Plus();     // sečti
        if( "*"          .equals(str) ) return new Times();    // vynásob
        if( "-"          .equals(str) ) return new Minus();    // odečti
        if( "div"        .equals(str) ) return new Div();      // celoč. vyděl
        
        if( "copy"       .equals(str) ) return new Copy();     // zkopíruj
        if( ":"          .equals(str) ) return new Doubledot();// v seznamu rozdělí první prvek a zbytek
        if( "pair"       .equals(str) ) return new Pair();     // sloučí argumenty do dvouprvkového seznamu
        if( "unpair"     .equals(str) ) return new Unpair();   // dvouprvkový seznam rozdelí na jednotlivé prvky
        if( "head"       .equals(str) ) return new Head();     // vrátí první prvek seznamu
        if( "tail"       .equals(str) ) return new Tail();     // vrátí vše krom prvního prvku seznamu
        if( "if"         .equals(str) ) return new If();       // v závislosti na hodnotě pošle vstup
                                                               // jedním nebo druhým výstupem
        if( "not"        .equals(str) ) return new Not();      // zneguje boolovskou hodnotu
        if( "=="         .equals(str) ) return new EqualsEquals();//porovná dvě hodnoty

        if( "rotCW"      .equals(str) ) return new RotCW();     // otočí směr posměru hodinových ručiček
        if( "rot180"     .equals(str) ) return new Rot180();    // otočí směr o 180 stupňů
        if( "rot"        .equals(str) ) return new Rot();       // první směr bere jako rotaci
                                                                // (nahoru = o 180, doprava = o 90 CW,
                                                                // doleva o 90 CCW, dolu o 0) a druhý jako
                                                                // směr který otáčí, vrátí otočený směr.
        if( "dirSides"   .equals(str) ) return new DirSides();  // vrací dva kolmé směry na jeden vstupní

        if( ":2"         .equals(str) ) return new Doubledot2(); //prvni arg (hlava) sloučí s druhým
                                                                 // arg (ocas) jako seznamem
        if( "get"        .equals(str) ) return new Get();        // prvni arg id slotu, druhý arg libovolný,
                                                                 // vrací obsah slotu s daným id
        if( "set"        .equals(str) ) return new Set();        // první arg id slotu,
                                                                 // druhý arg hodnota ke zkopírovaní do slotu,
                                                                 //vrací druhý arg
        if( "read"       .equals(str) ) return new Read( f );    // umožňuje mouše číst sloty
        if( "write"      .equals(str) ) return new Write( f );   // umožňuje mouše zapisovat do slotů

        if( "getMem"     .equals(str) ) return new GetMem( f );  //dává mouše přístup k celé její paměti
        if( "topMem"     .equals(str) ) return new TopMem( f );  //davá mouše přístup k prvnímu prvku paměti
        if( "popMem"     .equals(str) ) return new PopMem( f );  //vyghodí z moušiny paměti první prvek
        if( "pushMem"    .equals(str) ) return new PushMem( f ); //zkopíruje na první místo v paměti mouchy arg


        if( "call"       .equals(str) ) return new Call();       //umožnuje zavolat funkci pomocí id
        if( "eval"       .equals(str) ) return new Eval();       //umožnuje brát seznam jako výraz

        if( "const"      .equals(str) ) return new Const();      // dva arg, vrací první

        if( "webInput"   .equals(str) ) return new WebInput();   // umožnuje odeslat na web
        if( "webOutput"  .equals(str) ) return new WebOutputImplementation(f);//umožnuje přijímat z webu

        if( "clock"      .equals(str) ) return new ClockImplementation( f );//umožnuje postupně vracet rostoucí
                                                                            // řadu čísel
        if( "incubator"  .equals(str) ) return new IncubatorImplementation( f ); //umožnuje postavit náhodnou
                                                                                 //mouchu z funkcí co má uvnitř

        if( "appleSensor".equals(str) ) return new AppleSensor( f ); //umožnuje mouše znát směr ve kterém je
                                                                     //nejbližší jablko
        if( "waspSensor" .equals(str) ) return new WaspSensor( f ); // ...nejbližší vosa
        if( "flySensor"  .equals(str) ) return new FlySensor( f );  //...nejbližší moucha
        if( "goalSensor" .equals(str) ) return new GoalSensorImplementation( f ); //dává mouše vědět
                                                                                   //že dorazila do cíle
        if( "touchSensor".equals(str) ) return new TouchSensorImplementation( f );//dává mouše vědět že
                                                                                  //se něčeho dotkla
        if( "flyCmd"     .equals(str) ) return new FlyCmd( f ); //změní pozici cíle mouchy

        //pokusné
        if( "log"        .equals(str) ) return new Log_();
        if( "logXML"     .equals(str) ) return new LogXML();
        if( "div2"       .equals(str) ) return new Div2();
        if( "c42"        .equals(str) ) return new C42();
        if( "c2342"      .equals(str) ) return new C2342();
        if( "plus3"      .equals(str) ) return new Plus3();
        if( "input"      .equals(str) ) return new Input();
        if( "output"     .equals(str) ) return new Output();


        return null;
    }

    /**
     * Převádí Kispové kódy hodnot na KObjecty.
     * @param str kód
     * @return KObject
     */
    public static KObject atomicKispElement( String str ){

        if( "[true]"  .equals(str) ) return new Bool(true);
        if( "[false]" .equals(str) ) return new Bool(false);

        if( "[left]"     .equals(str) ) return new Direction(Direction.Vals.left);
        if( "[right]"    .equals(str) ) return new Direction(Direction.Vals.right);
        if( "[up]"       .equals(str) ) return new Direction(Direction.Vals.up);
        if( "[down]"     .equals(str) ) return new Direction(Direction.Vals.down);
        if( "[randdir]"  .equals(str) ) return new Direction(Direction.Vals.randdir);

        return null;
    }

    /**
     * DŮLEŽITÁ - převádí Kispové výrazy na FunctionImplemetation.
     * @param str Kispový výraz
     * @param f funkce, ve které se implementace nakonec objeví
     * @return odpovídající FunctionImplemetation
     */
    public static FunctionImplemetation newImplementation( String str , Function f ){

        if( str == null    ){return new Id();}
        
        str = iqTrim(str);
        if( str.equals("") ){return new Id();}       


        if( isLambdaTerm( str ) ){

            LinkedList<String> parts = iqSplit( iqTrim( str ) );
            String var  = parts.get(1);
            String body = parts.get(2);

            return new Lambda(var, body, f);
        }

        int lastSpace = cutLastBracketSegment(str);

        if( lastSpace != -1 ){

            String part1 = str.substring(0, lastSpace);
            String part2 = str.substring(lastSpace+1);



            if( part1.equals("'") ){

                KObject o = fromString(part2);
                KObjectFactory.insertKObjectToSystem(o, null);
                return new FakeImplementation( o );
            }

            
            FunctionImplemetation operator = newImplementation( part1 , f );
            FunctionImplemetation operand  = newImplementation( part2 , f );

            if( (operator instanceof ErroneousImplementation) ||
                (operand  instanceof ErroneousImplementation)  ){
                return new ErroneousImplementation();
            }


            if( operator instanceof Call ){
                return new UnarCall( getArg( operand ) , str );
            }

            if( operator instanceof UnarCall ){
                UnarCall uc = (UnarCall) operator;

                Function fce =
                        (Function) KObjectFactory.insertKObjectToSystem(
                        new Function( uc.title() ), null);

                return new FakeImplementation( fce.call(new KObject[]{ getArg(operand) } )[0] );
            }

            if( operator instanceof FakeImplementation ){
                KObject o = ((FakeImplementation)operator).get();
                if( o instanceof Symbol ){
                    Symbol s = (Symbol)o;
                    KObject target = Global.idDB().get( s.get() );
                    if( target != null && target instanceof Function ){
                        Function fce = (Function) target;
                        return new FakeImplementation( fce.call(new KObject[]{ getArg(operand) } )[0]);
                    }
                }
                return new ErroneousImplementation();
            }

            if( operator instanceof Eval ){
                return new UnarEval( getArg( operand ) , str );
            }

            if( operator instanceof Lambda ){
                Lambda lambda = (Lambda) operator;
                return lambda.betaReduction( getArg(operand) );
            }
            
            if( operator instanceof UnarImplementation ){
                UnarImplementation ui = (UnarImplementation) operator;
                return new FakeImplementation( ui.compute( getArg( operand ) ) );
            }
            if( operator instanceof BinarImplementation ){
                BinarImplementation bi = (BinarImplementation) operator;
                return new UnarFromBinar( bi , getArg( operand ) , str , 0 );
            }


            return new ErroneousImplementation();
        }
        else {

            if( str.matches("[-]?[0-9]+") || str.matches("(\\[)(.)*(\\])") ){
                KObject o = KObjectFactory.insertKObjectToSystem( fromString(str) , null);
                return new FakeImplementation( o );
            }

            if( str.equals("in") ){
                return new FakeImplementation( new In() );
            }

            if( str.equals("out") ){
                return new FakeImplementation( new Out() );
            }

            FunctionImplemetation ret = newAtomImplementation(str, f ) ;
            
            if( ret == null ){
                return new FakeImplementation( new Symbol(str) );
            }
            return ret;
        }

    }

    private static KObject getArg(FunctionImplemetation fi){
        if( fi instanceof FakeImplementation ){
            return ((FakeImplementation)fi).get();
        } else {
            return new Function( fi.title() );
        }

    }

    private static int cutLastBracketSegment( String str ){

        int numBrackets = 0;

        for( int i = str.length()-1 ; i >= 0 ; i-- ){
            if( str.charAt(i) == ')' ){
                numBrackets ++;
            } else if( str.charAt(i) == '(' ){
                numBrackets --;
            } else if( str.charAt(i) == ' ' && numBrackets == 0 ){
                if( i > 1 ){
                    if( str.charAt(i-1) == '\'' ){
                        continue;
                    }
                }
                return i;
            }
        }
        return -1;
    }

    private static LinkedList<String> iqSplit( String str ){

        LinkedList<String> ret = new LinkedList<String>();

        StringBuilder sb = new StringBuilder();
        int numBrackets = 0;

        for( int i=0 ; i < str.length() ; i++ ){

            if     ( str.charAt(i) == '(' ){numBrackets ++;}
            else if( str.charAt(i) == ')' ){numBrackets --;}
            else if( str.charAt(i) == ' ' && numBrackets == 0 ){

                ret.add( sb.toString() );
                sb.setLength(0);
                continue;
            }

            sb.append(str.charAt(i));
        }

        if( sb.length() > 0 ) ret.add( sb.toString() );

        return ret;
    }

    /**
     * Speciální předpříprava řetězce na vhodný formát.
     * @param str původní řetězec
     * @return normovaný řetězec
     */
    public static String iqTrim( String str ){

        if( str == null ) return str;

        str = str .replaceAll("\\(", " ( ")
                  .replaceAll("\\)", " ) ")
                  .replaceAll("'", " ' ")
                  .replaceAll("\\\\", " \\\\ ")
                  .replaceAll("\\s+", " ")
                  .trim();

        if( str.equals("") ) return str;

        if( str.charAt(0) != '(' ) return str;

        int numOpenBrackets = 0;
        for( int i = 0 ; i < str.length() ; i++ ){
            if     ( str.charAt(i) == '(' ){numOpenBrackets ++;}
            else if( str.charAt(i) == ')' ){numOpenBrackets --;}

            if( numOpenBrackets == 0 && i < str.length()-1 ){
                return str;
            }
        }

        if( numOpenBrackets == 0 ){
            return iqTrim( str.substring(1,str.length()-1) );
        }

        return str;
    }

    private static boolean isLambdaTerm( String str ){

        LinkedList<String> parts = iqSplit( iqTrim(str) );

        if( parts.size() != 3 ) return false;
        if( ! parts.getFirst().equals("\\") ) return false;

        return true;
    }

    // převod Kispové datové struktury (seznam, číslo, směr, symbol) na odpovídající KObject
    public static KObject fromString( String str ){


        String[] parts  = str .replaceAll("\\(", " ( ")
                              .replaceAll("\\)", " ) ")
                              .trim()
                              .split("\\s+");

        if( parts.length > 1 && !parts[0].equals("(") ){
            return fromString( "("+ str +")" );
        }

        Stack<Box>   stack    = new Stack<Box>();
        Stack<Int2D> posStack = new Stack<Int2D>();

        for( int i = 0 ; i < parts.length ; ++i ){

            String part = parts[i];

            if( "(".equals(part) ){

                Box b = new Box();

                b.setPosDirect(new Int2D(100 ,100));

                stack   .push( b );
                posStack.push( new Int2D(100,100) );
            }
            else if( ")".equals(part) ){

                if( stack.empty() ) return null;

                Box   box = stack   .pop();
                posStack.pop();

                if( stack.empty() ){
                    if( i < parts.length-1 ) return null;
                    return box;
                }

                Box   peek = stack.peek();
                Int2D pos  = posStack.peek();

                box.setPosDirect(pos.copy());
                pos.adjustX(75);

                peek.directAdd(box);


            }
            else if( part.matches("[-]?[0-9]+") ){
                Num num = new Num( Integer.parseInt(part) );

                if( stack.empty() ){
                    if( i < parts.length-1 ) return null;
                    return num;
                }
                Box peek   = stack.peek();
                Int2D pos  = posStack.peek();

                num.setPosDirect(pos.copy());
                pos.adjustX(50);

                peek.directAdd(num);

            }
            else if( part.matches("(\\[)(.)*(\\])") ){

                KObject ko = atomicKispElement(part);

                if( ko == null || !( ko instanceof Basic ) ) return null;
                Basic o = (Basic)ko;

                if( stack.empty() ){
                    if( i < parts.length-1 ) return null;
                    return o;
                }
                Box peek   = stack.peek();
                Int2D pos  = posStack.peek();

                o.setPosDirect(pos.copy().plus(new Int2D(-26, -23)) );
                pos.adjustX( 60 );

                peek.directAdd(o);
            }
            else{

                Symbol sym = new Symbol( part );

                if( stack.empty() ){
                    if( i < parts.length-1 ) return null;
                    return sym;
                }
                Box peek   = stack.peek();
                Int2D pos  = posStack.peek();

                sym.setPosDirect(pos.copy().plus(new Int2D(-15, -16)));
                pos.adjustX( sym.getWidth()+20 );

                peek.directAdd(sym);
            }

        }
        return null;
    }


}

// popisy následujícch FunctionImplemetations jsou výše v těle metody newAtomImplementation.

class Id extends UnarImplementation {
    public Id(){ super("id",12); }
    public KObject compute(KObject o) {
        return o;
    }
}

class Input extends NularImplementation {
    public Input(){ super("input",0); }
    public KObject compute() {
        return null;
    }
}

class Output extends UnarNularImplementation {
    public Output(){ super("output",0); }
    public void compute(KObject o) {
    }
}


class Copy extends UnarBinarImplementation {
    public Copy(){ super("copy",21); }
    public KObject[] compute(KObject o) {
        return new KObject[] { o , KObjectFactory.insertKObjectToSystem( o.copy(),null ) };
    }
}

class Inc extends UnarImplementation {
    public Inc(){ super("inc",8); }
    public KObject compute(KObject o) {
        if( o instanceof Num ){
            Num num = (Num) o;
            num.set( num.get()+1 );
        }
        return o;
    }
}

class Dec extends UnarImplementation {
    public Dec(){ super("dec",8); }
    public KObject compute(KObject o) {
        if( o instanceof Num ){
            Num num = (Num) o;
            num.set( num.get()-1 );
        }
        return o;
    }
}


class Get extends BinarImplementation {
    public Get(){ super("get",0); }
    public KObject compute(KObject o , KObject o2) {
        if( o instanceof Symbol ){
            String id = ((Symbol) o).get();
            KObject ob = Global.idDB().get(id);
            if( ob != null && ob instanceof Slot ){
                Slot slot = (Slot)ob;

                if( slot.inside().isEmpty() ){
                    return null;
                }

                KObject ret = slot.inside().getFirst().copy();
                return KObjectFactory.insertKObjectToSystem( ret , null );
           }
        }
        return KObjectFactory.insertKObjectToSystem( o.copy() , null );
    }
}

class Set extends BinarImplementation {
    public Set(){ super("set",0); }
    public KObject compute(KObject o1 ,KObject o2) {
        if( o1 instanceof Symbol ){
            String id = ((Symbol) o1).get();
            KObject ob = Global.idDB().get(id);
            if( ob != null && ob instanceof Slot ){
                Slot slot = (Slot)ob;

                slot.directClear();
                KObject copy = o2.copy();
                
                slot.directAdd( copy );
                KObjectFactory.insertKObjectToSystem( copy , slot );
                
                return o2;
            }
        }
        return null;
    }
}


class WebInput extends UnarNularImplementation{
    public WebInput(){ super("webInput",-4); }
    public void compute(KObject o) {
        KObject copy = KObjectFactory.insertKObjectToSystem(o.copy(), null);
        WebSlot.setWebSlot_post( "kutil" , copy.toXml().toString() );
        
    }
}

class WebOutputImplementation extends NularImplementation {
    WebOutput wo;
    public WebOutputImplementation(Function f){
        super("webOutput",-10);
        wo = (WebOutput) f;
    }
    public KObject compute( ) {
        return KObjectFactory.insertKObjectToSystem( wo.getLastBullet() ,null );
    }
}


class If extends UnarBinarImplementation{
    public If(){ super("if",25); }
    @Override
    public KObject[] compute(KObject o) {

        if( o instanceof Bool ){
            if( !((Bool)o).get() ){
                return new KObject[]{ null, o };
            }
        }
        else if( o instanceof Num ){
            if( ((Num)o).get() == 0 ){
                return new KObject[]{ null, o };
            }
        }
        else if( o instanceof Box ){
            if( ((Box)o).isEmpty() ){
                return new KObject[]{ null, o };
            }
        }
//        else if( o instanceof Direction ){
//            Direction dir = (Direction) o;
//            if( dir.get() == Direction.Vals.down ||
//                dir.get() == Direction.Vals.left ){
//                return new KObject[]{ null , o};
//            }
//        }

        return new KObject[] { o , null };
    }
}

class EqualsEquals extends BinarImplementation{
    public EqualsEquals(){ super("==",25); }
    @Override
    public KObject compute(KObject o1 , KObject o2) {

        if( equalsEquals(o1, o2) ){
            return KObjectFactory.insertKObjectToSystem( new Bool(true),null );
        }
        else{
            return KObjectFactory.insertKObjectToSystem( new Bool(false),null );
        }
    }

    public static boolean equalsEquals( KObject o1 , KObject o2 ){

        if( o1 instanceof Bool && o2 instanceof Bool ){
            if( ((Bool)o1).get() == ((Bool)o2).get() ){
                return true;
            }
        }
        else if( o1 instanceof Num && o2 instanceof Num ){
            if( ((Num)o1).get() == ((Num)o2).get() ){
                return true;
            }
        }
        else if( o1 instanceof Direction && o2 instanceof Direction ){
            if( ((Direction)o1).get() == ((Direction)o2).get() ){
                return true;
            }
        }
        else if( o1 instanceof Box && o2 instanceof Box ){
            Box b1 = (Box) o1;
            Box b2 = (Box) o2;

            if( b1.inside().size() != b2.inside().size() ) return false;
            if( b1.isEmpty() && b2.isEmpty() ) return true;

            for( int i = 0 ; i < b1.inside().size() ; i++ ){
                if( !equalsEquals( o1.inside().get(i), o2.inside().get(i) ) ){
                    return false;
                }
            }
            return true;
        }

        return false;
    }
    
}




class Not extends UnarImplementation{
    public Not(){ super("not",6); }
    public KObject compute(KObject o) {
        if( o instanceof Bool ){
            ((Bool)o).negate();
        }
        return o;
    }
}

class DirSides extends UnarBinarImplementation{
    public DirSides(){ super("dirSides", 20); }
    public KObject[] compute(KObject o) {
        if( o instanceof Direction ){

            Direction ret1 = new Direction( Direction.Vals.randdir );
            Direction ret2 = new Direction( Direction.Vals.randdir );

            switch( ((Direction)o).get() ){
                case up :   ret1.set(Direction.Vals.left);
                            ret2.set(Direction.Vals.right);
                            break;
                case down : ret1.set(Direction.Vals.right);
                            ret2.set(Direction.Vals.left);
                            break;
                case right: ret1.set(Direction.Vals.up);
                            ret2.set(Direction.Vals.down);
                            break;
                case left : ret1.set(Direction.Vals.down);
                            ret2.set(Direction.Vals.up);
                            break;
            }

            KObjectFactory.insertKObjectToSystem(ret1, null);
            KObjectFactory.insertKObjectToSystem(ret2, null);

            return new KObject[]{ ret1 , ret2 };
        }
        return null;
    }
}

class RotCW extends UnarImplementation{
    public RotCW(){ super("rotCW",2); }
    public KObject compute(KObject o) {
        if( o instanceof Direction ){
            ((Direction)o).rotateCW();
        }
        return o;
    }
}

class Rot180 extends UnarImplementation{
    public Rot180(){ super("rot180",2);}
    public KObject compute(KObject o){
        if( o instanceof Direction ){
            ((Direction)o).rotateCW();
            ((Direction)o).rotateCW();
        }
        return o;
    }
}

class Rot extends BinarImplementation{
    public Rot(){ super("rot",20); }
    public KObject compute(KObject o1,KObject o2) {
        if( o1 instanceof Direction && o2 instanceof Direction ){
            Direction dir1 = (Direction) o1;
            Direction dir2 = (Direction) o2;

            switch( dir1.get() ){
                case down  : break;
                case right : dir2.rotateCW();
                             break;
                case up    : dir2.rotateCW();
                             dir2.rotateCW();
                             break;
                case left  : dir2.rotateCW();
                             dir2.rotateCW();
                             dir2.rotateCW();
                             break;
                case randdir: return dir1;
            }

            return dir2;
        }
        return null;
    }
}



//---
class Plus extends BinarImplementation {
    public Plus(){ super("+",28); }
    public KObject compute( KObject o1 , KObject o2 ) {
        if( o1 instanceof Num && o2 instanceof Num ){
            Num num1 = (Num) o1;
            Num num2 = (Num) o2;
            num1.set( num1.get() + num2.get()  );
        }
        return o1;
    }
}

class Minus extends BinarImplementation {
    public Minus(){ super("-",28); }
    public KObject compute( KObject o1 , KObject o2 ) {
        if( o1 instanceof Num && o2 instanceof Num ){
            Num num1 = (Num) o1;
            Num num2 = (Num) o2;
            num1.set( num1.get() - num2.get()  );
        }
        return o1;
    }
}

class Div extends BinarImplementation {
    public Div(){ super("div",28); }
    public KObject compute( KObject o1 , KObject o2 ) {
        if( o1 instanceof Num && o2 instanceof Num ){
            Num num1 = (Num) o1;
            Num num2 = (Num) o2;
            num1.set( num1.get() / num2.get()  );
        }
        return o1;
    }
}

class Plus3 extends TernarImplementation {
    public Plus3(){ super("plus3",30); }
    public KObject compute( KObject o1 , KObject o2,KObject o3 ) {
        if( o1 instanceof Num && o2 instanceof Num && o3 instanceof Num ){
            Num num1 = (Num) o1;
            Num num2 = (Num) o2;
            Num num3 = (Num) o3;
            num1.set( num1.get() + num2.get() + num3.get()  );
        }
        return o1;
    }
}



class Times extends BinarImplementation {
    public Times(){ super("*",28); }
    public KObject compute( KObject o1 , KObject o2 ) {
        if( o1 instanceof Num && o2 instanceof Num ){
            Num num1 = (Num) o1;
            Num num2 = (Num) o2;
            num1.set( num1.get() * num2.get()  );
        }
        return o1;
    }
}

class Const extends BinarImplementation {
    public Const(){ super("const",15); }
    public KObject compute( KObject o1 , KObject o2 ) {
        return o1;
    }
}

class Doubledot extends BinarImplementation {
    public Doubledot(){ super(":",28); }
    public KObject compute( KObject o1 , KObject o2 ) {

        o1.setParent(o2);
        o2.addFirst(o1);
        o2.step();

        return o2;
    }
}

class Pair extends BinarImplementation {
    public Pair(){ super("pair",22); }
    public KObject compute( KObject o1 , KObject o2 ) {

        Box box = new Box();
        KObjectFactory.insertKObjectToSystem(box, null);

        box.add(o1);
        box.add(o2);
        box.step();

        return box;

//        return KObjectFactory.insertKObjectToSystem(
//                Kisp.fromString( "( "+o1.toKisp() + " " + o2.toKisp()+" )" ) , null );
    }
}

class Unpair extends UnarBinarImplementation {
    public Unpair(){ super("unpair",22); }
    public KObject[] compute( KObject o ) {

        KObject o1 = o.popFirst();
        o.step();
        KObject o2 = o.popFirst();
        o.step();

        return new KObject[]{ o1 , o2 };
    }
}

class Tail extends UnarImplementation {
    public Tail(){ super("tail",2); }
    public KObject compute( KObject o ) {

        o.popFirst();
        o.step();

        return o;
    }
}
class Head extends UnarImplementation {
    public Head(){ super("head",2); }
    public KObject compute( KObject o ) {

        return o.popFirst();
    }
}

class Doubledot2 extends UnarBinarImplementation {
    public Doubledot2(){ super(":",28); }
    public KObject[] compute( KObject o ) {

        KObject first = o.popFirst();
        o.step();

        return new KObject[] { first , o };
    }
}


//---

class AppleSensor extends UnarImplementation{
    Function f;
    public AppleSensor( Function f ){
        super("appleSensor");
        this.f = f;
    }
    @Override
    public KObject compute(KObject o) {
        if( f.parent() instanceof Fly ){
            Fly fly = (Fly)f.parent();
            return fly.appleSensor();
        }
        return null;
    }
}
class WaspSensor extends UnarImplementation{
    Function f;
    public WaspSensor( Function f ){
        super("waspSensor");
        this.f = f;
    }
    @Override
    public KObject compute(KObject o) {
        if( f.parent() instanceof Fly ){
            Fly fly = (Fly)f.parent();
            return fly.waspSensor();
        }
        return null;
    }
}
class FlySensor extends UnarImplementation{
    Function f;
    public FlySensor( Function f ){
        super("flySensor");
        this.f = f;
    }
    @Override
    public KObject compute(KObject o) {
        if( f.parent() instanceof Fly ){
            Fly fly = (Fly)f.parent();
            return fly.flySensor();
        }
        return null;
    }
}

class GoalSensorImplementation extends NularImplementation {
    GoalSensor gs;
    public GoalSensorImplementation (Function f){
        super("goalSensor",-10);
        gs = (GoalSensor) f;
    }
    public KObject compute( ) {
        return KObjectFactory.insertKObjectToSystem( gs.getLastBullet() ,null );
    }
}

class TouchSensorImplementation extends NularImplementation {
    TouchSensor ts;
    public TouchSensorImplementation (Function f){
        super("touchSensor",-10);
        ts = (TouchSensor) f;
    }
    public KObject compute( ) {
        return KObjectFactory.insertKObjectToSystem( ts.getLastBullet() ,null );
    }
}

class ClockImplementation extends UnarImplementation {
    Clock s;
    public ClockImplementation(Function f){
        super("clock",0);
        s = (Clock) f;
    }
    public KObject compute( KObject o ) {

        KObject response = s.getResponse( o );

        if( response == null ) return null;

        return KObjectFactory.insertKObjectToSystem( response ,null );
    }
}

class IncubatorImplementation extends UnarImplementation {
    Incubator i;
    public IncubatorImplementation(Function f){
        super("incubator",-5);
        i = (Incubator) f;
    }
    public KObject compute( KObject o ) {
        return i.getFly();
    }
}

class FlyCmd extends UnarNularImplementation{
    Function f;
    public FlyCmd( Function f ){
        super("flyCmd", -1);
        this.f = f;
    }
    public void compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            ((Fly)parent).flyCmd(o);
        }
    }
}

class GetMem extends UnarImplementation {
    Function f;
    public GetMem( Function f ){
        super("getMem", 0);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            return fly.getMem();
        }
        return null;
    }
}

class TopMem extends UnarImplementation {
    Function f;
    public TopMem( Function f ){
        super("topMem", 0);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            return fly.topMem();
        }
        return null;
    }
}

class PopMem extends UnarImplementation {
    Function f;
    public PopMem( Function f ){
        super("popMem", 0);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            return fly.popMem();
        }
        return null;
    }
}

class PushMem extends UnarImplementation {
    Function f;
    public PushMem( Function f ){
        super("pushMem", 0);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            fly.pushMem(o);
            return o;
        }
        return null;
    }
}

class Read extends UnarImplementation {
    Function f;
    public Read( Function f ){
        super("read", 2);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            return fly.getDataFromActualPosition();
        }
        return null;
    }
}

class Write extends UnarImplementation {
    Function f;
    public Write( Function f ){
        super("write", 2);
        this.f = f;
    }
    public KObject compute( KObject o ) {
        KObject parent = f.parent();
        if( parent != null && parent instanceof Fly ){
            Fly fly = ((Fly)parent);
            fly.setDataForActualPosition( o );
        }
        return o;
    }
}


//---

class Log_ extends UnarNularImplementation{
    public Log_(){ super("log",7); }
    public void compute(KObject o) {
        Log.it( o.toXml() );
    }
}

class C42 extends UnarImplementation{
    public C42(){ super("c42"); }
    public KObject compute(KObject o) {
        return KObjectFactory.insertKObjectToSystem( new Num(42),null );
    }
}

class C2342 extends UnarBinarImplementation {
    public C2342(){ super("c2342"); }
    public KObject[] compute(KObject o) {
        return new KObject[]{
                              KObjectFactory.insertKObjectToSystem( new Num(23) ,null ),
                              KObjectFactory.insertKObjectToSystem( new Num(42) ,null )
                            };
    }
}

class LogXML extends UnarImplementation{
    public LogXML(){ super("logXML"); }
    public KObject compute(KObject o) {
        //return KObjectFactory.newKObject( "<object type=\"num\" val=\"42\" />" );
        Log.it( o.toXml() );
        return o;
    }
}

class Test extends UnarBinarImplementation {
    public Test(){ super("test.."); }
    public KObject[] compute(KObject o) {

        KObject num42 = KObjectFactory.newKObject( "<object type=\"num\" val=\"42\">" );

        return new KObject[]{ o,num42 };
    }
}

class Div2 extends UnarBinarImplementation{
    public Div2(){ super("div2.."); }
    public KObject[] compute(KObject o) {
        if( o instanceof Num ){
            Num num = (Num) o;
            num.set( num.get()/2 );
        }
        return new KObject[]{ o,o };
    }
}
