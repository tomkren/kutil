package kutil.core;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import kutil.functions.ErroneousImplementation;
import kutil.functions.FakeImplementation;
import kutil.kevents.Cmd;
import kutil.kobjects.Basic;
import kutil.kobjects.Figure;
import kutil.kobjects.Frame;
import kutil.kobjects.Function;
import kutil.kobjects.Inputable;
import kutil.kobjects.KObject;
import kutil.kobjects.KObjectFactory;
import kutil.kobjects.Outputable;

/**
 * Instance třídy Rucksack má důležitou roli při interakci s uživatelem,
 * umožňuje manipulaci s objekty pomocí myši,
 * implemetuje schránku,
 * zajišťuje ovládání běhu simulace,
 * zajišťuje pohyb zpět či vpřed v historii editačních změn,
 * zajišťuje interakci s textovou konzolí programu,
 * zajišťuje dialog pro ukládání a otevírání stavu,
 * drží informaci o označených objektech, atd.
 * V programu se používá jediná instance přístupná přes třídu Global.
 * @author Tomáš Křen
 */
public class Rucksack implements ActionListener{

    private Scheduler  scheduler; // plánovač, defacto vrchol hierarche KObjektů

    private KObject    selected;      // označený KObjekt - projevuje se červeným okrajem
    private Frame      selectedFrame; // označený Frame   - projevuje se zeleným okrajem

    private KObject    inClipboard; // KObject ve schránce

    private KObject    onCursor;      // KObject vysící na kurzoru
    private Int2D      onCursorClickPos; //relativní pozice kliknutí vůči pozici KObjectu na kurzoru
    private boolean    lastTimeCut; //zda bylo naposledy vyjmuto (HAX)

    private boolean    showInfo; // zda ukazovat u objektů stručné info
    private boolean    isSimulationRunning; // zda běží simulace

    private Int2D      onDragAbsPos; // absolutní pozice kurzoru při přesouvání
    private Int2D      onDragPos;    // relativní pozice kurzoru při přesouvání

    private int mouseX; // x-pozice myši
    private int mouseY; // y-pozice myši

    private Outputable from;     //z jakého Outputable KObjektu je nyní propojováno
    private int        fromPort; // a z jako portu v rámci tohoto Outputable KObjektu
    
    private JFrame popupFrame;         // reference na pomocný frame při vytváření popup okna (pravé tlačítko)
    private Int2D  rightClickLocation;// pozcie kde se kliko pravym tlacitkem

    private LinkedList<String> undoBuffer; //buffer editační historie směrem do minulosti
    private LinkedList<String> redoBuffer; //buffer editační historie směrem do budoucnosti

    private KObject main; // reference na KObject do kterého se nahravá ze souboru
                          // a u kterého se zaznamenává editační historie

    private Console console; // reference na otevřenou textovou konzoli

    private JFileChooser fileChooser; //reference na dialog save/load

    private Figure actualFigure; //aktivní figurka (právě ovládaný)

    /**
     * Vytvoří nový rucksack.
     * Tato metoda je volána jen z Global.init()
     */
    public Rucksack(){
        
        selected            = null;
        selectedFrame       = null;
        showInfo            = false;
        onCursor            = null;
        popupFrame          = null;
        isSimulationRunning = false;
        console             = null;
        actualFigure         = null;

        fileChooser         = new JFileChooser(  );
        try{
            File f = new File(new File(".").getCanonicalPath());
            fileChooser.setCurrentDirectory(f);
        } catch(Exception e){}

        undoBuffer = new LinkedList<String>();
        redoBuffer = new LinkedList<String>();

        resetFrom();
    }

    /**
     * Provede globální příkaz.
     * @param cmd textová forma příkazu
     */
    public LinkedList<String> cmd( String cmd ){
        String[] cmds = cmd.split(";");

        LinkedList<String> ret = new LinkedList<String>();

        for( int i = 0 ; i<cmds.length ; i++ ){
            ret.add( oneCmd( cmds[i] ) );
        }

        return ret;
    }

