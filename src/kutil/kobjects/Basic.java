package kutil.kobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import kutil.core.*;
import kutil.items.BooleanItem;
import kutil.items.Int2DItem;
import kutil.items.Items;
import kutil.items.ListItem;
import kutil.items.StringItem;
import kutil.kevents.ClickEvent;
import kutil.kevents.Cmd;
import kutil.kevents.DragEvent;
import kutil.kevents.KEvent;
import kutil.kevents.ReleaseEvent;
import kutil.shapes.KShape;
import kutil.xml.Xml;
import kutil.xml.XmlElement;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.strategies.QuadSpaceStrategy;


/**
 * Základní typ objektu virtuálního světa.
 * Nyní od tohoto objektu dědí všechny (až na Text) ostatní objekty virtuálího světa.
 * Dedit od tohoto objektu není nutností, jen se to silně doporučuje.
 * Pro bytí objektem virtuálního světa se implementuje rozhraní KObject.
 * @author Tomáš Křen
 */
public class Basic implements KObject {

    private Items               items      ;   // drží všechny položky tohoto objektu pohromadě

    private StringItem          type       ;   // typ tohoto objektu
    private StringItem          id         ;   // id tohoto objektu
    private Int2DItem           pos        ;   // fyzická pozice tohoto objektu
    private StringItem          shapeCmd   ;   // příkaz definující tvar objektu
    private BooleanItem         physical   ;   // určuje zda se jedná o fyzickální objekt
    private BooleanItem         rotable    ;   // určuje zda objekt může být rotován - true nedoporučeno
    private BooleanItem         attached   ;   // určuje zda je fyzický objekt nepohyblivý(pro nefyz.nemá význam)
    private BooleanItem         movable    ;   // určuje zda je možno objektem hýbat myší
    private StringItem          bgcolorCmd ;   // barva pozadí
    private BooleanItem         main       ;   // určuje, zda se jedná o hlavní objekt
    private BooleanItem         takable    ;   // určuje zda je tento objekt budha umí sebrat
    private BooleanItem         guiStuff   ;   // určuje zda je tento objekt součást gui
                                               // a tedy s ním nejdou některé nepříjemnosti (smazat, vylíst nad)
    private StringItem          onInit     ;   // příkaz který se spustí při inicializaci kobjektu
    private StringItem          onEnter    ;   // příkaz který se spustí při vstupu dovnitř objektu
    private StringItem          ffType     ;   
    private BooleanItem         align15    ;   
    private BooleanItem         isBrick    ;
    private ListItem            inside     ;   // objekty tvořící vnitřek tohoto objektu

    private KObject             parent ;       // objekt, v jehož vnitřku je tento object
        
    private KShape              shape;         // tvar objektu
    private double              rot;           // rotace objektu
    private Color               bgcolor;       // barva pozadí vnitřku

    private boolean             isHighlighted ;  // udává, zda je objekt zvýrazněn (při o značení např.)

    private World               world ;  // fyzikální simulace uvnitř objektu
    private Body                body  ;  // těleso reprezentující tento objekt ve vnitřku parent objektu

    private LinkedList<KObject> objectsToAdd      ; //objekty čekající na přidaní na poslední místo do vnitřku
    private LinkedList<KObject> objectsToAddFirst ; //objekty čekající na přidaní na první místo do vnitřku
    private LinkedList<KObject> objectsToRemove   ; //objekty čekající na vymazání z vnitřku

    private String              oldCopyId;  // pomocná proměnná pužívaná při kopírování objektů
    private boolean             stepInside; // určuje, zda je vnitřním objektům tohoto objektu posílána
                                            // instrukce na udělání kroku
    private boolean             isAffectedByGravity; //určuje, zda je objekt ovlivněn gravitací

    private boolean             isInitialised; //určuje zda už proběhla inicializace metodou init()

    private boolean             clearItAfterAdding ; 
    private boolean             clearItBeforeAdding ;


