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

/**
 * Class responsible for all GUI
 *
 * @author Lucas
 * @version 0.2
 */
class Window extends JFrame {

    private Board board;
    private static CheckersGame game;
    JTextField infoField;
    Label finish;

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
    private Color textForeground = new Color(37, 35, 32);
    private Color backgroundColor = new Color(191, 192, 190);

    //click vars
    public JTable jt;
    private DefaultTableModel dtm;
    private static int numOfClicks = 0;
    private Cell selectedCell;
    private Cell targetCell;

    //menu vars
    private boolean hints = true;

    Window(CheckersGame g, Board b) {
        super("AI Checkers");
        game = g;
        board = b;

        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenus();
        createGameView();
        createTextField();

        pack();
        setVisible (true);
    }

    private void createGameView() {
        Panel gameView = new Panel();
        gameView.setLayout(new BorderLayout());

        gameView.add(createTableView(), BorderLayout.WEST);

        Panel controlsView = new Panel();
        controlsView.setLayout(new GridLayout(2,1));
        controlsView.setBackground(backgroundColor);

        controlsView.add(getNameView());

        controlsView.add(createTextField());

        gameView.add(controlsView);

        getContentPane().add(gameView, BorderLayout.EAST);
    }

    private Panel getNameView() {
        Panel nameView = new Panel();
        nameView.setLayout(new GridLayout(5,1));
        nameView.setBackground(backgroundColor);

        Label p2NameLabel = new Label(game.getPlayer2().getName());
        p2NameLabel.setForeground(textForeground);
        p2NameLabel.setAlignment(Label.CENTER);
        p2NameLabel.setFont(helv20);
        nameView.add(p2NameLabel);

        Label p2DifficultyLabel = new Label(game.getPlayer2().getDifficulty());
        p2DifficultyLabel.setForeground(textForeground);
        p2DifficultyLabel.setAlignment(Label.CENTER);
        p2DifficultyLabel.setFont(helv14);
        nameView.add(p2DifficultyLabel);

        Label vsLabel = new Label("vs");
        vsLabel.setForeground(textForeground);
        vsLabel.setAlignment(Label.CENTER);
        vsLabel.setFont(helv14);
        nameView.add(vsLabel);

        Label p1NameLabel = new Label(game.getPlayer1().getName());
        p1NameLabel.setForeground(textForeground);
        p1NameLabel.setAlignment(Label.CENTER);
        p1NameLabel.setFont(helv20);
        nameView.add(p1NameLabel);

        Label p1DifficultyLabel = new Label(game.getPlayer1().getDifficulty());
        p1DifficultyLabel.setForeground(textForeground);
        p1DifficultyLabel.setAlignment(Label.CENTER);
        p1DifficultyLabel.setFont(helv14);
        nameView.add(p1DifficultyLabel);

        return nameView;
    }

