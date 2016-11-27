package code;
// ColoredCells.java

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.*;

class Window extends JFrame {

    private Board board;
    private CheckersGame game;
    public JTextField infoField;
    private DefaultTableModel dtm;

    //player names
    private TextField player1Name;
    private TextField player2Name;

    //player human/AI
    private JRadioButton player1HumanButton;
    private JRadioButton player2HumanButton;

    //difficulty panels
    private Panel player1DifficultyLabel;
    private Panel player1DifficultySliderPanel;
    private JSlider player1DifficultySlider;
    private Panel player2DifficultyLabel;
    private Panel player2DifficultySliderPanel;
    private JSlider player2DifficultySlider;

    //label data
    private Font helv20 = new Font("Helvetica", Font.PLAIN, 20);
    private Font helv14 = new Font("Helvetica", Font.PLAIN, 14);
    private Color textFore = new Color(191, 192, 190);

    //click vars
    private JTable jt;
    private static int numOfClicks = 0;
    private Cell selectedCell;
    private Cell targetCell;

    Window(CheckersGame g, Board b) {
        super("AI Checkers");
        game = g;
        board = b;

        setLayout(new BorderLayout());
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);

        createMenus();
        createTableView();
        createTextField();

        setSize (700, 500);
        pack();
        setVisible (true);
    }

    private void createTableView() {
        dtm = new DefaultTableModel(board.getRowCount(), board.getColumnCount()) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //fill default table model with cells
        for (int y = 0; y < board.getRowCount(); y++) {
            for (int x = 0; x < board.getColumnCount(); x++) {
                dtm.setValueAt(board.getValueAt(x, y), y, x);
            }
        }

        jt = new JTable(dtm) {
            public TableCellRenderer getCellRenderer(int row, int columns) {
                return new CustomCellRenderer(getWidth()/board.getColumnCount(), getRowHeight());
            }
        };

        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable tableTarget = (JTable)e.getSource();
                int row = tableTarget.getSelectedRow();
                int col = tableTarget.getSelectedColumn();
                numOfClicks += 1;

                if (numOfClicks == 1) { //first click
                    selectedCell = board.getValueAt(col, row);
                    //if click on piece
                    if ((selectedCell.isWhite() && game.player == 2) || (selectedCell.isBlack() && game.player == 1)) {
                        selectedCell.setSelected(true);
                        jt.repaint(jt.getCellRect(selectedCell.getyPos(), selectedCell.getxPos(), false));
                        //System.out.println("Sel:" + col + "," + row);
                    } else { // if not clicked on piece
                        selectedCell = null;
                        numOfClicks = 0;
                    }
                } else if (numOfClicks == 2) { // if it's second click
                    targetCell = board.getValueAt(col, row);
                    // if 2 clicks on same cell, cancel selection
                    if (selectedCell == targetCell) {
                        selectedCell.setSelected(false);
                        selectedCell = null;
                        targetCell = null;
                        numOfClicks = 0;
                    }
                    // if cell selected is another piece, select that one
                    else if ((targetCell.isBlack() && game.player == 1) || (targetCell.isWhite() && game.player == 2)) {
                        selectedCell.setSelected(false);
                        jt.repaint(jt.getCellRect(selectedCell.getyPos(), selectedCell.getxPos(), false));
                        selectedCell = targetCell;
                        selectedCell.setSelected(true);
                        jt.repaint(jt.getCellRect(selectedCell.getyPos(), selectedCell.getxPos(), false));
                        targetCell = null;
                        numOfClicks = 1;
                    } else {
                        //if not the same cell, try move
                        System.out.println("Tar:" + col + "," + row);
                        game.gotInput(selectedCell, targetCell); //send input to game
                        selectedCell.setSelected(false);
                        selectedCell = null;
                        targetCell = null;
                        repaint();
                        numOfClicks = 0;
                    }
                }
            }
        });
        jt.setShowGrid(false);
        jt.setRowHeight(72);
        getContentPane().add(jt, BorderLayout.NORTH);
    }

    private void createTextField() {
        infoField = new JTextField("Ready to start");
        getContentPane().add(infoField, BorderLayout.SOUTH);
    }

    private void createMenus() {
        JMenuBar mb = new JMenuBar();
        JMenu game = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGameWindow();
            }
        });

        JMenuItem exitGame = new JMenuItem("Quit game");
        exitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenu help = new JMenu("Help");

        JMenuItem showRules = new JMenuItem("Display rules");
        showRules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayRules();
            }
        });

        JMenuItem showHints = new JMenuItem("Show hints");
        showHints.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        game.add(newGame);
        game.addSeparator();
        game.add(exitGame);
        mb.add(game);
        help.add(showRules);
        help.add(showHints);
        mb.add(help);
        this.setJMenuBar(mb);
    }

    private void newGameWindow() {
        final JFrame newGameFrame = new JFrame("New game...");
        newGameFrame.setDefaultCloseOperation(Window.DISPOSE_ON_CLOSE);
        newGameFrame.setAlwaysOnTop(true);
        newGameFrame.setLayout(new BorderLayout());
        newGameFrame.setBackground(new Color(37, 35, 32));

        // WEST
        newGameFrame.add(createPlayer1Panel(), BorderLayout.WEST);

        // CENTER
        newGameFrame.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.CENTER);

        // EAST
        newGameFrame.add(createPlayer2Panel(), BorderLayout.EAST);

        // SOUTH
        Panel gameSetup = new Panel();
        gameSetup.setLayout(new BorderLayout());
        gameSetup.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);

        Panel launchButtonPanel = new Panel();
        Button launchButton = new Button("LAUNCH!");
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.newGame(player1Name.getText(),
                        player1HumanButton.isSelected(),
                        player1DifficultySlider.getValue(),
                        player2Name.getText(),
                        player2HumanButton.isSelected(),
                        player2DifficultySlider.getValue()
                );
                newGameFrame.dispose();
                new Window(game, game.getBoard());
            }
        });
        launchButtonPanel.add(launchButton);
        gameSetup.add(launchButtonPanel, BorderLayout.SOUTH);

        newGameFrame.add(gameSetup, BorderLayout.SOUTH);


        //show on middle of screen
        newGameFrame.pack();
        newGameFrame.setVisible(true);
        newGameFrame.repaint();
    }

    private void displayRules() {
        JFrame rulesFrame = new JFrame("The rules of Draughts");
        rulesFrame.setDefaultCloseOperation(Window.DISPOSE_ON_CLOSE);
        rulesFrame.setAlwaysOnTop(true);

        System.out.println("here");
        Path filePath = Paths.get(this.getClass().getResource("/files/rules.txt").getPath());
        ArrayList<String> lines;
        String html = "";
        try {
            lines = (ArrayList<String>) Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                html += line;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        JEditorPane ep = new JEditorPane("text/html", html);
        ep.setEditable(false);
        ep.setCaretPosition(0);
        ep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException | URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        rulesFrame.add(new JScrollPane(ep));
        rulesFrame.setVisible(true);
        rulesFrame.setSize(500, 500);
    }

    private Panel createPlayer1Panel() {
        //Player 1 box
        Panel player1Panel = new Panel();
        player1Panel.setLayout(new GridLayout(5, 1));

        // title
        Panel p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        Label l1 = new Label("White");
        l1.setForeground(textFore);
        l1.setFont(helv20);
        p.add(l1);
        player1Panel.add(p);  // item 1

        // radio button choose
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        player1HumanButton = new JRadioButton("Human", true);
        player1HumanButton.setForeground(textFore);
        player1HumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer1DifficultyVisible(false);
                if (player2Name.getText().equals("Human")) player1Name.setText("Human 2");
                else player1Name.setText("Human");
            }
        });
        JRadioButton player1AIButton = new JRadioButton("AI");
        player1AIButton.setForeground(textFore);
        player1AIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer1DifficultyVisible(true);
                if (player2Name.getText().equals("AI")) player1Name.setText("AI 2");
                else player1Name.setText("AI");
            }
        });
        ButtonGroup bg = new ButtonGroup();
        bg.add(player1HumanButton);
        bg.add(player1AIButton);
        p.add(player1HumanButton);
        p.add(player1AIButton);
        player1Panel.add(p);  // item 2

        // name
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        Label l2 = new Label("Name:");
        l2.setForeground(textFore);
        l2.setFont(helv14);
        p.add(l2);
        player1Name = new TextField("Human", 6);
        p.add(player1Name);
        player1Panel.add(p);  // item 3

        // difficulty label
        player1DifficultyLabel = new Panel();
        player1DifficultyLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        player1DifficultyLabel.setVisible(false);
        Label l3 = new Label("Difficulty:");
        l3.setForeground(textFore);
        l3.setFont(helv14);
        player1DifficultyLabel.add(l3);
        player1Panel.add(player1DifficultyLabel);  // item 4

        // difficulty slider
        player1DifficultySliderPanel = new Panel();
        player1DifficultySliderPanel.setVisible(false);
        player1DifficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        player1DifficultySlider.setMajorTickSpacing(1);
        player1DifficultySlider.setPaintLabels(true);
        player1DifficultySlider.addChangeListener(null);
        player1DifficultySlider.setPaintTicks(true);
        player1DifficultySlider.setSnapToTicks(true);
        player1DifficultySlider.setForeground(textFore);
        /*
        Hashtable player1DifficultyTable = new Hashtable();
        player1DifficultyTable.put(new Integer(1), new Label("1");
        player1DifficultyTable.put(new Integer(5), new Label("5");
        player1DifficultySlider.setLabelTable(player1DifficultyTable);
        */
        player1DifficultySliderPanel.add(player1DifficultySlider);
        player1Panel.add(player1DifficultySliderPanel);  // item 5

        return player1Panel;
    }

    private Panel createPlayer2Panel() {
        //Player 2 box
        Panel player2Panel = new Panel();
        player2Panel.setLayout(new GridLayout(5, 1));

        // title
        Panel p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        Label l1 = new Label("Black");
        l1.setForeground(textFore);
        l1.setFont(helv20);
        p.add(l1);
        player2Panel.add(p);  // item 1

        // radio button choose
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        player2HumanButton = new JRadioButton("Human");
        player2HumanButton.setForeground(textFore);
        player2HumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer2DifficultyVisible(false);
                if (player1Name.getText().equals("Human")) player2Name.setText("Human 2");
                else player2Name.setText("Human");
            }
        });
        JRadioButton player2AIButton = new JRadioButton("AI", true);
        player2AIButton.setForeground(textFore);
        player2AIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer2DifficultyVisible(true);
                if (player1Name.getText().equals("AI")) player2Name.setText("AI 2");
                else player2Name.setText("AI");
            }
        });
        ButtonGroup bg = new ButtonGroup();
        bg.add(player2HumanButton);
        bg.add(player2AIButton);
        p.add(player2HumanButton);
        p.add(player2AIButton);
        player2Panel.add(p);  // item 2

        // name
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        Label l2 = new Label("Name:");
        l2.setForeground(textFore);
        l2.setFont(helv14);
        p.add(l2);
        player2Name = new TextField("AI", 6);
        p.add(player2Name);
        player2Panel.add(p);  // item 3

        // difficulty label
        player2DifficultyLabel = new Panel();
        player2DifficultyLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Label l3 = new Label("Difficulty:");
        l3.setForeground(textFore);
        l3.setFont(helv14);
        player2DifficultyLabel.add(l3);
        player2Panel.add(player2DifficultyLabel);  // item 4

        // difficulty slider
        player2DifficultySliderPanel = new Panel();
        player2DifficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        player2DifficultySlider.setMajorTickSpacing(1);
        player2DifficultySlider.setPaintLabels(true);
        player2DifficultySlider.addChangeListener(null);
        player2DifficultySlider.setPaintTicks(true);
        player2DifficultySlider.setSnapToTicks(true);
        player2DifficultySlider.setForeground(textFore);
        /*
        Hashtable player1DifficultyTable = new Hashtable();
        player1DifficultyTable.put(new Integer(1), new Label("1");
        player1DifficultyTable.put(new Integer(5), new Label("5");
        player1DifficultySlider.setLabelTable(player1DifficultyTable);
        */
        player2DifficultySliderPanel.add(player2DifficultySlider);
        player2Panel.add(player2DifficultySliderPanel);  // item 5

        return player2Panel;
    }

    private void setPlayer1DifficultyVisible(boolean b) {
        player1DifficultyLabel.setVisible(b);
        player1DifficultySliderPanel.setVisible(b);
    }

    private void setPlayer2DifficultyVisible(boolean b) {
        player2DifficultyLabel.setVisible(b);
        player2DifficultySliderPanel.setVisible(b);
    }

    private class CustomCellRenderer extends DefaultTableCellRenderer {

        private int cellWidth, cellHeight;

        CustomCellRenderer(int width, int height) {
            cellWidth = width;
            cellHeight = height;
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
        }

        protected void setValue (Object v) {
            super.setValue(v);

            setText("");
            Cell cellValue;
            if (v instanceof Cell) {
                cellValue = (Cell) v;
                if (cellValue.isLighter()) {
                    setBackground(new Color(234, 235, 200));
                } else {
                    setBackground(new Color(100, 134, 68));
                }
                if (cellValue.isBlack()) {
                    ImageIcon i;
                    if (cellValue.isKing())
                        i = new ImageIcon(this.getClass().getResource("/images/blackKing.png"));
                    else
                        i = new ImageIcon(this.getClass().getResource("/images/black.png"));

                    Image img = i.getImage();

                    if (!cellValue.isSelected()) {
                        img = img.getScaledInstance(cellWidth-4, cellHeight-4, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(img));
                    } else {
                        img = img.getScaledInstance(cellWidth-14, cellHeight-14, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(img));
                    }
                } else if (cellValue.isWhite()) {
                    ImageIcon i;
                    if (cellValue.isKing())
                        i = new ImageIcon(this.getClass().getResource("/images/whiteKing.png"));
                    else
                        i = new ImageIcon(this.getClass().getResource("/images/white.png"));
                    Image img = i.getImage();

                    if (cellValue.isSelected()) {
                        img = img.getScaledInstance(cellWidth-14, cellHeight-14, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(img));
                    } else {
                        img = img.getScaledInstance(cellWidth-4, cellHeight-4, Image.SCALE_SMOOTH);
                        setIcon(new ImageIcon(img));
                    }
                }
            }
        }
    }
}