    /**
     * Vytvoří nový základní objekt podle kAtts.
     * @param kAtts strukturovaný vstupní argument obsahující vstupní data
     */
    public Basic( KAtts kAtts ){

        items = new Items();

        type       = items.addString ( kAtts , "type"     , null         ) ;
        id         = items.addString ( kAtts , "id"       , null         ) ;
        pos        = items.addInt2D  ( kAtts , "pos"      , Int2D.zero() ) ;
        shapeCmd   = items.addString ( kAtts , "shape"    , null         ) ;
        bgcolorCmd = items.addString ( kAtts , "bgcolor"  , null         ) ;
        physical   = items.addBoolean( kAtts , "physical" , false        ) ;
        rotable    = items.addBoolean( kAtts , "rotable"  , false        ) ;
        attached   = items.addBoolean( kAtts , "attached" , false        ) ;
        movable    = items.addBoolean( kAtts , "movable"  , true         ) ;
        main       = items.addBoolean( kAtts , "main"     , false        ) ;
        takable    = items.addBoolean( kAtts , "takable"  , true         ) ;
        guiStuff   = items.addBoolean( kAtts , "guiStuff" , false        ) ;
        onInit     = items.addString ( kAtts , "onInit"   , null         ) ;
        onEnter    = items.addString ( kAtts , "onEnter"  , null         ) ;
        ffType     = items.addString ( kAtts , "ffType"   , null         ) ;
        align15    = items.addBoolean( kAtts , "align15"  , false        ) ;
        isBrick    = items.addBoolean( kAtts , "isBrick"  , false        ) ;
        
        inside     = items.addList   ( kAtts , "inside"                  ) ;

        create();
    }

    /**
     * Vytvoří nový imlicitní základní objekt.
     */
    public Basic(){

        items = new Items();
        
        type       = items.addString   ( "type"     , null          ) ;
        id         = items.addString   ( "id"       , null          ) ;
        pos        = items.addInt2D    ( "pos"      , Int2D.zero()  ) ;
        shapeCmd   = items.addString   ( "shape"    , null          ) ;
        bgcolorCmd = items.addString   ( "bgcolor"  , null          ) ;
        physical   = items.addBoolean  ( "physical" , false , false ) ;
        rotable    = items.addBoolean  ( "rotable"  , false , false ) ;
        attached   = items.addBoolean  ( "attached" , false , false ) ;
        movable    = items.addBoolean  ( "movable"  , true  , true  ) ;
        main       = items.addBoolean  ( "main"     , false , false ) ;
        takable    = items.addBoolean  ( "takable"  , true  , true  ) ;
        guiStuff   = items.addBoolean  ( "guiStuff" , false , false ) ;
        onInit     = items.addString   ( "onInit"   , null          ) ;
        onEnter    = items.addString   ( "onEnter"  , null          ) ;
        ffType     = items.addString   ( "ffType"   , null          ) ; 
        align15    = items.addBoolean  ( "align15"  , false , false ) ;
        isBrick    = items.addBoolean  ( "isBrick"  , false , false ) ;
        
        inside     = items.addEmptyList( "inside"                   ) ;

        create();
    }

    /**
     * Kopírovací konstruktor.
     */
    public Basic( Basic b ){

        items = new Items();

        type       = items.addString   ( "type"     , null                        ) ;
        id         = items.addString   ( "id"       , null                        ) ;
        pos        = items.addInt2D    ( "pos"      , b.pos.get().copy()          ) ;
        shapeCmd   = items.addString   ( "shape"    , b.shapeCmd.get()            ) ;
        bgcolorCmd = items.addString   ( "bgcolor"  , b.bgcolorCmd.get()          ) ;
        physical   = items.addBoolean  ( "physical" , b.physical.get() , false    ) ;
        rotable    = items.addBoolean  ( "rotable"  , b.rotable.get()  , false    ) ;
        attached   = items.addBoolean  ( "attached" , b.attached.get() , false    ) ;
        movable    = items.addBoolean  ( "movable"  , b.movable .get() , true     ) ;
        main       = items.addBoolean  ( "main"     , b.main.get()     , false    ) ;
        takable    = items.addBoolean  ( "takable"  , b.takable.get()  , true     ) ;
        guiStuff   = items.addBoolean  ( "guiStuff" , b.guiStuff.get() , false    ) ;
        onInit     = items.addString   ( "onInit"   , b.onInit.get()              ) ;
        onEnter    = items.addString   ( "onEnter"  , b.onEnter.get()             ) ;
        ffType     = items.addString   ( "ffType"   , b.ffType.get()              ) ; 
        align15    = items.addBoolean  ( "align15"  , b.align15.get()  , false    ) ;
        isBrick    = items.addBoolean  ( "isBrick"  , b.isBrick.get()  , false    ) ;
        
        inside     = items.addEmptyList( "inside" ) ;

        for( KObject o :  b.inside() ){

            KObject copy = o.copy();
            inside.get().add( copy );

        }

        oldCopyId = b.id();

        create();
    }

