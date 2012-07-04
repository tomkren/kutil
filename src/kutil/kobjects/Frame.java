package kutil.kobjects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputListener;
import kutil.items.BooleanItem;
import kutil.items.Int2DItem;
import kutil.items.StringItem;
import kutil.kevents.ClickEvent;
import kutil.kevents.DragEvent;
import kutil.kevents.KEvent;
import kutil.kevents.ReleaseEvent;
import kutil.core.Global;
import kutil.core.Int2D;
import kutil.core.KAtts;
import kutil.core.Log;

/**
 * Objekt virtuálního světa fungující jako základní prvek GUI - okno.
 * @author Tomáš Křen
 */
public class Frame extends Basic {

    private static final int maxFrameDepth = 100;

    // Položky:
    private Int2DItem   size;        // velikost okna
    private Int2DItem   cam;         // pozice kamery v rámci vnitřku zobrazovaného objektu
    private StringItem  targetID;    // id objektu, jehož vnitřek zobrazuje tento frame
    private StringItem  title;       // titulek okan
    private BooleanItem showXML;     // určuje zda okno zobrazuje XML reprezentaci targetu, nebo
                                     // zda standardně ukuzaju GUI
    private BooleanItem movableCam;  // určuje, zda je možno posouvat kameru

    private KObject  target; // objekt, jehož vnitřek zobrazujeme
    private Int2D    center; // střed okna

    private boolean  jFrameZatimNezalozen; // indikuje že pro tento frame nebyl zatím založen JFrame
    private boolean  isHighlightedFrame ;  // je frame označen jako oaktuální frame

    private MyJFrame myJFrame; // JFrame tohoto framu, používá se pokud je frame zároveň oknem programu
                               // tzn. nejedn se o vnořený frame

    /**
     * Vytvoří nový Frame podle kAtts.
     * @param kAtts strukturovaný vstupní argument obsahující vstupní data
     */
    public Frame(KAtts kAtts ){
        super(kAtts);

        setType("frame");

        size       = items().addInt2D ( kAtts , "size"       , new Int2D(600, 600) );
        cam        = items().addInt2D ( kAtts , "cam"        , Int2D.zero()        );
        targetID   = items().addString( kAtts , "target"     , null                );
        title      = items().addString( kAtts , "title"      , targetID.get()      );
        showXML    = items().addBoolean(kAtts , "showXML"    , false               );
        movableCam = items().addBoolean(kAtts , "movableCam" , true                );

        computeCenter();

        jFrameZatimNezalozen = true  ;
        isHighlightedFrame   = false ;

        
    }

    @Override
    public void init() {
        super.init();
        if( targetID.get() == null ){
            target = this;
            if( title.get()==null ){ title.set( id() );}
        }
        else {
            target = idDB().get( targetID.get() );
        }

        if( showXML.get() ) {
            backgroundColor = Color.BLACK;
        }else{
            backgroundColor = target.bgcolor();
        }
    }

    private int steps = 0;

    @Override
    public void step(){
        super.step();
        steps++;

        if( ! showXML.get() ){
            backgroundColor = target.bgcolor();
        }

        if( !( parent() instanceof Time) ) return;

        if( jFrameZatimNezalozen ){
            myJFrame = new MyJFrame(this);
            jFrameZatimNezalozen = false;

            rucksack().setSelected(this);
            rucksack().setSelectedFrame(this);
        }


        myJFrame.myJPanel.paintScreen();
    }


    /**
     * Metoda sloužící k přenastavení zobrazovaného objektu.
     * @param newTargetID id nového targetu
     */
    public void resetTarget( String newTargetID ){
        //idDB().unregisterFrame(this,targetID);
        //target = idDB().registerFrame(this, newTargetID);
        target = idDB().get(newTargetID);

        targetID.set( newTargetID );
        
        title.set( targetID.get() );
        if( ! jFrameZatimNezalozen ){
            myJFrame.setTitle( title.get() );
        }

        backgroundColor = target.bgcolor();
        //Log.it(target.id()+" má barvu "+target.bgcolor() );
    }

    @Override
    public void resolveRenaming(String oldId, String newId) {
        super.resolveRenaming(oldId, newId);

        if( targetID.get() != null && targetID.get().equals( oldId ) ){
            resetTarget(newId);
        }
    }

