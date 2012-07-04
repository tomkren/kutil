package kutil.core;

import java.util.Random;
import kutil.shapes.ShapeFactory;

/**
 * Třída, která umožňuje přístup ke globálním objektům programu.
 * @author Tomáš Křen
 */
public class Global {

    private static IdDB             idDB;
    private static IdChangeDB       idChangeDB;
    private static Rucksack         rucksack;
    private static ShapeFactory     shapeFactory;
    private static Random           random;

    /**
     * Inicializace globálních objektů.
     */
    public static void init(){
        idDB            = new IdDB();
        idChangeDB      = new IdChangeDB();
        rucksack        = new Rucksack();
        shapeFactory    = new ShapeFactory();
        random          = new Random();
    }

    /**
     * Zpřístupňuje aktuální databáze všech objektů virtuálního světa.
     */
    public static IdDB idDB(){
        return idDB;
    }

    /**
     * Zpřístupnuje objekt umožňující překlad unikátních id (ze starých na nově vzniklá)
     * při kopírování KObjectů.
     */
    public static IdChangeDB idChangeDB(){
        return  idChangeDB;
    }

    /**
     * Zpřístupnuje objekt rucksack který má důležitou roli při interakci s uživatelem,
     * umožňuje manipulaci s objekty pomocí myši,
     * implemetuje schránku,
     * zajišťuje ovládání běhu simulace,
     * zajišťuje pohyb zpět či vpřed v historii editačních změn,
     * zajišťuje interakci s textovou konzolí programu,
     * zajišťuje dialog pro ukládání a otevírání stavu,
     * drží informaci o označených objektech, atd.
     */
    public static Rucksack rucksack(){
        return rucksack;
    }

    /**
     * zpřístupnuje globalní objekt jenž má za úkol nahrát obrázky ze zdrojů do programu,
     * dále pak obsahuje metodu na převod kódu tvaru na KShape.
     * @return
     */
    public static ShapeFactory shapeFactory(){
        return shapeFactory;
    }

    /**
     * Zpřístupnuje globální zdroj náhody.
     */
    public static Random random(){
        return random;
    }


}
