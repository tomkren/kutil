package kutil.shapes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import kutil.core.Int2D;
import kutil.core.Log;

/**
 * Třída jejíž jediná instance má za úkol nahrát obrázky ze zdrojů do programu,
 * dále pak obsahuje metodu na převod kódu tvaru na KShape.
 * V programu se používá jediná instance přístupná přes třídu Global.
 * @author Tomáš Křen
 */
public class ShapeFactory {

    public BufferedImage unarImg, unarImgSel, inImg , inImgSel , outImg , outImgSel,
                         binarImg, binarImgSel, unarBinarImg, unarBinarImgSel,
                         unarBlackImg, unarBlackImgSel, boxImg , boxImgSel, flyImgSel,waspImgSel,
                         nularImg,nularImgSel,cloudImg,platformImg,platformImgSel,
                         recursionImg, recursionImgSel,
                         upImg, downImg, leftImg, rightImg, randomImg,
                         upImgSel, downImgSel, leftImgSel, rightImgSel, randomImgSel,
                         unarNularImg, unarNularImgSel, slotImg, slotImgSel ,
                         ingImg,ingImgSel, ternarImg, ternarImgSel, budhaImg, budhaImgSel,
                         appleImg, appleImgSel, earthImg,  earthImgSel , mrImgSel , 
                         ffImg,ffImgSel, smallFishImg;

    public BufferedImage[] flyImgs, waspImgs, mrImgs ;

    public ShapeFactory(){
        unarImg         = img("unar.png");
        unarImgSel      = img("unar_selected.png");
        unarBlackImg    = img("unarblack.png");
        unarBlackImgSel = img("unarblack_selected.png");
        inImg           = img("in.png");
        inImgSel        = img("in_selected.png");
        outImg          = img("out.png");
        outImgSel       = img("out_selected.png");
        binarImg        = img("binar.png");
        binarImgSel     = img("binar_selected.png");
        unarBinarImg    = img("unarbinar.png");
        unarBinarImgSel = img("unarbinar_selected.png");
        boxImg          = img("box.png");
        boxImgSel       = img("box_selected.png");
        flyImgSel       = img("fly_selected.png");
        waspImgSel      = img("wasp_selected.png");
        nularImg        = img("nular.png");
        nularImgSel     = img("nular_selected.png");
        cloudImg        = img("cloud.png");
        platformImg     = img("platform.png");
        platformImgSel  = img("platform_selected.png");
        recursionImg    = img("recursion.png");
        recursionImgSel = img("recursion_selected.png");
        upImg           = img("smer-up.png");
        upImgSel        = img("smer-up_selected.png");
        downImg         = img("smer-down.png");
        downImgSel      = img("smer-down_selected.png");
        leftImg         = img("smer-left.png");
        leftImgSel      = img("smer-left_selected.png");
        rightImg        = img("smer-right.png");
        rightImgSel     = img("smer-right_selected.png");
        randomImg       = img("smer-rand.png");
        randomImgSel    = img("smer-rand_selected.png");
        unarNularImg    = img("unarnular.png");
        unarNularImgSel = img("unarnular_selected.png");
        slotImg         = img("slot.png");
        slotImgSel      = img("slot_selected.png");
        ingImg          = img("ing.png");
        ingImgSel       = img("ing_selected.png");
        ternarImg       = img("ternar.png");
        ternarImgSel    = img("ternar_selected.png");
        budhaImg        = img("budha.png");
        budhaImgSel     = img("budha_selected.png");
        appleImg        = img("apple.png");
        appleImgSel     = img("apple_selected.png");
        earthImg        = img("earth.png");
        earthImgSel     = img("earth_selected.png");
        mrImgSel        = img("pan_selected.png");
        ffImg           = img("ff.png");
        ffImgSel        = img("ff_selected.png");
        smallFishImg    = img("smallfish2.png");
   

        flyImgs         = new BufferedImage[]{
                            img("fly1.png"),
                            img("fly2.png"),
                            img("fly3.png"),
                            img("fly4.png"),
                            img("fly5.png")
                          };
        waspImgs        = new BufferedImage[]{
                            img("wasp1.png"),
                            img("wasp2.png"),
                            img("wasp3.png"),
                            img("wasp4.png"),
                            img("wasp5.png")
                          };
        mrImgs          = new BufferedImage[]{
                            img("pan1.png"),
                            img("pan2.png"),
                            img("pan3.png"),
                            img("pan4.png"),
                            img("pan5.png")
                          };

    }

