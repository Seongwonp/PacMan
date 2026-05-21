/*
 * 팩맨 게임 - Modern Edition
 * Java Swing을 이용한 클래식 팩맨 게임
 * 간단하고 깔끔한 구조로 리팩토링됨
 */

package PM;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class PacMan {
    private JFrame frame;
    private GameBoard gameBoard;
    private GameLogic gameLogic;
    private GameInfoPanel infoPanel;
    private JPanel boardPanel;
    private JDialog resultDialog;
    
    private ImageIcon smallDot, bigDot, wall, enemy, pacman, empty;
    private ImageIcon[] ghostIcons;
    private ImageIcon successIcon, failIcon;
    
    private Timer gameTimer;
    private Timer pacmanAnimTimer;
    private Timer startBgmTimer;
    private boolean gameRunning = true;

    public PacMan() {
        showStartScreen();
    }

    private void initializeGame() {
        // stop start-screen bgm if running
        if (startBgmTimer != null) {
            startBgmTimer.stop();
            startBgmTimer = null;
        }

        // 이미지 로드
        loadImages();
        
        // 게임 로직 초기화
        // pass first ghost icon as default to GameBoard
        ImageIcon defaultGhost = (ghostIcons != null && ghostIcons.length > 0) ? ghostIcons[0] : null;
        gameBoard = new GameBoard(smallDot, bigDot, wall, defaultGhost, pacman, empty);
        // generate a slightly randomized map for the starting level (adds variety between plays)
        gameBoard.resetBoard(1);
        gameLogic = new GameLogic(gameBoard, wall, pacman, smallDot, bigDot, empty, ghostIcons);
        
        // UI 초기화
        setupUI();
        
        // 이벤트 리스너 추가
        setupEventListeners();
        
        // start pacman animation timer
        pacmanAnimTimer = new Timer(160, e -> {
            mouthOpen = !mouthOpen;
            int prow = gameLogic.getState().getPacmanRow();
            int pcol = gameLogic.getState().getPacmanCol();
            int pdir = gameLogic.getState().getPacmanDirection();
            gameBoard.setPacmanImage(createPacmanIcon(pdir, mouthOpen), prow, pcol);
            boardPanel.repaint();
        });
        pacmanAnimTimer.start();

        // 게임 타이머 시작
        startGameTimer();

        // 배경 음악 시작 (게임이 실제 시작될 때만 재play)
        SoundManager.playStartSound();
    }

    private void loadImages() {
        // Generate icons programmatically (no PNGs)
        smallDot = createDotIcon(8, GameTheme.DOT_SMALL);
        bigDot = createDotIcon(14, GameTheme.DOT_BIG);
        wall = createBlockIcon(GameTheme.WALL);
        // Create a palette of ghost icons (classic colors)
        java.awt.Color[] ghostColors = new java.awt.Color[] {
            new java.awt.Color(0xFF, 0x00, 0x00),        // red
            new java.awt.Color(0xFF, 0x69, 0xB4),        // pink
            new java.awt.Color(0x00, 0xFF, 0xFF),        // cyan
            new java.awt.Color(0xFF, 0xA5, 0x00)         // orange
        };
        ghostIcons = new ImageIcon[ghostColors.length];
        for (int i = 0; i < ghostColors.length; i++) {
            ghostIcons[i] = createGhostIcon(ghostColors[i]);
        }
        // keep a legacy single enemy reference for compatibility
        enemy = ghostIcons.length > 0 ? ghostIcons[0] : createGhostIcon(GameTheme.DANGER);
        pacman = createPacmanIcon(GameConstants.DIRECTION_RIGHT, true);
        empty = createEmptyIcon();

        successIcon = createSimpleIcon(GameTheme.SUCCESS, "✓");
        failIcon = createSimpleIcon(GameTheme.DANGER, "✕");
    }

    // Helper icon generation
    private ImageIcon createDotIcon(int diameter, java.awt.Color color) {
        int w = GameConstants.BOARD_CELL_SIZE;
        int h = GameConstants.BOARD_CELL_SIZE;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new java.awt.Color(0, 0, 0, 0));
        g2.fillRect(0, 0, w, h);
        g2.setColor(color);
        int size = Math.max(2, diameter / 2);
        int x = (w - size) / 2;
        int y = (h - size) / 2;
        g2.fillOval(x, y, size, size);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createBlockIcon(java.awt.Color color) {
        int w = GameConstants.BOARD_CELL_SIZE;
        int h = GameConstants.BOARD_CELL_SIZE;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        // outer border
        g2.setColor(GameTheme.BORDER);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
        // inner block
        g2.setColor(color);
        g2.fillRoundRect(2, 2, w - 5, h - 5, 6, 6);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createPacmanIcon(int direction, boolean open) {
        int w = GameConstants.BOARD_CELL_SIZE;
        int h = GameConstants.BOARD_CELL_SIZE;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new java.awt.Color(0, 0, 0, 0));
        g2.fillRect(0, 0, w, h);
        g2.setColor(GameTheme.ACCENT);
        int margin = Math.max(3, w / 7);
        if (open) {
            int mouth = 42;
            int start = 0;
            switch (direction) {
                case GameConstants.DIRECTION_UP: start = 90 + mouth / 2; break;
                case GameConstants.DIRECTION_DOWN: start = 270 + mouth / 2; break;
                case GameConstants.DIRECTION_LEFT: start = 180 + mouth / 2; break;
                case GameConstants.DIRECTION_RIGHT:
                default: start = mouth / 2; break;
            }
            g2.fillArc(margin, margin, w - margin * 2, h - margin * 2, start, 360 - mouth);
            g2.setColor(new java.awt.Color(0, 0, 0, 0));
            int cx = w / 2;
            int cy = h / 2;
            int mouthX = cx;
            int mouthY = cy;
            int tip = Math.max(4, w / 3);
            int spread = Math.max(5, w / 5);
            java.awt.Polygon wedge = new java.awt.Polygon();
            switch (direction) {
                case GameConstants.DIRECTION_UP:
                    wedge.addPoint(mouthX, mouthY);
                    wedge.addPoint(mouthX - spread, mouthY - tip);
                    wedge.addPoint(mouthX + spread, mouthY - tip);
                    break;
                case GameConstants.DIRECTION_DOWN:
                    wedge.addPoint(mouthX, mouthY);
                    wedge.addPoint(mouthX - spread, mouthY + tip);
                    wedge.addPoint(mouthX + spread, mouthY + tip);
                    break;
                case GameConstants.DIRECTION_LEFT:
                    wedge.addPoint(mouthX, mouthY);
                    wedge.addPoint(mouthX - tip, mouthY - spread);
                    wedge.addPoint(mouthX - tip, mouthY + spread);
                    break;
                case GameConstants.DIRECTION_RIGHT:
                default:
                    wedge.addPoint(mouthX, mouthY);
                    wedge.addPoint(mouthX + tip, mouthY - spread);
                    wedge.addPoint(mouthX + tip, mouthY + spread);
                    break;
            }
            g2.setColor(new java.awt.Color(0, 0, 0));
            g2.fillPolygon(wedge);
        } else {
            g2.fillOval(margin, margin, w - margin * 2, h - margin * 2);
        }
        // small eye for classic look
        g2.setColor(java.awt.Color.BLACK);
        int eyeSize = Math.max(2, w / 10);
        int eyeX = w / 2 + (direction == GameConstants.DIRECTION_LEFT ? -2 : 3);
        int eyeY = h / 3;
        g2.fillOval(eyeX, eyeY, eyeSize, eyeSize);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createGhostIcon(java.awt.Color color) {
        int w = GameConstants.BOARD_CELL_SIZE;
        int h = GameConstants.BOARD_CELL_SIZE;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new java.awt.Color(0, 0, 0, 0));
        g2.fillRect(0, 0, w, h);
        // rounded body
        g2.setColor(color);
        g2.fillOval(3, 2, w - 6, h - 7);
        g2.fillRoundRect(3, h / 3, w - 6, h / 2, 10, 10);
        // wavy bottom
        int scallop = Math.max(4, w / 5);
        for (int x = 3; x < w - 3; x += scallop) {
            g2.fillOval(x, h - 8, scallop, scallop);
        }
        // eyes
        g2.setColor(java.awt.Color.WHITE);
        g2.fillOval(w / 4, h / 3, 5, 5);
        g2.fillOval(2 * w / 3 - 4, h / 3, 5, 5);
        g2.setColor(java.awt.Color.BLUE);
        g2.fillOval(w / 4 + 1, h / 3 + 1, 3, 3);
        g2.fillOval(2 * w / 3 - 3, h / 3 + 1, 3, 3);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createEmptyIcon() {
        int w = GameConstants.BOARD_CELL_SIZE;
        int h = GameConstants.BOARD_CELL_SIZE;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setColor(new java.awt.Color(0,0,0,0));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createSimpleIcon(java.awt.Color color, String text) {
        int w = 40, h = 40;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, w, h);
        g2.setColor(java.awt.Color.WHITE);
        g2.setFont(GameTheme.FONT_LARGE);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);
        g2.dispose();
        return new ImageIcon(img);
    }

    private void setupUI() {
        frame = new JFrame("PacMan - Classic");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        // 정보 패널
        infoPanel = new GameInfoPanel();
        infoPanel.setDotsRemaining(gameLogic.getState().getDotsRemaining());
        
        // 게임 보드 패널
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(GameConstants.BOARD_SIZE, GameConstants.BOARD_SIZE));
        boardPanel.setBackground(GameTheme.BG_DARK);
        boardPanel.setBorder(BorderFactory.createLineBorder(GameTheme.BORDER, 2));
        
        JLabel[][] boardCells = gameBoard.getBoard();
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                // Make cells match board cell size
                boardCells[i][j].setPreferredSize(new java.awt.Dimension(GameConstants.BOARD_CELL_SIZE, GameConstants.BOARD_CELL_SIZE));
                boardPanel.add(boardCells[i][j]);
            }
        }
        
        // 결과 다이얼로그
        resultDialog = new JDialog(frame, "Game Over", true);
        resultDialog.setResizable(false);
        resultDialog.setSize(400, 200);
        resultDialog.getContentPane().setBackground(GameTheme.BG_DARK);
        
        // 프레임 설정
        frame.setLayout(new BorderLayout());
        frame.add(infoPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setBackground(GameTheme.BG_DARK);
        frame.getContentPane().setBackground(GameTheme.BG_DARK);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // Bring to front on macOS/Windows
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);
        frame.toFront();
        frame.requestFocusInWindow();
    }

    private void setupEventListeners() {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameRunning) return;
                
                int key = e.getKeyCode();
                int direction = -1;
                
                switch (key) {
                    case KeyEvent.VK_UP:
                        direction = GameConstants.DIRECTION_UP;
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = GameConstants.DIRECTION_DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = GameConstants.DIRECTION_LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = GameConstants.DIRECTION_RIGHT;
                        break;
                }
                
                if (direction != -1) {
                    boolean isGameOver = gameLogic.movePacman(direction);
                    updateUI();
                    
                    if (isGameOver) {
                        endGame(false);
                    } else if (gameLogic.isGameWon()) {
                        endGame(true);
                    }
                }
            }
        });
    }

    private void startGameTimer() {
        gameTimer = new Timer(GameConstants.TIMER_INTERVAL, e -> {
            if (!gameRunning) return;

            boolean collision = gameLogic.moveEnemies();
            updateUI();

            if (collision) {
                endGame(false);
            } else if (gameLogic.isGameWon()) {
                // Level up without ending the game
                gameTimer.stop();
                if (pacmanAnimTimer != null) pacmanAnimTimer.stop();

                gameLogic.nextLevel();
                infoPanel.setGameStatus("Level " + gameLogic.getLevel());

                // Rebuild board UI to reflect new board state
                refreshBoard();

                if (pacmanAnimTimer != null) pacmanAnimTimer.start();
                gameTimer.start();
            }
        });
        gameTimer.start();
    }

    private boolean mouthOpen = true;

    private void updateUI() {
        // Update info
        infoPanel.setDotsRemaining(gameLogic.getState().getDotsRemaining());

        // Update enemies icons (in case positions changed)
        java.util.List<GameLogic.EnemyInfo> enemyInfos = gameLogic.getEnemyInfos();
        for (GameLogic.EnemyInfo ei : enemyInfos) {
            gameBoard.setCellIcon(ei.row, ei.col, ei.icon);
        }

        boardPanel.repaint();
    }

    private void refreshBoard() {
        if (boardPanel == null || gameBoard == null) return;
        boardPanel.removeAll();
        JLabel[][] boardCells = gameBoard.getBoard();
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                boardCells[i][j].setPreferredSize(new java.awt.Dimension(GameConstants.BOARD_CELL_SIZE, GameConstants.BOARD_CELL_SIZE));
                boardPanel.add(boardCells[i][j]);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
        frame.pack();
        frame.requestFocusInWindow();
    }

    private void endGame(boolean isWin) {
        gameRunning = false;
        if (gameTimer != null) gameTimer.stop();
        if (pacmanAnimTimer != null) pacmanAnimTimer.stop();
        
        // Show modal dialog with restart option
        resultDialog.getContentPane().removeAll();
        JPanel panel = new JPanel();
        panel.setBackground(GameTheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel(isWin ? "YOU WIN!" : "GAME OVER");
        title.setForeground(GameTheme.TEXT_PRIMARY);
        title.setFont(GameTheme.FONT_LARGE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        
        JButton restart = new JButton("Restart");
        restart.setAlignmentX(Component.CENTER_ALIGNMENT);
        restart.addActionListener(e -> {
            resultDialog.setVisible(false);
            // Reinitialize
            frame.dispose();
            new PacMan();
        });
        panel.add(restart);
        
        JButton quit = new JButton("Quit");
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.addActionListener(e -> System.exit(0));
        panel.add(Box.createVerticalStrut(6));
        panel.add(quit);
        
        resultDialog.getContentPane().add(panel);
        infoPanel.setGameStatus(isWin ? "🎉 승리!" : "💀 게임 오버!");
        resultDialog.pack();
        resultDialog.setLocationRelativeTo(frame);
        resultDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PacMan());
    }

    // Simple start screen
    private void showStartScreen() {
        JFrame startFrame = new JFrame("PacMan - Classic");
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setSize(400, 300);
        startFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(GameTheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PAC-MAN");
        title.setFont(GameTheme.FONT_TITLE);
        title.setForeground(GameTheme.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JLabel subtitle = new JLabel("Classic Arcade");
        subtitle.setFont(GameTheme.FONT_LARGE);
        subtitle.setForeground(GameTheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));

        JButton start = new JButton("Start Game");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(e -> {
            startFrame.dispose();
            initializeGame();
        });
        panel.add(start);

        startFrame.getContentPane().add(panel);

        // Play start-screen BGM once immediately, then every 10s
        SoundManager.playStartSound();
        startBgmTimer = new Timer(10000, ev -> SoundManager.playStartSound());
        startBgmTimer.setInitialDelay(10000);
        startBgmTimer.start();

        startFrame.setVisible(true);
    }
}