    /**
     * Vrací kopii tohoto objektu.
     * @return kopie objektu
     */
    public KObject copy(){
        return new Basic( this );
    }

    /**
     * Metoda volaná všemi konstruktory.
     * Po tom, co konstruktor určí, z jakých položek se daný objekt zkládá, zavolá
     * tuto metodu, která na základě těchto položek naincializuje ostatní proměnné.
     */
    private void create(){

        isInitialised = false;
        clearItAfterAdding = false;
        clearItBeforeAdding = false;

        if( main.get() ){
            Global.rucksack().setMain( this );
        }

        shape    = Global.shapeFactory().newKShape(shapeCmd.get());

        if( bgcolorCmd.get() != null ){
            String[] part = bgcolorCmd.get().split(" ");

            bgcolor = new Color( Integer.parseInt(part[0]),
                                 Integer.parseInt(part[1]),
                                 Integer.parseInt(part[2]));
        }else{
            bgcolor = Color.white;
        }
        
        if( align15.get() ){
            pos.set( pos.get().align(15) ); 
        }

        rot      = 0f;

        isHighlighted       = false;
        stepInside          = true;
        isAffectedByGravity = true;

        world = new World( new Vector2f(0.0f, 10.0f), 10, new QuadSpaceStrategy(20,5) );

        objectsToAdd      = new LinkedList<KObject>();
        objectsToAddFirst = new LinkedList<KObject>();
        objectsToRemove   = new LinkedList<KObject>();
    }

    /**
     * Tato metoda je volána při vytváření objektu pro informovaní objektu o jeho rodiči.
     */
    public void parentInfo( KObject parent ) {
        this.parent = parent;
        for( KObject o : inside.get() ){
            o.parentInfo( this );
        }
    }

    /**
     * Voláním této metody se dokončí druhá fáze inicializace objektu.
     * Inicializace objektu probíhá ve dvou fázích, první je zavolání konstrultoru,
     * druhá zavolání této metody. Důvodem je, že v době vytvoření tohoto objektu konstruktorem
     * ještě není známo, jaká id mají všechny objekty a tomu příbuzné problémy.
     */
    public void init(){

        if( isInitialised ) return;
        isInitialised = true;

        // pokud objekt nemá explicitní id, dostane nějaké přiděleno.
        if( id.get() == null ){
            id.set( idDB().getUniqueID() );
            idDB().put( id.get() , this);
            //Log.it("pridano implicitní id "+id.get() );
        }

        // pokud tento objekt vznikl jako kopie
        if( isCopied() ){
            Global.idChangeDB().put(oldCopyId, id.get() );
        }

        // pokud se jedná o fyzikální objekt:
        // - vytvoříme vhodné fyzikální těleso
        // - toto těleso vložíme do světa parent objektu
        if( physical.get() ){

            if( attached.get() ){
                body = new StaticBody( shape.getPhys2dShape() );
            } else {
                body = new Body( shape.getPhys2dShape() , 100.0f );
            }

            body.setGravityEffected(isAffectedByGravity);
            body.setRotatable( rotable.get() );
            body.setUserData( (Object) this );

            setBodyPos( pos.get() );


            World parentWorld = getParentWorld();
            if( parentWorld != null ){
                parentWorld.add(body);
            }
        }

        if( onInit.get() != null ){
            Global.rucksack().cmd( onInit.get() );
        }

        for( KObject o : inside.get() ){
            o.init( );
        }
    }