    public KShape newKShape( String shapeCmd ){

        if( shapeCmd == null ){
            return defaultShape();
        }

        String[] parts = shapeCmd.split( "\\s+" );
        String shapeName = parts[0];

        if ("rectangle".equals(shapeName)) {
            
            if( parts.length == 4 ){
                
                int x     = Integer.parseInt(parts[1]);
                int y     = Integer.parseInt(parts[2]);
                Color col = Color.PINK;
                
                try {
                    col = Color.decode(parts[3]);}
                catch(NumberFormatException e){
                    Log.it("Uncorrect color code : "+e.getMessage());}
                
                return new RectangleShape( new Int2D( x,y ), col );
            }
            
            if( parts.length != 3 ) return defaultShape();
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            return new RectangleShape( new Int2D( x,y ), Color.black );
        }

        if( "budha".equals(shapeName) ) {

            return new ImageShape( img("budha.png"), img("budha_selected.png") ,
                                            new Int2D[]{new Int2D( 35, 40 ),
                                                        new Int2D( 55, 40 ),
                                                        new Int2D( 80, 80 ),
                                                        new Int2D( 75,105 ),
                                                        new Int2D( 25,105 ),
                                                        new Int2D( 15, 80 ) },
                                            new Int2D(50,75),
                                            new Int2D(50,100)
                                                        );

        }
        
        if( "smallfish".equals(shapeName) ){
            
            return new ImageShape(smallFishImg, smallFishImg, 
                new Int2D[]{
                    new Int2D(  0, 0 ),
                    new Int2D( 44, 0 ),
                    new Int2D( 44, 14 ),
                    new Int2D(  0, 14 ) },
                new Int2D( 0, 0 ),
                new Int2D( 0, 0 ));
        }

        if( "earth".equals(shapeName) ) {

            return new EarthShape();
        }

        if( "cloud".equals(shapeName) ) {

            return new ImageShape( cloudImg , cloudImg , // 381 * 227
                                            new Int2D[]{new Int2D( 180, 100 ),
                                                        new Int2D( 200, 100 ),
                                                        new Int2D( 200, 130 ),
                                                        new Int2D( 180, 130 ) },
                                            new Int2D(190,115),
                                            new Int2D(190,115)
                                                        );
        }

        if( "platform".equals(shapeName) ) {

            return new ImageShape( platformImg , platformImgSel ,
                                            new Int2D[]{new Int2D( 32, 38 ),
                                                        new Int2D( 679, 38 ),
                                                        new Int2D( 366,150 ) },
                                            new Int2D(366,100),
                                            new Int2D(366,100)
                                                        );
        }

        if( "img".equals(shapeName) ){

            if( parts.length != 5 ) return defaultShape();
            int           x  = Integer.parseInt(parts[1]);
            int           y  = Integer.parseInt(parts[2]);
            String filename1 = parts[3];
            String filename2 = parts[4];

            return new ImageShape( img(filename1) , img(filename2) ,
                                    new Int2D[]{new Int2D( 0, 0 ),
                                                new Int2D( x, 0 ),
                                                new Int2D( x, y ),
                                                new Int2D( 0, y ) },
                                    new Int2D(x/2,y/2),
                                    new Int2D(x/2,y/2)
                                                );

        }

        if( "img2".equals(shapeName) ){

            if( parts.length != 7 ) return defaultShape();
            int           x  = Integer.parseInt(parts[1]);
            int           y  = Integer.parseInt(parts[2]);
            String filename1 = parts[3];
            String filename2 = parts[4];
            int           sx = Integer.parseInt(parts[5]);
            int           sy = Integer.parseInt(parts[6]);

            return new ImageShape( img(filename1) , img(filename2) ,
                                    new Int2D[]{new Int2D( 0, 0 ),
                                                new Int2D( x, 0 ),
                                                new Int2D( x, y ),
                                                new Int2D( 0, y ) },
                                    new Int2D(sx,sy),
                                    new Int2D(sx,sy)
                                                );

        }

        return defaultShape();
    }

    private BufferedImage img( String file ){
        try {

            return ImageIO.read( getClass().getResource("/kutil/img/" +  file ) );
        }
        catch (IOException e) {
            Log.it(e);
            return null;
        }
    }

    public static KShape defaultShape(){
        return new RectangleShape(new Int2D(32,32), Color.black );
    }

}