    private JTable createTableView() {
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
            public void mouseReleased(MouseEvent e) {
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
                        new SendInput(selectedCell, targetCell).start(); //send input to game through thread
                        jt.repaint(jt.getCellRect(selectedCell.getyPos(), selectedCell.getxPos(), false));
                        jt.repaint(jt.getCellRect(targetCell.getyPos(), targetCell.getxPos(), false));
                        selectedCell.setSelected(false);
                        selectedCell = null;
                        targetCell = null;
                        numOfClicks = 0;
                    }
                }
            }
        });
        jt.setRowSelectionAllowed(false);
        jt.setColumnSelectionAllowed(false);
        jt.setShowGrid(false);
        jt.setRowHeight(72);
        return jt;
    }

    public void updateMove() {
        jt.repaint();
    }

    private Panel createTextField() {
        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        infoField = new JTextField("Ready to start");
        p.add(infoField, BorderLayout.SOUTH);
        finish = new Label();
        finish.setForeground(textForeground);
        finish.setFont(new Font("Helvetica", Font.BOLD, 34));
        finish.setAlignment(Label.CENTER);
        p.add(finish, BorderLayout.NORTH);

        Button showHintButton = new Button("Show hint");
        showHintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.player == 1) {

                } else { //player 2

                }
            }
        });
        return p;
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

        JMenuItem showSimplifiedRules = new JMenuItem("Simplified rules");
        showSimplifiedRules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displaySimplifiedRules();
            }
        });

        JCheckBoxMenuItem showHints = new JCheckBoxMenuItem("Show hints");
        showHints.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                hints = !hints;
            }
        });
        showHints.setState(true);

        game.add(newGame);
        game.addSeparator();
        game.add(exitGame);
        mb.add(game);
        help.add(showRules);
        help.add(showSimplifiedRules);
        help.addSeparator();
        help.add(showHints);
        mb.add(help);
        this.setJMenuBar(mb);
    }

    private void newGameWindow() {
        final JFrame newGameFrame = new JFrame("New game...");
        newGameFrame.setDefaultCloseOperation(Window.DISPOSE_ON_CLOSE);
        newGameFrame.setAlwaysOnTop(true);
        newGameFrame.setBackground(backgroundColor);
        newGameFrame.setResizable(false);

        newGameFrame.setLayout(new BorderLayout());

        // WEST
        newGameFrame.add(createPlayer1Panel(), BorderLayout.WEST);

        // CENTER
        Panel sepPanel = new Panel();
        sepPanel.setLayout(new BorderLayout());
        sepPanel.setBackground(backgroundColor);
        sepPanel.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.CENTER);
        newGameFrame.add(sepPanel, BorderLayout.CENTER);

        // EAST
        newGameFrame.add(createPlayer2Panel(), BorderLayout.EAST);

        // SOUTH
        Panel gameSetup = new Panel();
        gameSetup.setLayout(new BorderLayout());
        gameSetup.setBackground(backgroundColor);
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
        newGameFrame.setLocationRelativeTo(this);
        newGameFrame.setVisible(true);
        newGameFrame.repaint();
    }

    private void displayRules() {
        JFrame rulesFrame = new JFrame("The rules of Checkers/Draughts");
        rulesFrame.setDefaultCloseOperation(Window.DISPOSE_ON_CLOSE);
        rulesFrame.setAlwaysOnTop(true);

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
        rulesFrame.pack();
        rulesFrame.setSize(500, 500);
        rulesFrame.setLocationRelativeTo(this);
        rulesFrame.setVisible(true);
    }

    private void displaySimplifiedRules() {
        JFrame simpleRulesFrame = new JFrame("Simplified rules");
        simpleRulesFrame.setDefaultCloseOperation(Window.DISPOSE_ON_CLOSE);
        simpleRulesFrame.setAlwaysOnTop(true);

        String simplifiedRules = "Simplified rules of Checkers/Draughts\n\n" +
                "Goal:\nCapture all pieces of your opponent\n\n" +
                "Actions:\n" +
                "1: move piece diagonally by 1 square\n\n" +
                "2: capture an opposing piece by moving diagonally by\n" +
                "   2 squares, going over the piece you want to capture\n\n" +
                "3: if a piece reaches the other side of the board,\n" +
                "   it becomes a kingkings can move and take backwards";

        Panel p = new Panel();
        TextArea ta = new TextArea(simplifiedRules);
        ta.setEditable(false);
        simpleRulesFrame.add(ta);
        simpleRulesFrame.pack();
        simpleRulesFrame.setSize(400, 280);
        simpleRulesFrame.setLocationRelativeTo(this);
        simpleRulesFrame.setVisible(true);
    }

    private Panel createPlayer1Panel() {
        //Player 1 box
        Panel player1Panel = new Panel();
        player1Panel.setLayout(new GridLayout(5, 1));
        player1Panel.setBackground(backgroundColor);

        // title
        Panel p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        Label l1 = new Label("Black");
        l1.setForeground(textForeground);
        l1.setFont(helv20);
        p.add(l1);
        player1Panel.add(p);  // item 1

        // radio button choose
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        player1HumanButton = new JRadioButton("Human", true);
        player1HumanButton.setForeground(textForeground);
        player1HumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer1DifficultyVisible(false);
                if (player2Name.getText().equals("Human")) player1Name.setText("Human 2");
                else player1Name.setText("Human");
            }
        });
        JRadioButton player1AIButton = new JRadioButton("AI");
        player1AIButton.setForeground(textForeground);
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
        l2.setForeground(textForeground);
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
        l3.setForeground(textForeground);
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
        player1DifficultySlider.setForeground(textForeground);
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
        player2Panel.setBackground(backgroundColor);

        // title
        Panel p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        Label l1 = new Label("White");
        l1.setForeground(textForeground);
        l1.setFont(helv20);
        p.add(l1);
        player2Panel.add(p);  // item 1

        // radio button choose
        p = new Panel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        player2HumanButton = new JRadioButton("Human");
        player2HumanButton.setForeground(textForeground);
        player2HumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPlayer2DifficultyVisible(false);
                if (player1Name.getText().equals("Human")) player2Name.setText("Human 2");
                else player2Name.setText("Human");
            }
        });
        JRadioButton player2AIButton = new JRadioButton("AI", true);
        player2AIButton.setForeground(textForeground);
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
        l2.setForeground(textForeground);
        l2.setFont(helv14);
        p.add(l2);
        player2Name = new TextField("AI", 6);
        p.add(player2Name);
        player2Panel.add(p);  // item 3

        // difficulty label
        player2DifficultyLabel = new Panel();
        player2DifficultyLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Label l3 = new Label("Difficulty:");
        l3.setForeground(textForeground);
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
        player2DifficultySlider.setForeground(textForeground);
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

                if (cellValue.isFree()) {
                    setText(cellValue.getxPos() + "," + cellValue.getyPos());
                    setHorizontalAlignment(RIGHT);
                    setVerticalAlignment(BOTTOM);
                    setFont(new Font("Helvetica", Font.PLAIN, 9));
                }

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

    class SendInput implements Runnable {
        Cell selected, target;
        Thread t;

        SendInput(Cell selected, Cell target) {
            this.selected = selected;
            this.target = target;
        }

        @Override
        public void run() {
            game.gotInput(selected, target);
        }

        void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }
        }
    }
}