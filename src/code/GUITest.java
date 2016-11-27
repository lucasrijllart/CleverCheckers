package code;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Lucas Rijllart
 * @version 0.1
 */
public class GUITest
        extends JFrame {

    private Board board;
    private JTable boardTable;
    private JTextField infoField;

    private static final int cellSize = 60;

    /**
     * Create the UI
     *
     * @param b
     */
    public GUITest(Board b) {
        super("Super Checkers");
        board = b;
        createUI();
        newBoard(b);
    }

    final void newBoard(Board m) {
        board = m;
        createMazeView();
    }

    private void createUI() {
        getContentPane().setLayout(new BorderLayout());
        //getContentPane().setBackground(new Color(37, 35, 32));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMazeView();
        createInfo();
        createMenus();

        pack();
        setVisible(true);
    }

    private void createMazeView() {
        DefaultTableModel dtm = new DefaultTableModel(8,8);
        boardTable = new JTable(dtm);
        //boardTable.setShowGrid(false);
        //for (int i = 0; i < boardTable.getColumnCount(); i++) {
            //boardTable.getColumnModel().getColumn(i).setPreferredWidth(cellSize);
        //}

        CellRenderer cr = new CellRenderer();

        TableColumnModel tcm = boardTable.getColumnModel();

        for (int c = 0; c < 8; c++) {
            TableColumn tc = tcm.getColumn(c);
            tc.setCellRenderer(cr);
        }

        //boardTable.setDefaultRenderer(Cell.class, new CellRenderer());

        board.addTableModelListener(boardTable);
        boardTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        boardTable.setRowHeight(cellSize);

        getContentPane().add(boardTable, BorderLayout.CENTER);
    }


    private void createInfo() {
        infoField = new JTextField("Ready to start");
        getContentPane().add(infoField, BorderLayout.SOUTH);
    }

    private void createMenus() {
        JMenuBar mb = new JMenuBar();
        JMenu game = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //buttonState(true);
                infoField.setText("Ready to start");
                board.reset();
                boardTable.repaint();
            }
        });

        JMenuItem exitGame = new JMenuItem("Quit game");
        exitGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JMenu route = new JMenu("Route");

        JMenuItem randSolve = new JMenuItem("Random route");
        randSolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //buttonState(false);
                //Collection<Place> randWalk = new RandomWalkSolver(maze.getExplorer()).bestRoute();
                //infoField.setText("Finsihed maze in " + randWalk.size() + " moves.");
                boardTable.repaint();
            }
        });

        JMenuItem bestRoute = new JMenuItem("Best route");
        bestRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //buttonState(false);
                //Solver sol = new Solver(maze);
                //sol.breadthFirstSearch();
                //infoField.setText("Finished maze in " + sol.bestRoute.size() + " moves.");
                boardTable.repaint();
            }
        });

        JMenu help = new JMenu("Help");

        JMenuItem displayHelp = new JMenuItem("Display help");
        displayHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayHelp();
            }
        });

        game.add(newGame);
        game.addSeparator();
        game.add(exitGame);
        mb.add(game);
        route.add(randSolve);
        route.add(bestRoute);
        mb.add(route);
        help.add(displayHelp);
        mb.add(help);
        this.setJMenuBar(mb);
    }

    /**
     * Displays the help window when called.
     */
    public void displayHelp() {
        final JFrame helpWindow = new JFrame("Help");
        helpWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpWindow.setAlwaysOnTop(true);
        JPanel helpPane = new JPanel();
        helpPane.setLayout(new GridLayout(6, 1));
        helpPane.add(new JLabel("Help:"));
        helpPane.add(new JLabel("The grey square is the explorer, you control the explorer with the buttons."));
        helpPane.add(new JLabel("The black square are walls, you cannot move into these."));
        helpPane.add(new JLabel("The yellow square is the target, you need to move to the target to win."));
        helpPane.add(new JLabel());
        JButton exit = new JButton("Return to game");
        helpPane.add(exit);
        helpWindow.add(helpPane);
        helpWindow.pack();
        helpWindow.setVisible(true);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                helpWindow.dispose();
            }
        });
    }

    /**
     * Renders the JTable cells for the Cell objects to give blocks and special
     * spaces for start / end / explorer
     */
    class CellRenderer extends DefaultTableCellRenderer implements Icon {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            /*
            Cell cellValue;
            // light(234, 235, 200)
            // dark(100, 134, 68)
            // black(68, 65, 65), line(42, 40, 40), shadow(53, 51, 51)
            // white(246, 246, 246), line(104, 104, 104), shadow(201, 201, 201)
            if (value instanceof Cell) {
                cellValue = (Cell) value;
                if (cellValue.isLighter()) {
                    setBackground(new Color(234, 235, 200));
                } else {
                    setBackground(new Color(100, 134, 68));
                }
                if (cellValue.isBlack()) {
                    //Shape circle = new Ellipse2D.Float(20, 20, 20, 20);
                    //ImageIcon i = new ImageIcon("/Users/Lucas/Desktop/black2.png");
                    //setIcon(i);
                    return this;

                    //setBackground(new Color(black, black, black));
                    //ImageIcon blackPiece = new ImageIcon("/Users/Lucas/Desktop/black.png");
                    //setIcon(blackPiece);

                } else if (cellValue.isWhite()) {
                    //System.out.println("2");
                    setText("White");
                } else {

                }

                /*
                if ((maze.getExplorer().getX() == column)
                        && (maze.getExplorer().getRow() == row)) {
                    setBackground(Color.GRAY);
                    setText("E");
                }
                */
            //}
            //return this;
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        protected void setValue(Object v) {
            super.setValue(v);

            Cell cellValue;
            // light(234, 235, 200)
            // dark(100, 134, 68)
            // black(68, 65, 65), line(42, 40, 40), shadow(53, 51, 51)
            // white(246, 246, 246), line(104, 104, 104), shadow(201, 201, 201)
            if (v instanceof Cell) {
                cellValue = (Cell) v;
                if (cellValue.isLighter()) {
                    setBackground(new Color(234, 235, 200));
                } else {
                    setBackground(new Color(100, 134, 68));
                }
                if (cellValue.isBlack()) {
                    //Shape circle = new Ellipse2D.Float(20, 20, 20, 20);
                    //ImageIcon i = new ImageIcon("/Users/Lucas/Desktop/black2.png");
                    //setIcon(i);

                    //setBackground(new Color(black, black, black));
                    //ImageIcon blackPiece = new ImageIcon("/Users/Lucas/Desktop/black.png");
                    //setIcon(blackPiece);

                } else if (cellValue.isWhite()) {
                    //System.out.println("2");
                    setText("White");
                } else {

                }

                /*
                if ((maze.getExplorer().getX() == column)
                        && (maze.getExplorer().getRow() == row)) {
                    setBackground(Color.GRAY);
                    setText("E");
                }
                */
            }
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.blue);
            double v = Double.valueOf(this.getText());
            int d = (int)(v * 10);
            int r = d / 2;
            g2d.fillOval(x + 5 - r, y + 5 - r, d, d);
        }

        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 0;
        }
    }

    /**
     * Handling the button presses for movement and updating the info field as
     * appropriate
     */
    class MazeMoveAction extends AbstractAction {


        @Override
        public void actionPerformed(ActionEvent e) {
            //Explorer exp;

        }
    }
}
