/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Dimitris
 */
public class StartGui extends JFrame {

    private JTextArea inputTextArea = new JTextArea();
    private JTextArea outputTextArea = new JTextArea();
    private JLabel status;
    private File file;
    private JFileChooser fileChooser = new JFileChooser();
    private boolean isSaved = true;
    DetectChangeDocumentListener textAreaDocumentListener;

    public StartGui() {
        setTitle("Peersim Launcher");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        textAreaDocumentListener = new DetectChangeDocumentListener(this);
        inputTextArea.getDocument().addDocumentListener(textAreaDocumentListener);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Input file", inputScrollPane);
        tabbedPane.addTab("Console output", outputScrollPane);
        tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        add(tabbedPane, BorderLayout.CENTER);

        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open file");
        openMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        JMenuItem saveMenuItem = new JMenuItem("Save file");
        saveMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu simuMenu = new JMenu("Simulation");
        JMenuItem startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startSimuation();
            }
        });

        simuMenu.add(startMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(simuMenu);

        setJMenuBar(menuBar);

        status = new JLabel("No file selected.");
        status.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        add(status, BorderLayout.PAGE_END);

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                handleExit();
            }
        });

        setVisible(true);
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
                inputTextArea.setText(textBuffer.toString());
                inputTextArea.setEditable(true);
                status.setText("Using file: " + file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Couldn't save file.", "Error", JOptionPane.ERROR_MESSAGE);
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
        int result = JOptionPane.showConfirmDialog(this, "The text has been modified.\nSave?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
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
                fileWriter.format("%s", inputTextArea.getText());
                fileWriter.close();
                file = fileChooser.getSelectedFile();
                isSaved = true;
                inputTextArea.getDocument().addDocumentListener(textAreaDocumentListener);
                status.setText("Using file: " + file.getAbsolutePath());
                return true;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Couldn't save file!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return false;
    }

    private void startSimuation() {
        if (isSaved) {
            redirectSystemStreams();
            // XXX to do
            
        } else {
            saveFile();
            redirectSystemStreams();
            // XXX to do
            
        }
    }

    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                outputTextArea.append(text);
            }
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
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
            isSaved = false;
            if (status.getText().contains("No file selected.")) {
                status.setText("Using new file.");
            }
            inputTextArea.getDocument().removeDocumentListener(this);
        }
    }
}