    /**
     * Vrací objekt, jehož vnitřek je zobrazován.
     */
    public KObject getTarget(){
        return target;
    }

    /**
     * Přenastaví rozměry framu.
     */
    public void resetSize( int x, int y ){
        size.get().setX(x);
        size.get().setY(y);
        computeCenter();
    }
    private void computeCenter(){
        center =  cam.get().negate();
        //když chci [0,0] implicitně v prostředku okna
        //center = size.get().times(0.5) .minus( cam.get() );
    }
    /**
     * Vráti pozici středu okna v rámci vnitřku cílového objektu.
     */
    public Int2D getCenter(){
        return center;
    }

    /**
     * informuj frame o tom ,že je vybraný frame.
     */
    public void setHighlightedFrame( boolean isHighlightedFrame ){
        this.isHighlightedFrame = isHighlightedFrame;
    }

    /**
     * Nastav barvu vrchní lišty okna
     */
    public void setDecorColor( Color c ){
        decorColor = c;
    }

    /**
     * Nastavuje, zda je zobrazena značka pro pozici [0,0] v rámci vnitřku cílového objektu.
     */
    public void setShowZeroPoint( boolean b ){
        showZeroPoint = b;
    }

    private Int2D camBefore;

    /**
     * přepíná mód zobrazování mezi XML reprezentací cílového objektu a klasickým zobrazením GUI
     */
    public void toggleShowXML( ){

        showXML.set( ! showXML.get() );

        if( showXML.get() ){
            camBefore = cam.get();
            backgroundColor = Color.BLACK;
        }
        else{
            backgroundColor = target.bgcolor();
        }
        
        cam.set( ( showXML.get() ? Int2D.zero() : camBefore ) );
        computeCenter();
    }

    private Color decorColor      = new Color(166,192,229);
            // původní modrá: new Color(166,192,229);
            // ubuntu hnědá : new Color(201,131,60);
    private Color backgroundColor = Color.white;
    private boolean showZeroPoint = true;