    /**
     * Převádí objekt do XML.
     * @return XML reprezentace tohoto objektu.
     */
    public Xml toXml(){
        XmlElement ret = new XmlElement("object");
        items.addAttsToXmlElement(ret);
        return ret;
    }

    /**
     * Vrací Kispovou reprezentaci tohoto objektu.
     * @return Kispová reprezentace objektu
     */
    public String toKisp(){

        StringBuilder sb = new StringBuilder();
        sb.append("( ");

        for( KObject o : inside.get() ){
            sb.append(o.toKisp());
            sb.append(" ");
        }

        sb.append(")");

        return sb.toString();
    }
    
    public String toKisp2(){
        String kispStr = toKisp();
        boolean addQuote = ( kispStr.charAt(0) == '(' );
        return ( addQuote ? "'" : "" ) + kispStr ;
    }
    
    public Basic getBasic(){
        return this;
    }

    /**
     * Vykoná jeden krok tohoto objektu.
     * Provede potřebné vlastní kroky (aktualizaci pozice,
     * krok fyzikální simulace svého vnitřku, přidá/odebere objekty čekající na odebrání)
     * a následně vybídne ke kroku své vnitřní objekty.
     */
    public void step() {

        if( physical.get() ){
            pos.set( shape.getPosByPhys2dCenter( body.getPosition() ) );
            rot = body.getRotation();
        }

        if( stepInside && Global.rucksack().isSimulationRunning() && world.getBodies().size() > 0 ){
            world.step();
            world.step();
            world.step();
            world.step();
            world.step();
        }

        if( clearItBeforeAdding ){
            internalClear();
            clearItBeforeAdding = false;
        }

        if( ! objectsToRemove.isEmpty() ){
            internalRemove();
        }

        if( ! objectsToAdd.isEmpty() ){
            internalAdd();
        }

        if( ! objectsToAddFirst.isEmpty() ){
            internalAddFirst();
        }

        if( clearItAfterAdding ){
            internalClear();
            clearItAfterAdding = false;
        }

        if( stepInside ){
            for( KObject o : inside.get() ){
                o.step();
                if( o instanceof Field ) manageField( (Field) o );
            }
        }
    }

    private void manageField(Field field){
        for( KObject ob : inside.get() ){
            
            
            
            if( !(ob instanceof Field) && field.isVisitedBy(ob) ){
                field.reactToObjectPresence(ob);
            }
        }
    }

    /**
     * Nastaví zda se mají vntřní objekty vybízet ke kroku.
     */
    public void setStepInside( boolean b ){
        stepInside = b;
    }
    /**
     * Vrací zda se mají vnitřní objekty vybízet ke kroku.
     */
    public boolean  getStepInside( ){
        return stepInside ;
    }
    /**
     * Nastaví zda je tento objekt ovlivněn gravitací.
     */
    public void setIsAffectedByGravity( boolean b ){
        isAffectedByGravity = b;
    }

    /**
     * Vrací zda je tento objekt sebratelný postavičkou řízenou hráčem.
     */
    public boolean isTakable(){
        return takable.get();
    }

    /**
     * Vrací příkaz prováďený při vstupu dovnitř objektu.
     */
    public String getOnEnterCmd(){
        return onEnter.get();
    }
    
    public String getFFType(){
        return ffType.get();
    }
    
    public boolean getAlign15(){
        return align15.get();
    }

