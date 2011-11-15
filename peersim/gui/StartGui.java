/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Dimitris
 */
public class StartGui extends JFrame {

    private JTextArea textArea = new JTextArea();
    private File file;
    private JFileChooser fileChooser = new JFileChooser();
    private boolean isSaved = true;
    DetectChangeDocumentListener textAreaDocumentListener;
    private Object aboutDialog;

    public StartGui() {
        super("PeerSim GUI");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        textAreaDocumentListener = new DetectChangeDocumentListener(this);
        textArea.getDocument().addDocumentListener(textAreaDocumentListener);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        JMenu fileMenu = new JMenu("Αρχείο");
        JMenuItem openMenuItem = new JMenuItem("Άνοιγμα");
        openMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        JMenuItem saveMenuItem = new JMenuItem("Αποθήκευση");
        saveMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Έξοδος");
        exitMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu helpMenu = new JMenu("Βοήθεια");

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                handleExit();
            }
        });
    }

    private void loadFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            try {
                Scanner fileReader = new Scanner(file);
                StringBuffer textBuffer = new StringBuffer();
                while (fileReader.hasNextLine()) {
                    textBuffer.append(fileReader.nextLine() + "\n");
                }
                textArea.setText(textBuffer.toString());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Δεν είναι δυνατό το άνοιγμα του αρχείου.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private void handleExit() {
        if (!isSaved) {
            askToSaveFile();
        } else {
            dispose();
        }
    }

    private void askToSaveFile() {
        int result = JOptionPane.showConfirmDialog(this, "Το κείμενο έχει τροποποιηθεί.\n\nΝα γίνει αποθήκευση;", "Προειδοποίηση", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (saveFile()) {
                dispose();
            }
        } else if (result == JOptionPane.NO_OPTION) {
            dispose();
        } else {
            // Do nothing
        }
    }

    private boolean saveFile() {
        if (file == null) {
            fileChooser.setSelectedFile(new File("Untitled.txt"));
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                Formatter fileWriter = new Formatter(fileChooser.getSelectedFile());
                fileWriter.format("%s", textArea.getText());
                fileWriter.close();
                file = fileChooser.getSelectedFile();
                isSaved = true;
                textArea.getDocument().addDocumentListener(textAreaDocumentListener);
                return true;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Δεν είναι δυνατή η αποθήκευση του αρχείου.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return false;
    }

    private class DetectChangeDocumentListener implements DocumentListener {

        private StartGui editor;

        public DetectChangeDocumentListener(StartGui editor) {
            this.editor = editor;
        }

        public void insertUpdate(DocumentEvent e) {
            markChanged();
        }

        public void removeUpdate(DocumentEvent e) {
            markChanged();
        }

        public void changedUpdate(DocumentEvent e) {
            markChanged();
        }

        private void markChanged() {
            /*editor.*/ isSaved = false;
            /*editor.*/ textArea.getDocument().removeDocumentListener(this);
        }
    }
}
