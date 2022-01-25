import java.awt.GridLayout;
import java.awt.BorderLayout;
// import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {
    public static enum GameMode {USER, COMPUTER}

    public static enum BoardType {FRENCH, GERMAN, ASYMETRICAL, ENGLISH, DIAMOND, TRIANGULAR}

    // private JPanel __statusPanel;
    private JPanel __boardPanel;
    private JButton __gameBoard[][];

    private JPanel __controlPanel;
    private JButton __undoBtn;
    private JButton __homeBtn;
    
    private Movement __curMov; // keeps the current movement (necassary for undo movement)
    private Vector<JButton> __nextPossibleBtn;
    private Stack<Movement> __movements;
    private int __numOfMov;
    private int __numOfPeg;

    public GamePanel (JButton homeButton, GameMode gameType, BoardType boardType) {
        setLayout(new BorderLayout());

        // getContentPane().setBackground(ColorScheme.BLACK.get()); //
        //! NOT USED gameType !!!!!!!!!
        
        // initialize the game board selected by user
        setGameBoard(boardType);

        // add undo button
        __undoBtn = new JButton();
        __undoBtn.setBackground(ColorScheme.BLACK.get());
        // __undoBtn.setForeground(ColorScheme.RED.get());
        // __undoBtn.setHorizontalTextPosition(JButton.CENTER);
        // __undoBtn.setVerticalTextPosition(JButton.BOTTOM);
        __undoBtn.setIcon(new ImageIcon("../img/undo.png"));

        // __undoBtn.setPreferredSize(new Dimension(50, 50));
        __undoBtn.addActionListener(this);
        __undoBtn.setEnabled(false); // initially not clickable
        
        // home button
        __homeBtn = homeButton; // homeButton given as parameter
        __homeBtn.setBackground(ColorScheme.BLACK.get());
        __homeBtn.setIcon(new ImageIcon("../img/home.png"));
        // __homeBtn.addActionListener(this);
        
        // set control panel which keeps undo and home buttons 
        __controlPanel = new JPanel(new BorderLayout());
        add(__controlPanel, BorderLayout.NORTH);    // at the top of the super panel
        __controlPanel.setBackground(ColorScheme.BLACK.get());
        __controlPanel.add(__undoBtn, BorderLayout.WEST);
        __controlPanel.add(__homeBtn, BorderLayout.EAST);
    }

    public double score () {
        // max score is 100 (when 1 peg left)
        return (double) numOfPeg() / 100.0;
    }

    public int numOfPeg () {
        int n = 0;
        for (int i = 0; i < __gameBoard.length; ++i)
            for (int j = 0; j < __gameBoard[i].length; ++j)
                if (__gameBoard[i][j].getText().equals("P"))
                    ++n;
        return n;
    }

    /*
    private void setGameStatus () {
        if (__textField != null)
            __textField.setText(String.format("#peg: %d \t #movements: %d", __numOfPeg, __numOfMov));
    }
    */

    public void setGameBoard (BoardType t) {
        if (__boardPanel != null)
            remove(__boardPanel);
            
        // set Board Panel (keeps each buttons to represent cells of PegSolitaire)
        __boardPanel = new JPanel();
        __boardPanel.setBackground(ColorScheme.BLACK.get());

        switch (t) {
            case FRENCH: 
                setFrenchBoard();break;
            case GERMAN: 
                setGermanBoard(); break;
            case ASYMETRICAL:
                setAsymmetricalBoard();break;
            case ENGLISH: 
                setEnglishBoard(); break;
            case DIAMOND: 
                setDiamondBoard(); break;
            case TRIANGULAR: 
                setTriangleBoard(); break;
        }

        add(__boardPanel);  // add board panel to the JFrame
        __numOfMov = 0;
        __numOfPeg = numOfPeg();
        // setGameStatus(); //!! NOT SURE
        // reset/init movement for new board
        __curMov = new Movement(__gameBoard);
        __movements = new Stack<Movement>();
    }

    private void setGermanBoard() {
        __boardPanel.setLayout(new GridLayout(9, 9)); 
        __gameBoard = new JButton[9][9];

        final String cellValue[][] = {
                { "", "", "", "P", "P", "P", "", "", "" },
                {"P", "P", "P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;

            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                __gameBoard[i][j].setOpaque(true); // ????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __gameBoard[i][j].setText("P");
                    ColorScheme.setColor(__gameBoard[i][j],  ColorScheme.BLACK, ColorScheme.RED);
                    __gameBoard[i][j].addActionListener(this);
                } else {
                    // set non-clicable buttons (Walls)
                    __gameBoard[i][j].setBackground(ColorScheme.GRAY.get());
                    __gameBoard[i][j].setEnabled(false);
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[4][4].setText(" "); // center empty cell
    }

    private void setFrenchBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7));
        initBoard(7, 7, "P");

        for (int i = 0, n = 2; i < 2; ++i, --n)
            for (int j = 0; j < n; ++j) 
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 0, n = 5; i < 2; ++i, ++n)
            for (int j = n; j < 7; ++j) 
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 5, n = 1; i < 7; ++i, ++n)
            for (int j = 0; j < n; ++j) 
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 5, n = 6; i < 7; ++i, --n)
            for (int j = n; j < 7; ++j) 
                setEmptyButton(__gameBoard[i][j]);
        
        __gameBoard[2][3].setText(" ");
    }

    private void setAsymmetricalBoard () {
        __boardPanel.setLayout(new GridLayout(8, 8)); 
        __gameBoard = new JButton[8][8];

        final String cellValue[][] = {
                { "", "", "P", "P", "P", "", "", ""},
                {"P", "P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (3 <= i && i <= 5) ? 1 : 0;

            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                __gameBoard[i][j].setOpaque(true); // ????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __gameBoard[i][j].setText("P");
                    ColorScheme.setColor(__gameBoard[i][j],  ColorScheme.BLACK, ColorScheme.RED);
                    __gameBoard[i][j].addActionListener(this);
                } else {
                    // set non-clicable buttons (Walls)
                    __gameBoard[i][j].setBackground(ColorScheme.GRAY.get());
                    __gameBoard[i][j].setEnabled(false);
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[4][3].setText(" "); // center empty cell   
    }

    private void setEnglishBoard () {
        __boardPanel.setLayout(new GridLayout(7, 7)); 
        __gameBoard = new JButton[7][7];

        final String cellValue[][] = {
                { "", "", "P", "P", "P", "", ""},
                {"P", "P", "P", "P", "P", "P", "P"}
        };

        for (int i = 0; i < __gameBoard.length; ++i) {
            int col = (2 <= i && i <= 4) ? 1 : 0;

            for (int j = 0; j < __gameBoard[i].length; ++j) {
                __gameBoard[i][j] = new JButton();
                __gameBoard[i][j].setOpaque(true); // ????????????? is needed
                if (cellValue[col][j].equals("P")) {
                    __gameBoard[i][j].setText("P");
                    ColorScheme.setColor(__gameBoard[i][j],  ColorScheme.BLACK, ColorScheme.RED);
                    __gameBoard[i][j].addActionListener(this);
                } else {
                    // set non-clicable buttons (Walls)
                    __gameBoard[i][j].setBackground(ColorScheme.GRAY.get());
                    __gameBoard[i][j].setEnabled(false);
                }
                __boardPanel.add(__gameBoard[i][j]);
            }
        }
        __gameBoard[3][3].setText(" "); // center empty cell   
    }
    
    private void setDiamondBoard () {
        __boardPanel.setLayout(new GridLayout(9, 9));
        initBoard(9, 9, "P");

        for (int i = 0, n = 4; i < 4; ++i, --n)
            for (int j = 0; j < n; ++j)
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 0, n = 5; i < 4; ++i, ++n)
            for (int j = n; j < 9; ++j)
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 5, n = 1; i < 9; ++i, ++n)
            for (int j = 0; j < n; ++j)
                setEmptyButton(__gameBoard[i][j]);

        for (int i = 5, n = 8; i < 9; ++i, --n)
            for (int j = n; j < 9; ++j)
                setEmptyButton(__gameBoard[i][j]);
        
        __gameBoard[4][4].setText(" ");
    }

    private void setTriangleBoard () {
        //! NOT IMPLEMENTED YET
        System.out.println("NOT IMPLEMENTED YET");
    } 
    
    private void setEmptyButton (JButton btn) {
        btn.setBackground(ColorScheme.GRAY.get());
        btn.setText("");
        btn.setEnabled(false);
    }

    private void initBoard (int row, int col, String val) {
        __gameBoard = new JButton[row][col];
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j) {
                __gameBoard[i][j] = new JButton(val);
                __gameBoard[i][j].addActionListener(this);
                ColorScheme.setColor(__gameBoard[i][j], ColorScheme.BLACK, ColorScheme.RED);
                __boardPanel.add(__gameBoard[i][j]);
            }
    }

    public boolean isGameOver() {
        for (int i = 0; i < __gameBoard.length; ++i)
            for (var btn : __gameBoard[i])
                if (canMakeMovement(btn))
                    return false;
        return true;
    }

    public boolean canMakeMovement(JButton btn) {
        Movement mov = new Movement(__gameBoard, btn);

        //! FOR TEST PURPOSE        
        int[] indexes = mov.findLocation(btn); 
        if (mov.setMovement(btn, Movement.Direction.DOWN))
            System.out.printf("[%d][%d]: D\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.UP))
            System.out.printf("[%d][%d]: U\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.LEFT))
            System.out.printf("[%d][%d]: L\n", indexes[0], indexes[1]);
        if (mov.setMovement(btn, Movement.Direction.RIGHT))
            System.out.printf("[%d][%d]: R\n", indexes[0], indexes[1]);
        //! FOR TEST PURPOSE        

        return  mov.setMovement(btn, Movement.Direction.UP) ||
                    mov.setMovement(btn, Movement.Direction.DOWN) ||
                    mov.setMovement(btn, Movement.Direction.RIGHT) ||
                    mov.setMovement(btn, Movement.Direction.LEFT);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        JButton selectedBtn = (JButton) e.getSource();
        if (selectedBtn == __undoBtn) 
            undo();
        else if (__curMov.start() == null) {
        // ignore selection of the cell which are Wall("") or Empty(" ") cells
        if (selectedBtn.getText().equals("P")) {
                __curMov.setStart(selectedBtn);
                
                __nextPossibleBtn = __curMov.nextPossibleMov();
                if (__nextPossibleBtn == null)
                    __curMov.setStart(null);
                else {
                    // set hover effect on selected button                    
                    selectedBtn.setForeground(ColorScheme.GREEN.get()); 
                    
                    // show possible movements by hovering buttons
                    for (var btn : __nextPossibleBtn)
                        btn.setBackground(ColorScheme.RED.get()); 
                }
            }
        }
        // if start button was selected, current selected button should be end button
        else if (selectedBtn != __curMov.start()) {
            selectedBtn.setForeground(ColorScheme.GREEN.get()); // set hover effect on selected button
            __curMov.setEnd(selectedBtn);
            // apply movement
            if (move(__curMov)) {
                if (isGameOver()) 
                    JOptionPane.showMessageDialog(this, String.format(
                                "      Game is over\n" + 
                                "Number of Movement: %d\n" + 
                                "   Remaining Peg: %d", 
                                __numOfMov, __numOfPeg
                    ), "Game is Over", JOptionPane.INFORMATION_MESSAGE);    
            }
            // else JOptionPane.showMessageDialog(null, "Illegal movement", "Error", JOptionPane.ERROR_MESSAGE);

            // set selected buttons background color as default
            __curMov.start().setForeground(ColorScheme.RED.get());
            __curMov.end().setForeground(ColorScheme.RED.get());
            // set current Movement as null for next movement
            __curMov.setStart(null);
            __curMov.setEnd(null);

            if (__nextPossibleBtn != null)
                for (var btn : __nextPossibleBtn)
                    btn.setBackground(ColorScheme.BLACK.get()); // set hover effect on selected button
        }
    }

    public boolean move (Movement mov) {
        if (mov.isValidMovement()) {
            mov.start().setText(" ");
            mov.jump().setText(" ");
            mov.end().setText("P");
            ++__numOfMov;
            --__numOfPeg;
            // add the current movement to the movements stack (copy of it!)
            __movements.push(mov.clone());  
            if (!__undoBtn.isEnabled())
                __undoBtn.setEnabled(true);
            return true;
        } else
            return false;
    }

    public boolean moveRandom () {
        Random rand = new Random();
        // choose an random starting position
        int row = rand.nextInt(__gameBoard.length);
        int col = rand.nextInt(__gameBoard[row].length);

        // start with selected position (row, col) and try each cell to make movement
        for (int i = 0; i < __gameBoard.length; ++i) {
            for (int j = 0; j < __gameBoard[i].length; ++j) {
                // check movement
                __curMov.setStart(__gameBoard[row][col]);
                if (    
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.RIGHT) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.LEFT) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.UP) ||
                        __curMov.setMovement(__gameBoard[row][col], Movement.Direction.DOWN)
                    ) {
                    move(__curMov);
                    return true;
                }
                // iterate coloumn
                col = (col == __gameBoard[row].length - 1) ? 0 : col + 1;
            }
            // iterate row
            row = (row == __gameBoard.length - 1) ? 0 : row + 1;
        }
        return false;
    }

    public boolean undo() {
        // if (__curMov.start() != null && __curMov.jump() != null && __curMov.end() // != null) {
        // if there is a valid movement made before, apply reverse of it
        if (__undoBtn.isEnabled()) {
            Movement lastMov = __movements.pop();
            lastMov.start().setText("P");
            lastMov.jump().setText("P");
            lastMov.end().setText(" ");
            --__numOfMov;
            ++__numOfPeg;
            
            if (__movements.size() == 0)
                __undoBtn.setEnabled(false);
            return true;
        }
        return false;
    }

    public static class Movement implements Cloneable {
        private JButton[][] __board; // game board for checking validty of movement
        private JButton __startBtn; // start position of movement
        private JButton __jumpBtn; // jump position of movement (between start and end)
        private JButton __endBtn; // end position of movement

        public static enum Direction {UP, DOWN, LEFT, RIGHT}

        public Movement(JButton[][] board, JButton start, JButton end) {
            __board = board;
            try {
                setStart(start);
                setEnd(end);
            } catch (InvalidParameterException e) {
                __startBtn = __endBtn = __jumpBtn = null;
                System.err.println("Invalid parameter for Movement Constructor");
            }
        }

        public Movement(JButton[][] board, JButton start) {this(board, start, null);}

        public Movement(JButton[][] board) {this(board, null, null);}

        public Movement() {this(null, null, null);}

        public JButton start() {
            return __startBtn;
        }

        public JButton end() {
            return __endBtn;
        }

        public JButton jump() {
            return __jumpBtn;
        }

        public Vector<JButton> nextPossibleMov () {
            Vector<JButton> v = new Vector<JButton>();
            if (start() != null) {
                for (Direction d : Direction.values())
                    if (setMovement(start(), d))
                        v.add(end());
            }
            return v.size() > 0 ? v : null;
        }

        public void setStart(JButton start) throws InvalidParameterException {
            // be sure given JButton is in the current game board
            if (start != null && findLocation(start) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __startBtn = start;
        }

        public void setEnd(JButton end) {
            // be sure given JButton is in the current game board
            if (end != null && findLocation(end) == null)
                throw new InvalidParameterException("given JButtons not exist in game board");
            __endBtn = end;
        }

        public void setJump() throws InvalidParameterException {
            if (__board == null || start() == null || end() == null)
                throw new NullPointerException("no enough information to find jump button");

            int[] startIndexes = findLocation(start());
            int[] endIndexes = findLocation(end());

            if (startIndexes != null && endIndexes != null) {
                int row = -1; // jump button row
                int col = -1; // jump button coloumn

                // starBtn and endBtn are at same row
                if (startIndexes[0] == endIndexes[0]) {
                    row = endIndexes[0];

                    int diff = endIndexes[1] - startIndexes[1];
                    if (diff == 2)
                        col = endIndexes[1] - 1;
                    else if (diff == -2)
                        col = endIndexes[1] + 1;
                }
                // starBtn and endBtn are at same coloumn
                else if (startIndexes[1] == endIndexes[1]) {
                    col = endIndexes[1];

                    int diff = endIndexes[0] - startIndexes[0];
                    if (diff == 2)
                        row = endIndexes[0] - 1;
                    else if (diff == -2)
                        row = endIndexes[0] + 1;
                }

                // be sure jump row and col are in range, otherwise set it as null
                __jumpBtn = (0 <= row && row < __board.length && 0 <= col && col < __board[row].length)
                        ? __board[row][col]
                        : null;
            }
        }

        public void setBoard(JButton[][] board) {
            __board = board;
            // be sure given buttons are still valid
            if (findLocation(__startBtn) == null)
                __startBtn = null;
            if (findLocation(__endBtn) == null)
                __startBtn = null;
            if (findLocation(__jumpBtn) == null)
                __jumpBtn = null;
        }

        public boolean setMovement (JButton start, Direction d) throws InvalidParameterException {
            try {
                setStart(start);    // can throw InvalidParameterException
                boolean r = false;
                if (start().getText().equals("P")) {
                   int[] indexes = findLocation(start); 
                   if (indexes != null) {
                        switch (d) {
                            case UP: 
                                r = setUpMovement(indexes[0], indexes[1]);
                                break;
                            case DOWN: 
                                r = setDownMovement(indexes[0], indexes[1]);
                                break;
                            case LEFT: 
                                r = setLeftMovement(indexes[0], indexes[1]);
                                break;
                            case RIGHT: 
                                r = setRightMovement(indexes[0], indexes[1]);
                                break;
                        }
                    }
                }
                return r;
            }
            catch (InvalidParameterException e) {
                System.err.printf("start JButton is invalid parameter for setting");
                throw e;
            }            
        }

        private boolean setUpMovement(int row, int col) {
            if (0 <= row - 2 && __board[row - 1][col].getText().equals("P")
                    && __board[row - 2][col].getText().equals(" ")) {
                __jumpBtn = __board[row - 1][col];
                __endBtn = __board[row - 2][col];
                return true;
            } 
            return false;
        }

        private boolean setDownMovement(int row, int col) {
            if (row + 2 < __board.length && __board[row + 1][col].getText().equals("P")
                    && __board[row + 2][col].getText().equals(" ")) {
                __jumpBtn = __board[row + 1][col];
                __endBtn = __board[row + 2][col];
                return true;
            } 
            return false;
        }

        private boolean setLeftMovement(int row, int col) {
            if (0 <= col - 2 && __board[row][col - 1].getText().equals("P")
                    && __board[row][col - 2].getText().equals(" ")) {
                __jumpBtn = __board[row][col - 1];
                __endBtn = __board[row][col - 2];
                return true;
            }

            return false;
        }

        private boolean setRightMovement(int row, int col) {
            if (col + 2 < __board[col].length && __board[row][col + 1].getText().equals("P")
                    && __board[row][col + 2].getText().equals(" ")) {
                __jumpBtn = __board[row][col + 1];
                __endBtn = __board[row][col + 2];
                return true;
            }
            return false;
        }

        public boolean isValidMovement() {
            setJump();
            // jump becomes null, if start and end buttons are not in proper position
            return jump() != null &&
                    __startBtn.getText().equals("P") &&
                    __jumpBtn.getText().equals("P") &&
                    __endBtn.getText().equals(" ");
        }

        public int[] findLocation(JButton btn) throws NullPointerException {
            int indexes[] = null;
            if (__board != null && btn != null) {
                for (int i = 0; i < __board.length && indexes == null; ++i)
                    for (int j = 0; j < __board[i].length && indexes == null; ++j)
                        if (__board[i][j] == btn) {
                            indexes = new int[2];
                            indexes[0] = i; // assign row
                            indexes[1] = j; // assign col
                        }
            }
            return indexes;
        }

        public Movement clone() {
            try {
                Movement r = (Movement) super.clone();
                r.__board = __board;
                r.__startBtn = __startBtn;
                r.__endBtn = __endBtn;
                r.__jumpBtn = __jumpBtn;
                return r;
            } catch (CloneNotSupportedException e) {
                // this will never be happen
                return null;
            }
        }
    } // end of Movement Class
}
