package kutil.functions;

import kutil.kobjects.KObject;

/**
 * Výraz jazyka Kisp je předpisem, podle kterého se vyrobí objekt implementující rozhraní FunctionImplemetation.
 * V tomto rozhraní je hlavně důležitá metoda počítající pro pole vstupů pole výstupů dané funkce, metoda
 * compute(...).
 * @author Tomáš Křen
 */
public interface FunctionImplemetation {

    /**
     * Nejdůležitější metoda: samotný výpočet.
     * @param objects pole KObjektů v roli vstupních hodnot
     * @return pole KObjektů v roli výstupních hodnot
     */
    public KObject[] compute( KObject[] objects );
    /**
     * Počet vstupů.
     * @return počet vstupů
     */
    public int       numArgs();
    /**
     * Počet výstupů.
     * @return počet výstupů
     */
    public int       numOutputs();
    /**
     * Zpřístupňuje titulek (ten je pak napsaný na funkci).
     * @return titulek
     */
    public String    title();
    /**
     * Posun titulku na x-ové ose pro vycentrování.
     * @return titulek
     */
    public int       getTitleShift();
    
    //public boolean fooAble();
}