    private static final Font  decorFont  = new Font( Font.SANS_SERIF , Font.BOLD, 11);
    private static final Font  xmlFont    = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );
    private static final int   posunFont  = 4;
    private static final int   decorY     = 20;
    private static final Color highlightColor = Color.red;
    private static final Color highlightFrameColor = Color.green;
    private static final Int2D decorDelta = new Int2D( 0, decorY );
    private static final Font  idFont = new Font( Font.SANS_SERIF , Font.PLAIN , 10 );

    @Override
    public void drawOutside(Graphics2D g, Int2D c , int frameDepth ) {

        Shape initClip = g.getClip();
        Int2D drawPos = c.plus( pos() );

        drawBackground( g, drawPos );
        setFrameClip( g , drawPos );
        drawFrame( g , drawPos.plus( center ).plus( decorDelta ) , drawPos.plus( decorDelta ) , frameDepth+1  );
        g.setClip(initClip);
        drawDecorFrame( g, drawPos );

        if( rucksack().showInfo() ){
            g.setColor(decorColor);
            g.setFont( idFont );
            g.drawString( id() , drawPos.getX() , drawPos.getY()-3 );
        }
                    
        if( isHighlighted() ){
            g.setColor( highlightColor );
            g.drawRect( drawPos.getX(), drawPos.getY() , size.get().getX() , size.get().getY()+decorY );
        }

        if( isHighlightedFrame ){
            g.setColor( highlightFrameColor );
            g.drawRect( drawPos.getX()-1, drawPos.getY()-1 , size.get().getX()+2 , size.get().getY()+decorY+2 );
        }
    }

    private void setFrameClip( Graphics2D g, Int2D drawPos ){
        Rectangle clip = g.getClipBounds();

        int clipX1 = drawPos.getX();
        int clipY1 = drawPos.getY();
        int clipX2 = drawPos.getX() + size.get().getX();
        int clipY2 = drawPos.getY() + size.get().getY();

        if( clipX1 < clip.getX() ) clipX1 = (int) clip.getX();
        if( clipY1 < clip.getY() ) clipY1 = (int) clip.getY();
        if( clipX2 < clip.getX() ) clipX2 = (int) clip.getX();
        if( clipY2 < clip.getY() ) clipY2 = (int) clip.getY();
        if( clipX1 > clip.getX() + clip.getWidth()  )
            clipX1 = (int) (clip.getX() + clip.getWidth()  );
        if( clipY1 > clip.getY() + clip.getHeight()  -decorY )
            clipY1 = (int) (clip.getY() + clip.getHeight()  -decorY );
        if( clipX2 > clip.getX() + clip.getWidth()  )
            clipX2 = (int) (clip.getX() + clip.getWidth()  );
        if( clipY2 > clip.getY() + clip.getHeight() -decorY )
            clipY2 = (int) (clip.getY() + clip.getHeight() -decorY );

        g.setClip(  clipX1 , clipY1 , clipX2-clipX1 , clipY2-clipY1+decorY );
    }

    private void drawBackground( Graphics2D g, Int2D drawPos  ){
        g.setColor( backgroundColor );
        g.fillRect( drawPos.getX(), drawPos.getY()+decorY , size.get().getX() , size.get().getY() );

        //Log.it( backgroundColor );
    }

    private void drawDecorFrame(Graphics2D g, Int2D drawPos ){
        g.setColor( decorColor );
        g.drawRect( drawPos.getX(), drawPos.getY()+decorY , size.get().getX()   , size.get().getY() );
        g.fillRect( drawPos.getX(), drawPos.getY()        , size.get().getX()+1 , decorY +1   );

        g.setColor(Color.white);
        g.setFont(decorFont);
        g.drawString( title.get() , drawPos.getX()+3+posunFont , drawPos.getY() + decorY-1-posunFont );
    }


    private void drawFrame(Graphics2D g  , Int2D c , Int2D corner , int frameDept ){


        drawProgressBar( g , corner );

        if( showXML.get() ){

            g.setColor(Color.green);
            g.setFont(xmlFont);

            String[] rows = target.toXml().toString().split("\n");

            for( int i=0 ; i<rows.length ; i++ ){
                g.drawString( rows[i] , c.getX()+4 , c.getY()+11*(i+1) );
            }
        }
        else {
            if( frameDept < maxFrameDepth ){
                drawBackground( g, corner.minus(decorDelta) );

                if(showZeroPoint){
                    drawZeroPoint  ( g , c );
                }

                target.drawInside( g , c , frameDept );
            }
        }

        if( ! jFrameZatimNezalozen ){
            
            g.setColor(Color.black);

            if( isHighlighted() ){
                g.setColor( highlightColor );
                g.drawRect(0, 0, size.get().getX()-1, size.get().getY()-1 );
            }

            if( rucksack().showInfo() ){
                g.setFont(idFont);
                g.drawString( id() , 3 , 12 );
            }
            

            if( isHighlightedFrame ){
                g.setColor( highlightFrameColor );
                g.drawRect( 1, 1 , size.get().getX()-3 , size.get().getY()-3 );
            }
        }
    }

    private static final Color zeroPointColor = Color.black;
    private void drawZeroPoint(Graphics2D g, Int2D c ){
        g.setColor( zeroPointColor );
        g.drawLine( c.getX()-10 , c.getY()    , c.getX()+10 , c.getY()    );
        g.drawLine( c.getX()    , c.getY()-10 , c.getX()    , c.getY()+10 );
    }

    private void drawProgressBar( Graphics2D g, Int2D corner ){
        g.setColor(Color.green);
        g.fillRect( corner.getX() , corner.getY()+ size.get().getY()-1 ,
                    steps % size.get().getX() , 1 );
    }



    private void frameEvent( KEvent kEvent ){
        target.event(kEvent);
    }


    @Override
    public boolean isHit( Int2D clickPos ){
        return Int2D.rectangeHit(clickPos, pos() , size.get().getX() , size.get().getY()+decorY ) ;
    }

    @Override
    public void drag(Int2D clickPos, Int2D delta , Frame f) {

        if( isDecorHit(clickPos) ){
            super.drag(clickPos, delta,f);
            if( ! jFrameZatimNezalozen ){
                myJFrame.setLocation( pos().getX() , pos().getY() );
            }
        }
        else if( showXML.get() ){
            moveCam( delta );
        }
        else {
            frameEvent(
                    new DragEvent(
                        clickPos.minus(pos().plus(decorDelta)).minus(center) ,
                        delta,
                        this
                        )
                      );
        }
    }

    @Override
    public void click(Int2D clickPos) {
        if( showXML.get() || isDecorHit(clickPos) ){
            super.click(clickPos);
        }
        else{
            frameEvent(
             new ClickEvent(clickPos.minus(pos().plus(decorDelta)).minus(center) , this));
        }
    }

    /**
     * Vrací zda byl kliknutím zasažena vrchní lišta okna.
     */
    public boolean isDecorHit(Int2D clickPos){
        return clickPos.minus( pos() ).getY() < decorY;
    }

    @Override
    public void release( Int2D clickPos ) {
        frameEvent(
             new ReleaseEvent(clickPos.minus(pos().plus(decorDelta)).minus(center) ) );
        
        
    }


    /**
     * Posouvá pozici kamery o vektor delta.
     */
    public void moveCam( Int2D delta ){
        if( movableCam.get() ){
            cam.set( cam.get().minus(delta));
            computeCenter();
        }
    }

    /**
     * Nastaví pozici kamery.
     * @param pos nová pozice kamery
     */
    public void setCam( Int2D pos ){
        if( movableCam.get() ){
            cam.set( pos );
            computeCenter();
        }
    }


    /**
     * Vrací title framu.
     */
    public String getTitle() { return title.get() ; }
    /**
     * Vrací rozměry framu.
     */
    public Int2D  getSize()  { return size .get() ; }

    /**
     * Vrací JPanel od JFramu.
     */
    public JPanel getJPanel(){
        return myJFrame.myJPanel;
    }

    protected void paintScreen(Graphics2D g){
        drawFrame(g , center , Int2D.zero , 0 );
    }

    protected void mouseClicked( MouseEvent e ){
        frameEvent( new ClickEvent( clickPos(e) , this ) );
        rucksack().onClick( e );
        if( e.getButton() == MouseEvent.BUTTON3 ){
            Global.rucksack().clickedRightMouseButton( this , e.getX() , e.getY() );
        }
    }
    protected void mousePressed(MouseEvent e){
        
    }
    protected void mouseReleased(MouseEvent e){
        frameEvent( new ReleaseEvent( clickPos(e) ) );
    }
    protected void mouseDragged( MouseEvent e , Int2D delta ){
        
        frameEvent( new DragEvent( clickPos(e) , delta , this ) );
        rucksack().onDrag( e );
        rucksack().onMove( e );

        if( e.getButton() == MouseEvent.BUTTON3 ){
            Global.rucksack().clickedRightMouseButton( this , e.getX() , e.getY() );
        }
    }
    protected void mouseMoved( MouseEvent e ){
        rucksack().onMove( e );
    }

    private Int2D clickPos( MouseEvent e ){
        return new Int2D( e.getX() , e.getY() ).minus(center);
    }
}

