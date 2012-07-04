package kutil.kobjects;

import kutil.core.Int2D;

/**
 * Rozhraní využívané funkcemi a in pro interakci s ostatními funkcemi a outem.
 * Zvláště důležitá je metoda hendleInput, respektive hendleOutsideInput, která
 * umožňuje dosadit do jiné funkce na konkrétní vstup nějakou hodnotu.
 * @author Tomáš Křen
 */
public interface Inputable {

    public void  handleInput( KObject input , int port );
    public void  handleOutsideInput( KObject input , int port );
    public Int2D getArrowEnd( int i );
    public KObject parent();
    public String  id();


}
