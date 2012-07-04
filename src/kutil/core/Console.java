package kutil.core;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Třída sprostředkovávající komunikaci s textovou konzolí programu a držící okno této konzole.
 */
public class Console extends JPanel implements ActionListener , WindowListener {
    protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";

    private JFrame frame;

    private Int2D loc;

    private static final Font font = new Font( Font.MONOSPACED , Font.PLAIN , 12 );

    /**
     * Vytvoří nové okno konzole.
     * @param loc pozice okna na obrazovce
     */
    public Console(Int2D loc) {
        super(new GridBagLayout());
        
        this.loc = loc;

        textField = new JTextField(32);
        textField.setFont(font);
        textField.addActionListener(this);

        textArea = new JTextArea(25, 32);
        textArea.setEditable(false);
        textArea.setFont(font);
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
        
        createAndShowGUI();
    }

    /**
     * Reaguje na ActionEvent
     * @param evt událost ActionEvent
     */
    public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();
        textArea.append(" >    " + text + newline);
        textField.selectAll();

        textArea.setCaretPosition(textArea.getDocument().getLength());
        
        Global.rucksack().handleConsoleCmd(this,text);
    }

    /**
     * Tuto metodu používají metody které chtějí nějak reagovat,
     * na příkaz zadaný do konzole.
     * @param str řetězec k vypsání
     */
    public void printString( String str ){
        textArea.append(" #    " + str + newline);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }


    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Console");
        //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocation(loc.getX(),loc.getY());

        frame.setAlwaysOnTop( true );
        //frame.setLocationByPlatform(true);

        frame.addWindowListener(this);

        //Add contents to the window.
        frame.add(this);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

       
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        textField.requestFocus();
    }

    /**
     * tato metoda je volána pro nastavení hodnoty příkazového řádku
     * @param str nová hodnota příkazového řádku
     */
    public void setCmd( String str ){
        textField.setText(str);
    }

    public void windowActivated(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {
    Global.rucksack().closeConsole();
    }
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}

    public JFrame getJFrame(){
        return frame;
    }

}