    /**
     * Provede textový příkaz na tomto objektu.
     * @param cmd text příkazu
     * @return vrací textovou odpověď na daný příkaz
     */
    public String cmd( String cmd ){

        //Log.it( "["+ id.get() +" cmd]: "+ cmd );

        String[] parts = cmd.split( "\\s+" );
        String cmdName = parts[0];

        if( this instanceof Frame ){
            Frame thisFrame = (Frame) this ;
            
            if( Cmd.changeFrameTarget.equals(cmdName) ){
                thisFrame.resetTarget( parts[1] );
                return "Frame target změněn na "+parts[1]+".";
            }
            if( Cmd.showXML.equals(cmdName) ){
                thisFrame.toggleShowXML();
                return "Přepnuto.";
            }
        }

        return "Neplatný příkaz pro objekt "+id()+".";
    }

    /**
     * Vrací zda tento objekt vznikl kopírováním.
     */
    public boolean isCopied(){
        return oldCopyId != null ;
    }

    /**
     * Pokud objekt potřebuje zareagovat na to, že byl zkopírován, dělá to v této metodě.
     * Tato metoda volá tuto metodu svých vnitřních objektů.
     */
    public void resolveCopying(){
        if( isCopied() ){
            for( KObject o : inside.get() ){
                o.resolveCopying();
            }
        }
    }

    /**
     * Tato metoda je volána pokud došlo ke změnění id nějakého objektu.
     * Pokud na to potřebujuje zareagovat, reaguje na to v této metodě.
     * @param oldId původní id objektu
     * @param newId nové id objektu
     */
    public void resolveRenaming( String oldId , String newId ){

        for( KObject o : inside.get() ){
            o.resolveRenaming(oldId , newId );
        }

        if( id.get().equals(oldId) ){
            id.set(newId);
        }
    }

    /**
     * Vrací svět fyzikální simulace, který používá rodič tohoto objektu.
     */
    public World getParentWorld(){
        if( parent != null ){
            return parent.getWorld();
        }
        return null;
    }

    /**
     * Vrací textový řetězec s informacemi o objektu.
     */
    public String getInfoString(){
        return id.get() + " ["+pos.get()+"]";
    }

    /**
     * Vrací seznam vnitřních objektů tohoto objektu.
     */
    public LinkedList<KObject> inside(){
        return inside.get();
    }

    /**
     * Vrací položku obsahující vnitřní objekty.
     */
    public ListItem insideItem(){
        return inside;
    }

    /**
     * Vrací svět fyzikální simulace tohoto objektu.
     * Tento svět fyzikální simulace simuluje chování
     * vnitřních fyzických objektů tohoto objektu.
     */
    public World getWorld(){
        return world;
    }

    /**
     * Vrací těleso fyzikální simulace odpovídající tomuto objektu.
     */
    public Body getBody(){
        return body;
    }
    /**
     * Vrací z da se jedná o fyzický předmět.
     */
    public boolean isPhysical(){
        return physical.get();
    }


    private void setBodyPos( Int2D newPos ){
        if( body == null ) {return;}
        
        ROVector2f bodyPos = shape.getPhys2dCenter( newPos );
        body.setPosition( bodyPos.getX() , bodyPos.getY() );
    }


    /**
     * Vrací rodičovský objekt tohoto objektu (tzn. objekt součástí jehož vnitřku je tento objekt).
     */
    public KObject parent(){
        return parent;
    }

    /**
     * Přenastaví hodnotu rodičovského objektu.
     */
    public void setParent( KObject newParent ){
        parent = newParent;
    }

    /**
     * Vrací barvu pozadí vnitřku tohoto objektu.
     */
    public Color bgcolor(){
        return bgcolor;
    }

    /**
     * Nastavuje barvu pozadí vnitřku tohoto objektu.
     */
    public void setBgcolor(Color c){
        bgcolorCmd.set( c.getRed() + " " + c.getGreen() + " " +c.getBlue() );
        bgcolor = c;
        //Log.it( bgcolor );
    }

    /**
     * Nastavuje položku "type" tohoto objektu.
     */
    public void setType( String t ){
        type.set(t);
    }
    /**
     * Nastavuje zda se jedná o fyzický objekt.
     */
    public void setPhysical( boolean p ){
        physical.set( p );
    }
    /**
     * Nastavuje zda se jedná o objekt s fixní pozicí či volný objekt.
     */
    public void setAttached( boolean a ){
        attached.set( a );
    }
    /**
     * Nastavuje zda se jedná o objekt se kterým je možno hýbat v rámci GUI.
     */
    public void setMovable( boolean b ){
        movable.set(b);
    }
    /**
     * Nastavuje tvar objektu.
     */
    public void setShape( KShape s ){
        shape = s;
    }