    private String oneCmd( String cmd ){
        //Log.it( "[cmd]: "+ cmd );

        cmd = cmd.trim();

        String[] parts = cmd.split( "\\s+" );
        String cmdName = parts[0];

        if( Cmd.sendTo.equals(cmdName) ){
            if( parts.length < 3 ) return "U sendTo musí být id a příkaz.";
            KObject o = Global.idDB().get( parts[1] );
            if( o != null ){
                return o.cmd( cmd.split("\\s+" , 3)[2] );
            }
            return parts[1] + " není platné id.";
        }
        if( Cmd.changeXML.equals(cmdName) ){
            if( parts.length != 3 ) return Cmd.changeXML+" potřebuje 2 argumenty.";
            changeXML( parts[1] , parts[2] , true );
            return "XML změněno.";
        }
        if( Cmd.play.equals(cmdName) ){
            togglePausePlay();
            return "Přepnuto.";
        }
        if( Cmd.showInfo.equals(cmdName) ){
            toggleShowInfo();
            return "Přepnuto.";
        }
        if( Cmd.console.equals(cmdName) ){
            openConsole();
            return "Konzole otevřena.";
        }
        if( Cmd.undo.equals(cmdName) ){
            undo();
            return "Undone.";
        }
        if( Cmd.redo.equals(cmdName) ){
            redo();
            return "Redone.";
        }
        if( Cmd.rename.equals(cmdName) ){
            if( parts.length != 3 ) return Cmd.rename+" potřebuje 2 argumenty.";
            return ( rename( parts[1], parts[2] )
                    ? "Povedlo se přejmenovat."
                    : "Nepovedlo se přejmenovat." );
        }
        if( Cmd.load.equals(cmdName) ){
            return load();
        }
        if( Cmd.save.equals(cmdName) ){
            return save();
        }
        if( Cmd.xml.equals(cmdName) ){
            if( parts.length < 2 ) return "U "+Cmd.xml + " musí být argument (vkládané xml).";
            String xml = cmd.split("\\s+" , 2)[1] ;
            fromXmlToCursor(xml);
            return "XML (snad) vloženo jako KObject.";
        }
        if( Cmd.bgcolor.equals(cmdName) ){
            if( parts.length < 3 ) return "U "+Cmd.bgcolor + " musí být 2 argumenty: [id] [color r g b]";
            String[] ps = cmd.split("\\s+" , 3);
            return changeBgcolor( ps[1] , ps[2] );
        }
        if( Cmd.selectedFrameTarger.equals(cmdName) ){
            if( parts.length < 2 ) return "U "+Cmd.selectedFrameTarger + " musí být 1 argumenty: [id]";
            return changeActualFrameTarget(parts[1]);
        }

        return "Neplatný příkaz.";
    }

    /**
     * Zpřístupňuje kódy všech použitých kláves.
     * @return pole kódů kláves
     */
    public String[] getKeyNames(){
        return keyNames;
    }

    private static final String[] keyNames =
            new String[]{ "ENTER", "BACK_SPACE", "I", "DELETE", "P" , "SPACE" ,
                          "control Z","control shift Z" , "F2" , "control R",
                          "LEFT","RIGHT","UP","DOWN", "B" ,
                           "control LEFT","control RIGHT","control UP","control DOWN",
                           "shift LEFT","shift RIGHT","shift UP","shift DOWN",
                           "control shift LEFT","control shift RIGHT","control shift UP","control shift DOWN"
                        };