class MyJFrame extends JFrame implements WindowListener, ComponentListener {

    protected MyJPanel myJPanel;
    private   Frame    frame;

    public MyJFrame( Frame frame ){
        super( frame.getTitle() );

        this.frame = frame;

        addWindowListener( this );
        addComponentListener(this);

        myJPanel = new MyJPanel(frame);
        myJPanel.setPreferredSize(new Dimension( frame.getSize().getX() ,
                                                 frame.getSize().getY() ));
        myJPanel.setBackground(Color.white);

        add( myJPanel );        

        pack();
        setResizable(true);
        setLocation(frame.pos().getX(), frame.pos().getY() );
        setVisible(true);

        //setExtendedState( getExtendedState() | MAXIMIZED_BOTH );  //pro maximalizaci
    }

    public void windowClosing       (WindowEvent e)    { 
        Global.rucksack().getScheduler().terminate();
        //this.dispose();
    }
    public void windowActivated     (WindowEvent e)    { }
    public void windowDeactivated   (WindowEvent e)    { }
    public void windowDeiconified   (WindowEvent e)    { }
    public void windowIconified     (WindowEvent e)    { }
    public void windowClosed        (WindowEvent e)    { }
    public void windowOpened        (WindowEvent e)    { }

    public void componentHidden     (ComponentEvent e) { }
    public void componentMoved      (ComponentEvent e) {

        frame.setPos( new Int2D( (int) e.getComponent().getLocation().getX() ,
                                 (int) e.getComponent().getLocation().getY() ) );

    }
    public void componentShown      (ComponentEvent e) { }
    public void componentResized    (ComponentEvent e) { }
}

class MyJPanel extends JPanel implements ComponentListener, MouseInputListener {