    /**
     * Vrací tvar objektu.
     */
    public KShape getShape( ){
        return shape;
    }

    /**
     * Vrací zda je objekt rotovatelný.
     */
    public boolean getIsRotable(){
        return rotable.get();
    }
    
    /**
     * Vrací zda je objekt součást guiStuff, čili nejde mazat a opouštět backspacem.
     */
    public boolean getIsGuiStuff(){
        return guiStuff.get();
    }
    
    public boolean getIsBrick(){
        return isBrick.get();
    }

    /**
     * Nastavuje zda je objekt součást guiStuff, čili nejde mazat a opouštět backspacem.
     */
    public void setIsGuiStuff(boolean b){
        guiStuff.set(b);
    }

    /**
     * Vrací zda je objekt movable, tzn. určuje zda je možno objektem hýbat myší.
     */
    public boolean getIsMovable(){
        return movable.get();
    }

    /**
     * Nastavuje zda je objekt rotovatelný.
     */
    public void setIsRotable( boolean b ){
        rotable.set(b);
    }

    /**
     * Vrací id objektu.
     */
    public String id(){
        return id.get();
    }

    /**
     * Zkratka pro volání Global.idDB().
     */
    public final IdDB idDB(){
        return Global.idDB();
    }

    /**
     * Zkratka pro volání Global.rucksack().
     */
    public final Rucksack rucksack(){
        return Global.rucksack();
    }

    /**
     * Vrací balíček všech položek tohoto objektu.
     */
    public Items items(){
        return items;
    }

    /**
     * Vrací pozici tohoto objektu.
     */
    public Int2D pos(){
        return pos.get();
    }

    /**
     * Vrací rotaci tohoto objektu.
     */
    public double getRot(){
        return rot;
    }

    /**
     * Nastaví pozici tohoto objektu.
     */
    public void setPos( Int2D p ){
        pos.set(p);

        //if( physical.get() ){
            setBodyPos(p);
        //}
    }
    
    public void setSpeed( Int2D v ){
        if( body == null ) {return;}
        
        ROVector2f vel = body.getVelocity();
        
        body.adjustVelocity( new Vector2f( v.getX() - vel.getX() , v.getY() - vel.getY() ) );
    }

    /**
     * Nastaví přímo pozici tohoto objektu, bez nastavování pozice tělesa fyzikální simulace (HAX).
     */
    public void setPosDirect( Int2D p ){
        pos.set(p);
    }

    /**
     * Nastaví zda je objekt označen.
     */
    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    /**
     * Vrátí zda je objekt označen.
     */
    public boolean isHighlighted(){
        return isHighlighted;
    }

    /**
     * Vykreslí vnitřek objektu.
     * @param g Graphics2D do které chceme vykreslit
     * @param center pozice středu vykreslování
     * @param frameDepth hloubka vykreslení (zabraňuje vykreslení nekonečné smyčky frámů)
     */
    public void drawInside( Graphics2D g , Int2D center , int frameDepth ){
        for( KObject o : inside.get() ){
            o.drawOutside(g, center , frameDepth );
        }
    }

    /**
     * Vykreslí vnější podobu objektu na základě jeho tvaru.
     * @param g Graphics2D do které chceme vykreslit
     * @param center pozice středu vykreslování
     * @param frameDepth hloubka vykreslení (zabraňuje vykreslení nekonečné smyčky frámů)
     */
    public void drawOutside( Graphics2D g , Int2D center , int frameDepth ){
        shape.draw( g, isHighlighted , getInfoString() , pos.get() , center , rot , rotable.get() );
    }

