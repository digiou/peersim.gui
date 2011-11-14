/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author Dimitris
 */
public class StartGui extends JFrame{
    private JTextArea textArea = new JTextArea();
    private File file;
    private JFileChooser fileChooser = new JFileChooser();
    private boolean isSaved = true;
    DetectChangeDocumentListener textAreaDocumentListener;
}