    private Frame frame;
    private MyActionMap myActionMap;
    private MyInputMap myInputMap;

    private Image      image;
    private Graphics2D graphics2D;
    private static final RenderingHints renderingHints =
            new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    
    public MyJPanel( Frame f ){
        frame = f;

        myInputMap = new MyInputMap();
        
        myInputMap.setParent( getInputMap( JComponent.WHEN_FOCUSED ) );
        setInputMap(JComponent.WHEN_FOCUSED, myInputMap );

        myActionMap = new MyActionMap(frame);

        setActionMap(myActionMap);        

        String[] keyNames = Global.rucksack().getKeyNames();
        for( int i = 0 ; i < keyNames.length ; i++ ){
            registerKeyboardEvent(keyNames[i]);
        }
        
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void resetImage(){

        image = createImage( frame.getSize().getX() , frame.getSize().getY() );

        if (image == null) {
            System.out.println("dbImage is null");
            return;
        }
        else{
            graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setRenderingHints(renderingHints);
            graphics2D.setClip(0, 0, frame.getSize().getX() , frame.getSize().getY() );
        }
    }

    public void paintScreen(){

        gameRender();

        Graphics g;
        try {
          g = this.getGraphics();
          if ((g != null) && (image != null))
            g.drawImage(image, 0, 0, null);
          g.dispose();

        }
        catch (Exception e)
        { System.out.println("Graphics context error: " + e);  }
    }

    private void gameRender()
    {
        if (image == null){
            resetImage();
        }

        if( graphics2D == null ){
            Log.it("CHYBA JE V TOM že graphics2D == null !!!");
            return;
        }

        graphics2D.setColor( Color.white );
        graphics2D.fillRect (0, 0, frame.getSize().getX() , frame.getSize().getY() );

        frame.paintScreen( graphics2D );
        frame.rucksack().paintScreen( graphics2D );
    }

    public final void registerKeyboardEvent( String event ){
        myActionMap.putNewAction(event);
        myInputMap.putNewInput(event);
    }


    public void mouseClicked(MouseEvent e) {
        frame.mouseClicked(e);
    }

    private int dragHelpX, dragHelpY;
    public void mousePressed(MouseEvent e) {
        frame.mousePressed(e);
        dragHelpX = e.getX() ;
        dragHelpY = e.getY() ;
    }

    public void mouseReleased(MouseEvent e){
        frame.mouseReleased(e);
    }

    public void mouseDragged(MouseEvent e) {
        frame.mouseDragged( e , new Int2D( e.getX() - dragHelpX , e.getY() - dragHelpY  ) );
        dragHelpX = e.getX();
        dragHelpY = e.getY();
    }

    public void mouseMoved(MouseEvent e)   {
        frame.mouseMoved(e);
    }


    public void mouseEntered(MouseEvent e) {  }
    public void mouseExited(MouseEvent e)  {  }
    

    public void componentHidden     (ComponentEvent e) { }
    public void componentMoved      (ComponentEvent e) { }
    public void componentShown      (ComponentEvent e) { }

    public void componentResized    (ComponentEvent e) {
        frame.resetSize( e.getComponent().getWidth()  ,
                         e.getComponent().getHeight() );
        resetImage();
    }
}

class MyInputMap extends InputMap {

    public MyInputMap() {
    }
    
    public void putNewInput( String str ){
        put( KeyStroke.getKeyStroke( str ) , str );
    }


}

class MyActionMap extends ActionMap {

    private Frame frame;

    public MyActionMap(Frame f) {
        frame = f;
    }

    public void putNewAction( String str ){
        final String str2 = str;
        put( str ,
            new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    frame.rucksack().keyboardEvent( str2 );
                }
            }
        );
    }
}


//    private void stepBackgroundColor(){
//        int s = Global.random().nextInt(3)-1;
//        int c = Global.random().nextInt(3);
//
//        int r = backgroundColor.getRed();
//        int g = backgroundColor.getGreen();
//        int b = backgroundColor.getBlue();
//
//        if     ( c==0 ) { r = correctColor(r + s); }
//        else if( c==1 ) { g = correctColor(g + s); }
//        else            { b = correctColor(b + s); }
//
//
//        backgroundColor = new Color(r,g,b);
//    }
//
//    private int correctColor( int i ){
//        if( i < 0   ) return 0;
//        if( i > 255 ) return 255;
//        return i;
//    }