package kutil.xml;

/**
 * Třída reprezentující holý text v rámci XML elementů.
 * @author Tomáš Křen
 */
public class XmlText implements Xml {

    private String text;

    /**
     * Vytvoří nový XML text z text. řetězce (jen obalovač).
     * @param text String pro obalení
     */
    public XmlText( String text ){
        this.text = text;
    }

    /**
     * Vrací textovou reprezentaci daného XML  textu.
     * @return textovou reprezentaci daného XML  textu
     */
    @Override
    public String toString() {
        return text + '\n';
    }



}