    /**
     * Vrací, zda je objekt zasažen kliknutím.
     * @param clickPos pozice kliknutí
     * @return zda byl objekt zasažen
     */
    public boolean isHit( Int2D clickPos ){
        return shape.isHit(pos.get(), clickPos, rot);
    }

    /**
     * Reaguje na událost uvnitř objektu.
     * @param kEvent událost, na kterou se má reagovat
     */
    public void event( KEvent kEvent ){
        if     ( kEvent instanceof ClickEvent   ){ clickEvent    ( (ClickEvent  ) kEvent ); }
        else if( kEvent instanceof DragEvent    ){ dragEvent     ( (DragEvent   ) kEvent ); }
        else if( kEvent instanceof ReleaseEvent ){ releaseEvenent( (ReleaseEvent) kEvent ); }
    }

    private void clickEvent( ClickEvent e ){
        
        rucksack().setSelectedFrame( e.getFrame() );
        
        Iterator<KObject> iter = inside.get().descendingIterator();
        while ( iter.hasNext() ){
            KObject o = iter.next();
            if( o.isHit( e.getPos() ) ){
                o.click( e.getPos() );
                return;
            }
        }
        rucksack().setSelected( e.getFrame() );
    }

    private void dragEvent( DragEvent e ){

        rucksack().setSelectedFrame( e.getFrame() );
        rucksack().onDrag( e.getPos() );

        Iterator<KObject> iter = inside.get().descendingIterator();
        while ( iter.hasNext() ){
            KObject o = iter.next();
            if( o.isHit( e.getPos() ) ){
                o.drag( e.getPos() , e.getDelta() , e.getFrame() );
                return;
            }
        }
        if( ! rucksack().somethingOnCursor() ){
            e.getFrame().moveCam(e.getDelta());
        }
        rucksack().setSelected( e.getFrame() );
    }

    private void releaseEvenent( ReleaseEvent e ){

        Iterator<KObject> iter = inside.get().descendingIterator();
        while ( iter.hasNext() ){
            KObject o = iter.next();
            if( o.isHit( e.getPos() ) ){

                if( !( this instanceof Frame ) && ! o.getIsMovable() ){
                    
                    //Log.it("1");
                    
                    rucksack().pasteFromCursor(this, e.getPos() );
                    return;
                }

                //Log.it("2");
                
                o.release(e.getPos(),this);
                
                //rucksack().pasteFromCursor(this, e.getPos() ); // přidáno nedávno po problémech se vkládáním na kobjekt
                
                return;
            }
        }
        
        //Log.it("3");
        rucksack().pasteFromCursor(this, e.getPos() );
    }

    /**
     * Reakce na táhnutí myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice začátku táhnutí
     * @param delta vektor táhnutí (kolik se táhlo)
     */
    public void drag( Int2D clickPos, Int2D delta , Frame f ){
        if( movable.get() ){
            rucksack().cutToCursor( this , clickPos );
        }
        else{
            if( rucksack().somethingOnCursor() ) return;
            rucksack().setSelected(this);
            f.moveCam(delta);
        }
    }

    /**
     * Reakce na kliknutí myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice kliknutí
     */
    public void click(Int2D clickPos) {
        rucksack().setSelected(this);
    }

    /**
     * Reakce na pustění tlačítka myši na samotném objektu (na jeho vnějšku).
     * @param clickPos pozice puštění
     */
    public void release(Int2D clickPos , KObject obj ){
        //rucksack().pasteFromCursor(this , Int2D.zero ); //pokud tam je tak umožnuje vkladat dovnitř
                                                          // objektu pouhým přetažením
        
       rucksack().pasteFromCursor( obj , clickPos ); 
    }