    /**
     * Implementuje reagování na zmačnutí klávesy (nebo kombinace kláves).
     * @param str kód klávesy
     */
    public void keyboardEvent( String str ){
        if     ( "ENTER"                .equals(str) ){ enter     ();      }
        else if( "BACK_SPACE"           .equals(str) ){ backspace ();      }
        else if( "I"                    .equals(str) ){ toggleShowInfo();  }
        else if( "DELETE"               .equals(str) ){ delete();          }
        else if( "P"                    .equals(str) ){ togglePausePlay(); }
        else if( "SPACE"                .equals(str) ){ openConsole();     }
        else if( "F2"                   .equals(str) ||
                 "control R"            .equals(str) ){ renameDialog();    }
        else if( "control Z"            .equals(str) ){ undo();            }
        else if( "control shift Z"      .equals(str) ){ redo();            }
        else if( "LEFT"                 .equals(str) ){ figureCmd(Figure.FigureCmd.left);  }
        else if( "RIGHT"                .equals(str) ){ figureCmd(Figure.FigureCmd.right); }
        else if( "UP"                   .equals(str) ){ figureCmd(Figure.FigureCmd.up);    }
        else if( "DOWN"                 .equals(str) ){ figureCmd(Figure.FigureCmd.down);  }
        else if( "control LEFT"         .equals(str) ){ allFigureCmd(Figure.FigureCmd.left);  }
        else if( "control RIGHT"        .equals(str) ){ allFigureCmd(Figure.FigureCmd.right); }
        else if( "control UP"           .equals(str) ){ allFigureCmd(Figure.FigureCmd.up);    }
        else if( "control DOWN"         .equals(str) ){ allFigureCmd(Figure.FigureCmd.down);  }
        else if( "shift LEFT"           .equals(str) ){ figureCmd(Figure.FigureCmd.shiftLeft);  }
        else if( "shift RIGHT"          .equals(str) ){ figureCmd(Figure.FigureCmd.shiftRight); }
        else if( "shift UP"             .equals(str) ){ figureCmd(Figure.FigureCmd.shiftUp);    }
        else if( "shift DOWN"           .equals(str) ){ figureCmd(Figure.FigureCmd.shiftDown);  }
        else if( "control shift LEFT"   .equals(str) ){ allFigureCmd(Figure.FigureCmd.shiftLeft);  }
        else if( "control shift RIGHT"  .equals(str) ){ allFigureCmd(Figure.FigureCmd.shiftRight); }
        else if( "control shift UP"     .equals(str) ){ allFigureCmd(Figure.FigureCmd.shiftUp);    }
        else if( "control shift DOWN"   .equals(str) ){ allFigureCmd(Figure.FigureCmd.shiftDown);  }
        else if( "B"                    .equals(str) ){ changeFigure();  }

        //KeyEvent.VK_

        else   { Log.it("ERR: neodchycena klavesa"); }
    }


      /**
     * Pro implemetaci ActionListener.
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();

        //Log.it("popup action : " + cmd );

        if( "copy".equals(cmd) ){
            inClipboard = selected;
        }
        else if( "undo".equals(cmd) ){
            undo();
        }
        else if( "paste to cursor".equals(cmd) ){
            copyToCursor(inClipboard);
        }
        else if("copy to cursor".equals(cmd)){
            copyToCursor(selected);
        }
        else if( "console".equals(cmd) ){
            openConsole();
        }
        else if( "show XML".equals(cmd) ){
            openEditor( selected.toXml().toString() );
        }
        else if( "show Kisp".equals(cmd) ){
            openEditor( selected.toKisp() );
        }
        else if( "add in and out".equals(cmd) ){
            ((Function)selected).addInAndOut();
        }

        if( popupFrame != null ){
            popupFrame.dispose();
        }
    }

   
    /**
     * Informuje rucksack o tom, že bylo kliknuto pravé tlačítko, aby
     * věděl že má otevřít popup.
     * @param frame Frame ve kterém bylo kliknuto
     * @param mouseX x-ová pozice myši na obrazovce
     * @param mouseY y-ová pozice myši na obrazovce
     */
    public void clickedRightMouseButton( Frame frame , int mouseX , int mouseY ){
        //Log.it("[RIGHT MOUSE BUTTON]");
        //Log.it("selected id : "+selected.id());

        if( popupFrame != null ){
            popupFrame.dispose();
        }

        popupFrame = new JFrame("popup");
        popupFrame.setLocation(0,0);
        popupFrame.setVisible(true);

        JPopupMenu popup = new JPopupMenu();

        if( selected instanceof Function ){
            addItemToPopup("add in and out", popup);
        }

        addItemToPopup("undo", popup);
        addItemToPopup("copy", popup);
        addItemToPopup("paste to cursor", popup);
        addItemToPopup("copy to cursor", popup);
        addItemToPopup("console", popup);
        addItemToPopup("show XML", popup);
        addItemToPopup("show Kisp", popup);
        Point p = frame.getJPanel().getLocationOnScreen();
        rightClickLocation = new Int2D( p.x+mouseX, p.y+mouseY );
        popup.show( popupFrame , rightClickLocation.getX() , rightClickLocation.getY()  );

    }

