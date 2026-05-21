package PM;

import javax.swing.*;
import java.awt.*;

/**
 * 게임 보드를 초기화하고 관리하는 클래스
 * 모든 점, 벽, 캐릭터의 위치를 설정
 */
public class GameBoard {
    private JLabel[][] board;
    private ImageIcon smallDot, bigDot, wallIcon, enemyIcon, pacmanIcon, emptyIcon;
    private char[][] state;
    
    public GameBoard(ImageIcon smallDot, ImageIcon bigDot, ImageIcon wall, 
                     ImageIcon enemy, ImageIcon pacman, ImageIcon empty) {
        this.smallDot = smallDot;
        this.bigDot = bigDot;
        this.wallIcon = wall;
        this.enemyIcon = enemy;
        this.pacmanIcon = pacman;
        this.emptyIcon = empty;
        board = new JLabel[GameConstants.BOARD_SIZE][GameConstants.BOARD_SIZE];
        state = new char[GameConstants.BOARD_SIZE][GameConstants.BOARD_SIZE];
        initializeBoard();
    }
    
    private void initializeBoard() {
        // 모든 셀을 벽으로 초기화
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                board[i][j] = new JLabel();
                board[i][j].setPreferredSize(new java.awt.Dimension(
                    GameConstants.BOARD_CELL_SIZE, GameConstants.BOARD_CELL_SIZE));
                board[i][j].setHorizontalAlignment(JLabel.CENTER);
                board[i][j].setVerticalAlignment(JLabel.CENTER);
                state[i][j] = '#';
                board[i][j].setIcon(wallIcon);
            }
        }
        
        // 특정 위치에 점과 공간 설정
        setDots();
    }
    
    private void setDots() {
        // 행별로 점 설정 - 아이콘 기반으로 상태 설정
        setCellState(1,1,'+'); setCellState(2,1,'+'); setCellState(3,1,'+');
        setCellState(4,1,'+'); setCellState(5,1,'+');
        setCellState(5,2,'O'); setCellState(5,3,'+'); setCellState(1,3,'+');
        setCellState(2,3,'+'); setCellState(3,3,'+');
        setCellState(4,3,'+'); setCellState(1,4,'+'); setCellState(1,5,'+');
        setCellState(1,6,'+'); setCellState(1,7,'+');
        setCellState(1,8,'+'); setCellState(1,9,'+'); setCellState(1,10,'+');
        setCellState(1,11,'+'); setCellState(1,12,'+');
        setCellState(2,9,'+'); setCellState(2,12,'+'); setCellState(3,12,'+');
        setCellState(4,12,'+'); setCellState(5,12,'+');
        setCellState(3,4,'+'); setCellState(3,5,'+'); setCellState(3,10,'+');
        setCellState(3,11,'+');
        setCellState(4,5,'+'); setCellState(4,6,'+'); setCellState(4,7,'+');
        setCellState(4,8,'+'); setCellState(4,9,'+');
        setCellState(4,10,'+'); setCellState(2,7,'+'); setCellState(3,7,'+');
        setCellState(4,10,'+'); setCellState(4,11,'O');
        setCellState(5,11,'+'); setCellState(6,11,'+'); setCellState(7,11,'+');
        setCellState(7,12,'+'); setCellState(8,12,'+');
        setCellState(9,12,'+'); setCellState(10,12,'+'); setCellState(11,12,'+');
        setCellState(5,7,' '); setCellState(6,2,'+');
        setCellState(7,1,'+'); setCellState(9,11,'O'); setCellState(11,1,'O');
        setCellState(11,3,'+'); setCellState(11,11,'+');
        setCellState(7,2,'+'); setCellState(7,3,'+'); setCellState(8,1,'+');
        setCellState(9,1,'+'); setCellState(9,2,'+');
        setCellState(9,3,'+'); setCellState(9,4,'+'); setCellState(9,5,'+');
        setCellState(9,6,'+'); setCellState(12,1,'+');
        setCellState(12,2,'+'); setCellState(12,3,'+'); setCellState(12,4,'+');
        setCellState(12,5,'+'); setCellState(12,9,'+');
        setCellState(12,10,'+'); setCellState(12,11,'+'); setCellState(10,5,'+');
        setCellState(11,5,'+'); setCellState(10,6,'+');
        setCellState(10,7,'+'); setCellState(10,8,'+'); setCellState(10,9,'+');
        setCellState(11,7,'+'); setCellState(12,7,'P');
        setCellState(9,8,'+'); setCellState(9,9,'+'); setCellState(11,9,'+');
        setCellState(6,5,' '); setCellState(6,6,' ');
        setCellState(6,7,' '); setCellState(6,8,' '); setCellState(6,9,' ');
        setCellState(7,5,' '); setCellState(7,6,' ');
        setCellState(7,7,'G'); setCellState(7,8,' '); setCellState(7,9,' ');
    }
    
    public JLabel[][] getBoard() {
        return board;
    }
    
    public JLabel getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }
    
    // Set the displayed icon for a cell without changing logical state
    public void setCell(int row, int col, ImageIcon icon) {
        if (isValidPosition(row, col)) {
            board[row][col].setIcon(icon);
        }
    }

    // Set logical state for a cell and update the default icon for that state.
    // For 'G' (ghost) we do NOT assign a global enemy icon here because
    // multiple enemies may have distinct icons; callers should use setCell
    // to place a per-enemy icon after setting state to 'G'.
    public void setCellState(int row, int col, char ch) {
        if (!isValidPosition(row, col)) return;
        state[row][col] = ch;
        switch (ch) {
            case '#': board[row][col].setIcon(wallIcon); break;
            case ' ': board[row][col].setIcon(emptyIcon); break;
            case '+': board[row][col].setIcon(smallDot); break;
            case 'O': board[row][col].setIcon(bigDot); break;
            case 'P': board[row][col].setIcon(pacmanIcon); break;
            case 'G': board[row][col].setIcon(emptyIcon); break; // keep empty until per-enemy icon is placed
            default: board[row][col].setIcon(emptyIcon); break;
        }
    }

    // Set a specific icon for a cell without altering logical state
    public void setCellIcon(int row, int col, ImageIcon icon) {
        if (!isValidPosition(row, col)) return;
        board[row][col].setIcon(icon);
    }

    // Return the logical state character for a cell
    public char getCellState(int row, int col) {
        if (!isValidPosition(row, col)) return '#';
        return state[row][col];
    }

    // Allow dynamic pacman image at runtime (used for animation)
    public void setPacmanImage(ImageIcon icon, int row, int col) {
        if (icon == null) return;
        this.pacmanIcon = icon;
        if (isValidPosition(row, col)) {
            state[row][col] = 'P';
            board[row][col].setIcon(icon);
        }
    }

    // Set a per-enemy icon at a given cell without modifying the global enemyIcon
    public void setEnemyIconAt(ImageIcon icon, int row, int col) {
        if (icon == null) return;
        if (isValidPosition(row, col)) {
            state[row][col] = 'G';
            board[row][col].setIcon(icon);
        }
    }

    // Reset the board to initial layout (rebuild the logical state and default icons)
    public void resetBoard() {
        resetBoard(1);
    }

    // Reset the board for a specific level (allows map variations per level)
    public void resetBoard(int level) {
        initializeBoard();
        randomizeMap(level);
    }

    // Count remaining dots (small '+' and big 'O') on the logical board
    public int countDots() {
        int cnt = 0;
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                char s = state[i][j];
                if (s == '+' || s == 'O') cnt++;
            }
        }
        return cnt;
    }

    // Apply small random variations to the base map based on level
    private void randomizeMap(int level) {
        java.util.Random rnd = new java.util.Random(System.currentTimeMillis() + level * 997);
        int changes = Math.min(6, 1 + level);
        for (int k = 0; k < changes; k++) {
            int r = 1 + rnd.nextInt(Math.max(1, GameConstants.BOARD_SIZE - 2));
            int c = 1 + rnd.nextInt(Math.max(1, GameConstants.BOARD_SIZE - 2));
            // Don't overwrite player start or core ghost spawn
            if ((r == GameConstants.PACMAN_START_ROW && c == GameConstants.PACMAN_START_COL) ||
                (r == GameConstants.ENEMY_START_ROW && c == GameConstants.ENEMY_START_COL)) {
                continue;
            }
            char s = state[r][c];
            if (s == '#') {
                setCellState(r, c, '+'); // open wall into a dot
            } else if (s == '+') {
                if (rnd.nextInt(5) == 0) setCellState(r, c, 'O'); // sometimes make big dot
            }
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GameConstants.BOARD_SIZE && 
               col >= 0 && col < GameConstants.BOARD_SIZE;
    }
}