    /**
     * Úplně smaže objekt.
     */
    public void delete(){
        remove();
        idDB().remove(id());
    }
    /**
     * Odstraní objekt ze svého rodiče.
     */
    public void remove(){
        if( parent != null ){
            parent.remove(this);
        }
    }
    /**
     * Odstraní daný objekt z vntřku tohoto objektu.
     * @param o objekt k vymazání
     */
    public void remove( KObject o ){
        objectsToRemove.add(o);
    }
    /**
     * Přidá daný objekt do vntřku (na konec) tohoto objektu.
     * @param o objekt k přidání
     */
    public void add( KObject o ){
        objectsToAdd.add(o);
    }
    /**
     * Přidá daný objekt do vntřku (na začátek) tohoto objektu.
     * @param o objekt k přidání
     */
    public void addFirst( KObject o ){
        objectsToAddFirst.add(o);
    }
    /**
     * Smaže první objekt z vnitřku.
     * @return sazaný objekt
     */
    public KObject popFirst( ){

        if( inside.get().isEmpty() ){
            return null;
        }else{
            KObject first = inside.get().getFirst();
            remove( first );
            return first;
        }
    }
    /**
     * Smaže poslední objekt z vnitřku.
     * @return sazaný objekt
     */
    public KObject popLast( ){

        if( inside.get().isEmpty() ){
            return null;
        }else{
            KObject last = inside.get().getLast();
            remove( last );
            return last;
        }
    }

    /**
     * Přímé přidání objektu do vnitřku (na konec).
     * Není bezpečné jako reakce na chování v GUI, odo toho je add.
     * @param o přidávaný objekt
     */
    public void directAdd( KObject o ){
        inside.get().add(o);
    }

    /**
     * Přímé přidání objektu do vnitřku (na začátek).
     * Není bezpečné jako reakce na chování v GUI, odo toho je add.
     * @param o přidávaný objekt
     */
    public void directAddFirst( KObject o ){
        inside.get().addFirst(o);
    }

    /**
     * Přímé smazání objektů vnitřku.
     */
    public void directClear( ){
        inside.get().clear();
        //objectsToAdd.clear();
    }


    /**
     * Smaže všechny objekty vnitřku po přidávaní.
     */
    public void clearAfterAdding(){
        clearItAfterAdding = true;
    }

    /**
     * Smaže všechny objekty vnitřku před přidávaním.
     */
    public void clearBeforeAdding(){
        clearItBeforeAdding = true;
    }

    private void internalClear(){
        inside().clear();
        world.clear();
    }

    private void internalAdd(){

        for( KObject o : objectsToAdd ){

            if( o.isPhysical() ){
                world.add( o.getBody() );
            }
            inside.get().add( o );

            //Log.it("adding "+o.id()+" velikost inside: "+inside.get().size());
        }

        objectsToAdd.clear();
    }
    private void internalAddFirst(){

        for( KObject o : objectsToAddFirst ){

            if( o.isPhysical() ){
                world.add( o.getBody() );
            }
            inside.get().addFirst( o );
        }

        objectsToAddFirst.clear();
    }
    private void internalRemove(){

        for( KObject o : objectsToRemove ){

            for( KObject f : inside.get() ){
                if( f instanceof Field ) ( (Field) f ).informFieldAboutDeletation(o);
            }
            
            if( o.isPhysical() ){
                world.remove( o.getBody() );
            }
            inside.get().remove( o );
            
            

            //Log.it("deleting "+o.id()+" velikost inside: "+inside.get().size());
        }
        

        objectsToRemove.clear();
    }
    
    
    public void setPhysicalOff(){
        if( isPhysical() ){
            getParentWorld().remove( getBody() );
            physical.set(false);
        }
    }

    public void setPhysicalOn(){
        if( ! isPhysical() ){
            getParentWorld().remove( getBody() );
            physical.set(true);
            
            setBodyPos( pos() );
            getParentWorld().add(body);
        }
    }
    
    public void glueBricks(){
        
        List<KObject> bricks = new LinkedList<KObject>();
        
        for( KObject o : parent().inside() ){
            if( o.getBasic().getIsBrick() ){
                o.remove();
                bricks.add( o );
            }
        }
        
        MultiObject multi = new MultiObject(bricks);
        multi.setPhysical( true );
        multi.getBasic().align15.set(true);
        
        KObjectFactory.insertKObjectToSystem( multi , null );
        multi.setParent( parent() );
        parent().add( multi );
               
        
        
    }


}