    private void addItemToPopup( String cmd , JPopupMenu popup ){
        JMenuItem menuItem = new JMenuItem( cmd );
        menuItem.addActionListener( this );
        menuItem.setActionCommand( cmd );
        popup.add(menuItem);
    }

       /**
     * Tuto metodu volá konzole, když chce aby byl obsloužen příkaz, který do ní
     * byl zadán.
     * @param console
     * @param cmd
     */
    public void handleConsoleCmd( Console console, String cmd ){

        cmd = cmd.trim();

        if( cmd == null || cmd.equals("") )return;

        if( cmd.charAt(0) == '!' ){

            LinkedList<String> logs = cmd( cmd.substring(1).trim() );

            for( String s : logs ){
                console.printString( s );
            }
        }
        else{

            KObject o = null;

            Function f = new Function(cmd);

            if( f.getImplementation() instanceof FakeImplementation ){
                o = ( (FakeImplementation) (f.getImplementation()) ).get();
            }
            else if( f.getImplementation() instanceof  ErroneousImplementation ){
                o = null;
            }
            else {
                o = f;
            }


            if( o != null ){

                KObjectFactory.insertKObjectToSystem(o, null);

                onCursor         = o;
                onCursorClickPos = Int2D.zero;

                setSelected( onCursor );

                lastTimeCut = false;

                console.printString("Ok!");
            }
            else {
                console.printString("Neplatný výraz Kispu.");
            }
        }
    }


    /**
     * Nastav aktuálně ovládanou figurku
     * @param f figurka která bude od ted ovládán
     */
    public void setActualFigure( Figure f ){
        actualFigure = f;
    }

    
    private int figureIndex = 0;
    private void changeFigure(){
        changeFigure(null);
    }
    private void changeFigure( Figure dontSelect ){

        if( selectedFrame == null ) return;

        LinkedList<Figure> figureList = getFigureList();

        if( figureList.size() > 0 ){

            figureIndex = (figureIndex + 1) % figureList.size();

            actualFigure = figureList.get( figureIndex );

            if( actualFigure == dontSelect ){
                actualFigure = figureList.get( (figureIndex+1)%figureList.size() );
            }

            actualFigure.figureCmd(Figure.FigureCmd.up);
            actualFigure.figureCmd(Figure.FigureCmd.up);

        }else{
            actualFigure = null;
        }
    }

    private LinkedList<Figure> getFigureList(){

        KObject t = selectedFrame.getTarget();

        LinkedList<Figure> figureList = new LinkedList<Figure>();

        for( KObject o : t.inside() ){
            if( o instanceof Figure ){
                figureList.add((Figure)o);
            }
        }

        return figureList;
    }

    private void figureCmd( Figure.FigureCmd cmd ){
        if( actualFigure == null ) return;
        if( actualFigure.parent() != selectedFrame.getTarget() ) return;

        actualFigure.figureCmd(cmd);
    }

    private void allFigureCmd( Figure.FigureCmd cmd ){

        for( Figure b : getFigureList() ){
            b.figureCmd(cmd);
        }
    }

    private String load(){
        if ( fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            changeXML(main.id(), file.getPath(), false );

            return "[LOAD] "+ file.getPath();
            
        } else {
            return "[LOAD] Zrušeno uživatelem.";
        }
    }

    private static final String firstLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private String save(){
        if ( fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();


            StringBuilder sb = new StringBuilder();
            for( KObject o : ((Basic)main).inside() ){
                sb.append( o.toXml().toString() );
            }

            String xml = firstLine + "<kutil>\n"+ sb.toString() +"</kutil>" ;


            try {

                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));

                String[] rows = xml.split("\n");

                for( String row : rows ){
                    out.write( row );
                    out.newLine();
                }


                out.close();
            }
            catch (IOException e) {}

            return "[SAVE] "+ file.getPath();

        } else {
            return "[SAVE] Zrušeno uživatelem.";
        }
    }

    private boolean rename( String oldId , String newId ){

        boolean succes = Global.idDB().rename(oldId, newId);
        if( ! succes ) return false;

        LinkedList<KObject> os = scheduler.getKObjects();

        for( KObject o : os ){
            o.resolveRenaming(oldId, newId);
        }

        return true;
    }

    private void renameDialog(){
        if( selected != null ){
            openConsole("!rename "+selected.id() +" " );
        }
    }

    private void saveStateToUndoBuffer(){
        if( main != null ){
            undoBuffer.addFirst( main.toXml().toString() );
            redoBuffer.clear();
            //Log.it("saving");
        }
    }

    private void undo(){
        if( main != null && (! undoBuffer.isEmpty() ) ){

            String lastState = undoBuffer.getFirst();
            undoBuffer.removeFirst();
            redoBuffer.addFirst( main.toXml().toString() );

            loadToMain(lastState);
        }
    }

    private void redo(){
        if( main != null && (! redoBuffer.isEmpty() ) ){

            String nextState = redoBuffer.getFirst();
            redoBuffer.removeFirst();
            undoBuffer.addFirst( main.toXml().toString() );

            loadToMain(nextState);
        }
    }

    private void loadToMain( String xmlString ){

        //Log.it(xmlString);

        KObject parent = main.parent();

        main.delete();

        KObject o = KObjectFactory.newKObject(xmlString);
        parent.add( o );
        o.setParent(parent);

        isSimulationRunning = false;
    }

    private String changeBgcolor( String id , String rgb ){

        KObject obj = Global.idDB().get(id);
        if( obj == null || !(obj instanceof Basic) ) return "neplatné [id]";
        
        ((Basic)obj).setBgcolor( Utils.parseColor(rgb) );

        return "barva změněna.";
    }

    private String changeActualFrameTarget(String id){
        selectedFrame.resetTarget(id);
        return "Sand se povedlo změnit id actualního framu.";
    }

    private void changeXML( String id , String filename , boolean isInternal ){

        KObject obj = Global.idDB().get(id);
        if( obj == null || !(obj instanceof Basic) ) return;

        if( obj instanceof Frame ){
            ((Frame)obj).setCam( Int2D.zero() );
        }

        isSimulationRunning = false;

        Basic basic = (Basic) obj;

        basic.clearBeforeAdding();

        LinkedList<KObject> list = null;

        if( isInternal){
            list = KObjectFactory.newKObjectFromResource(filename);
        } else {
            list = KObjectFactory.newKObjectFromFile(filename);
        }

        for( KObject o : list ){

            KObjectFactory.insertKObjectToSystem( o , basic);
            basic.add( o );
        }

        undoBuffer.clear();
        redoBuffer.clear();
    }

    public void setMain( KObject o ){
        main = o;
    }



   

    private void openConsole(){

        if( console == null ){
            console = new Console(new Int2D(12,26));
        }
        else{
            console.requestFocus();
        }
    }

    private void openConsole( String cmd ){
        openConsole();
        console.setCmd(cmd);
    }

    /**
     * Zavírá textovou konzoli programu.
     */
    public void closeConsole(){
        console.getJFrame().dispose();
        console = null;
    }

    private void openEditor( String str ){
        Editor ed = new Editor( new Int2D(100,100) , str );
    }

 

  
    /**
     * Dovoluje nastavit Outputable KObject, kterému je nyní nastavováno cílové napojení
     * @param f Outputable KObject, kterému je nyní nastavováno cílové napojení
     * @param fp port (číslo výstupu od 0 počínaje) ze kterého se propojjuje
     */
    public void setFrom( Outputable f , int fp ){

        saveStateToUndoBuffer();

        from = f;
        fromPort = fp;
    }
    /**
     * Vrací Outputable KObject, kterému je nyní nastavováno cílové napojení
     * @return Outputable KObject, kterému je nyní nastavováno cílové napojení
     */
    public Outputable getFrom( ){
        return from;
    }
    /**
     * Vrací číslo portu v rámci Outputable KObject, kterému je nyní nastavováno cílové napojení
     * @return číslo portu (číslo výstupu od 0 počínaje)
     */
    public int getFromPort( ){
        return fromPort;
    }
    /**
     * Vynuluje from -> cílové napojení není právě nastavováno.
     */
    public final void resetFrom(){

        from = null;
        fromPort = -1;
    }

    /**
     * zkopíruje KObjekt na kurzor.
     * @param o co se má zkopírovat
     */
    public void copyToCursor( KObject o ){

        if( onCursor != null ) return;
        
        KObject copy = o.copy();

        KObjectFactory.insertKObjectToSystem(copy,null);

        onCursor         = copy;
        onCursorClickPos = Int2D.zero;//  onCursor.pos().minus(clickPos);

        setSelected( onCursor );

        lastTimeCut = false;
    }

    /**
     * Vyjmout na kurzor.
     * @param o co se má vyjmout
     * @param clickPos pozice kliknutí
     */
    public void cutToCursor( KObject o , Int2D clickPos ){

        if( onCursor != null ) return;

        KObject parent = o.parent();
        if( parent == null ) return;

        saveStateToUndoBuffer();

        parent.remove(o);
        onCursor = o;
        onCursorClickPos = onCursor.pos().minus( clickPos );

        setSelected(onCursor);

        lastTimeCut = true;
    }

    private void fromXmlToCursor( String xmlStr ){

        KObject o = KObjectFactory.newKObject( xmlStr );

        //KObjectFactory.insertKObjectToSystem(o, null);

        onCursor         = o;
        onCursorClickPos = Int2D.zero;

        setSelected( onCursor );

        lastTimeCut = false;
    }

    /**
     * Vlož z kurzoru do virtuálního světa.
     * @param newParent nový rodičovský KObbject
     * @param clickPos pozice kam bylo kliknuto pro vložení
     */
    public void pasteFromCursor( KObject newParent , Int2D clickPos ){
        if( onCursor == null ) return;

        //saveStateToUndoBuffer();

        onCursor.setPos( clickPos.plus(onCursorClickPos) );
        onCursor.setParent( newParent );
        newParent.add( onCursor );

        onCursor = null;
    }
    /**
     * Odpovídá na otázku, zda právě něco visí na kurzoru.
     * @return odpověď na otázku , zda právě něco visí na kurzoru.
     */
    public boolean somethingOnCursor(){
        if( onCursor == null ) return false;
        return true;
    }

    
    /**
     * Metoda volaná pro informování rucksacku o relativní pozici myši při táhnutí
     * @param mousePos pozice myši při táhnutí
     */
    public void onDrag( Int2D mousePos ){
        onDragPos = mousePos;
    }
    /**
     * Metoda volaná pro informování rucksacku o absolutní pozici myši při táhnutí.
     * @param e MouseEvent způsobený táhnutím myši
     */
    public void onDrag( MouseEvent e ){
        onDragAbsPos = new Int2D( e.getX(), e.getY() );
    }
    /**
     * Metoda volaná pro informování rucksacku o kliknutí, aby zavřel popup.
     * @param e MouseEvent
     */
    public void onClick( MouseEvent e ){
        if( popupFrame != null ){
            popupFrame.dispose();
        }
    }
    /**
     * Metoda volaná pro informování rucksacku o aktalní pozici myši.
     * @param e MouseEvent
     */
    public void onMove( MouseEvent e ){
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Metoda zpřístupnující komukoli x-pozici myši.
     * @return
     */
    public int getMouseX(){ return mouseX; }
    /**
     * Metoda zpřístupnující komukoli y-pozici myši.
     * @return
     */
    public int getMouseY(){ return mouseY; }

    /**
     * Metoda umožňující rucksacku vykreslit to co je na kurzoru do GUI.
     * @param g Graphics2D kam se bude kreslit
     */
    public void paintScreen(Graphics2D g){
        try{
            if( lastTimeCut && onCursor != null ){

                Int2D center    = onDragAbsPos.minus(onDragPos) ;
                Int2D objectPos = onDragPos.plus(onCursorClickPos);


                    onCursor.setPos( objectPos );
                    onCursor.drawOutside(g, center , 0);

            }
            else if( !lastTimeCut && onCursor != null ){

                onCursor.setPos( new Int2D(mouseX, mouseY) );
                onCursor.drawOutside(g, Int2D.zero , 0);

            }
        }
        catch(NullPointerException e){
                Log.it("Chycena výjimka při vykreslení, nevadí.");
        }
    }

    /**
     * Nastaví objekt o jako "označený".
     * @param o objekt, který bude nyní označený
     */
    public void setSelected( KObject o ){

        if( selected != null ){
            selected.setHighlighted( false );
        }

        selected = o;
        selected.setHighlighted( true );

        if( selected instanceof Figure ){
            setActualFigure((Figure)selected);
        }

        if( ! ((selected instanceof Inputable) || (selected instanceof Outputable) ) ){
            resetFrom();
        }
    }

    /**
     * Umožnuje nastavit aktuální frame.
     * @param f frame
     */
    public void setSelectedFrame( Frame f ){

        if( selectedFrame != null ){
            selectedFrame.setHighlightedFrame( false );
        }

        selectedFrame = f;
        selectedFrame.setHighlightedFrame( true );
    }



    private void enter(){
        if( selected == null || selectedFrame == null ) return;
        if( selected.getIsGuiStuff() ) return;

        saveStateToUndoBuffer();

        selectedFrame.resetTarget( selected.id() );
        selectedFrame.setCam(Int2D.zero());

        if( selected instanceof Basic ){
            String cmd  = ((Basic)selected).getOnEnterCmd();
            if( cmd != null ){
                cmd( cmd );
            }
        }
    }

    private void backspace(){
        if( selectedFrame                      == null ) return;
        if( selectedFrame.getTarget().parent() == null ) return;

        if( selectedFrame.getTarget().getIsGuiStuff() ) return;

        saveStateToUndoBuffer();

        selectedFrame.resetTarget( selectedFrame.getTarget().parent().id() );
        selectedFrame.setCam(Int2D.zero());
    }

    private void delete(){
        if( selected == null ) return;
        if( selected == selectedFrame ) return;
        if( selected.getIsGuiStuff() ) return;

        saveStateToUndoBuffer();

        selected.delete();

        if( selected == actualFigure ){
            changeFigure( actualFigure );
        }

        selected = null;
    }

    private void toggleShowInfo(){
        showInfo = !showInfo;
    }

    private void togglePausePlay(){
        isSimulationRunning = !isSimulationRunning;
        if( isSimulationRunning ){
            saveStateToUndoBuffer();
        }
    }

    /**
     * Odpvoídá na otázku, zde právě běží simulace (tzn je play a ne pause).
     * @return zde právě běží simulace
     */
    public boolean isSimulationRunning(){
        return isSimulationRunning;
    }

    /**
     * Odpvoídá na otázku, zde se právě zobrazuje u objektů info
     * @return zde se právě zobrazuje u objektů info
     */
    public boolean showInfo(){
        return showInfo;
    }

    /**
     * Dává komukoli referenci na plánovač.
     * @return plánovač, defacto vrchol hierarchie KObjectů
     */
    public Scheduler getScheduler(){
        return scheduler;
    }

    /**
     * Umožňuje plánovači dát o sobě vědět.
     * @param s plánovač
     */
    protected void setScheduler(Scheduler s){
        scheduler = s;
    }

}

