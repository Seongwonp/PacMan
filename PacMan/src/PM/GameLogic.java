package PM;

import javax.swing.*;
import java.util.*;

/**
 * 게임의 핵심 로직을 관리하는 클래스
 * 멀티 적, 레벨, 간단한 추적/랜덤 AI 등을 처리
 */
public class GameLogic {
    private GameState state;
    private GameBoard board;
    private Random random;
    private ImageIcon wall, pacman, smallDot, bigDot, empty;
    private ImageIcon[] ghostIcons;
    private List<Enemy> enemies;
    private int level = 1;

    // Public lightweight snapshot for UI
    public static class EnemyInfo {
        public final int row;
        public final int col;
        public final ImageIcon icon;
        public EnemyInfo(int r, int c, ImageIcon icon) { this.row = r; this.col = c; this.icon = icon; }
    }

    // Internal enemy representation
    private static class Enemy {
        int row, col;
        int behavior; // 0 random, 1 chase
        int delay; // move delay counter
        ImageIcon icon;
        char prevState; // what was under the ghost (so we can restore it)
        int lastDir; // last move direction to reduce jitter
        Enemy(int r, int c, ImageIcon icon, int behavior, char prevState) {
            this.row = r; this.col = c; this.icon = icon; this.behavior = behavior; this.delay = 0; this.prevState = prevState; this.lastDir = -1;
        }
    }

    public GameLogic(GameBoard board, ImageIcon wall, ImageIcon pacman,
                     ImageIcon smallDot, ImageIcon bigDot, ImageIcon empty,
                     ImageIcon[] ghostIcons) {
        this.board = board;
        this.state = new GameState();
        this.random = new Random();
        this.wall = wall;
        this.pacman = pacman;
        this.smallDot = smallDot;
        this.bigDot = bigDot;
        this.empty = empty;
        this.ghostIcons = ghostIcons != null ? ghostIcons : new ImageIcon[0];
        this.enemies = new ArrayList<>();

        // synchronize dot count with current board layout
        state.setDotsRemaining(board.countDots());

        // place initial enemies according to level 1
        createEnemiesForLevel(level);
    }

    public GameState getState() { return state; }
    public int getLevel() { return level; }

    public List<EnemyInfo> getEnemyInfos() {
        List<EnemyInfo> out = new ArrayList<>();
        for (Enemy e : enemies) out.add(new EnemyInfo(e.row, e.col, e.icon));
        return out;
    }

    private int reverseDir(int d) {
        switch (d) {
            case GameConstants.DIRECTION_UP: return GameConstants.DIRECTION_DOWN;
            case GameConstants.DIRECTION_DOWN: return GameConstants.DIRECTION_UP;
            case GameConstants.DIRECTION_LEFT: return GameConstants.DIRECTION_RIGHT;
            case GameConstants.DIRECTION_RIGHT: return GameConstants.DIRECTION_LEFT;
            default: return -1;
        }
    }

    private void createEnemiesForLevel(int lvl) {
        // Clear existing enemies from board (restore underlying states)
        for (Enemy e : enemies) {
            board.setCellState(e.row, e.col, e.prevState);
        }
        enemies.clear();

        int maxGhosts = Math.max(1, ghostIcons.length);
        int count = Math.min(lvl, maxGhosts);
        // Spawn positions (cluster near center) - ensure within board
        int[][] spawns = new int[][]{{7,7},{7,6},{6,7},{6,6}};
        for (int i = 0; i < count; i++) {
            int r = spawns[i % spawns.length][0];
            int c = spawns[i % spawns.length][1];
            int behavior = (i % 2 == 0) ? 1 : 0; // alternate chase/random
            ImageIcon icon = ghostIcons.length > 0 ? ghostIcons[i % ghostIcons.length] : null;
            char prev = board.getCellState(r, c);
            Enemy en = new Enemy(r, c, icon, behavior, prev);
            enemies.add(en);
            board.setCellState(r, c, 'G');
            board.setCellIcon(r, c, icon);
        }
    }

    /**
     * 팩맨을 주어진 방향으로 이동시킵니다
     * @return 게임 오버 여부
     */
    public boolean movePacman(int direction) {
        int newRow = state.getPacmanRow();
        int newCol = state.getPacmanCol();

        switch (direction) {
            case GameConstants.DIRECTION_UP:    newRow--; break;
            case GameConstants.DIRECTION_DOWN:  newRow++; break;
            case GameConstants.DIRECTION_LEFT:  newCol--; break;
            case GameConstants.DIRECTION_RIGHT: newCol++; break;
            default: return false;
        }

        if (!board.isValidPosition(newRow, newCol)) return false;

        char nextState = board.getCellState(newRow, newCol);

        // 적과 충돌
        if (nextState == 'G') {
            SoundManager.playDeathSound();
            return true;
        }

        // 벽과 충돌
        if (nextState == '#') return false;

        // 점 먹기
        if (nextState == '+' || nextState == 'O') {
            state.decrementDots();
            SoundManager.playMoveSound();
        } else if (nextState == ' ') {
            SoundManager.playMoveSound();
        }

        // 기록 방향
        state.setPacmanDirection(direction);

        // 팩맨 이동 (logical state updates)
        board.setCellState(state.getPacmanRow(), state.getPacmanCol(), ' ');
        state.setPacmanRow(newRow);
        state.setPacmanCol(newCol);
        board.setCellState(newRow, newCol, 'P');

        return false;
    }

    /**
     * 모든 적을 이동시킵니다
     * @return 팩맨과 충돌 여부
     */
    public boolean moveEnemies() {
        for (Enemy e : enemies) {
            if (e.delay > 0) { e.delay--; continue; }

            int prow = state.getPacmanRow();
            int pcol = state.getPacmanCol();
            int er = e.row;
            int ec = e.col;
            int newR = er;
            int newC = ec;
            int chosenDir = -1;
            boolean moved = false;

            // Chase behavior uses BFS for shortest path
            if (e.behavior == 1) {
                int n = GameConstants.BOARD_SIZE;
                boolean[][] visited = new boolean[n][n];
                int[][] prt = new int[n][n];
                int[][] pct = new int[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) { prt[i][j] = -1; pct[i][j] = -1; }
                }
                java.util.Queue<int[]> q = new java.util.ArrayDeque<>();
                q.add(new int[]{er, ec});
                visited[er][ec] = true;
                int[] dirs = new int[]{GameConstants.DIRECTION_UP, GameConstants.DIRECTION_LEFT, GameConstants.DIRECTION_RIGHT, GameConstants.DIRECTION_DOWN};
                while (!q.isEmpty()) {
                    int[] cur = q.poll();
                    int r = cur[0], c = cur[1];
                    if (r == prow && c == pcol) break;
                    for (int dir : dirs) {
                        int rr = r, cc = c;
                        switch (dir) {
                            case GameConstants.DIRECTION_UP: rr = r - 1; break;
                            case GameConstants.DIRECTION_DOWN: rr = r + 1; break;
                            case GameConstants.DIRECTION_LEFT: cc = c - 1; break;
                            case GameConstants.DIRECTION_RIGHT: cc = c + 1; break;
                        }
                        if (!board.isValidPosition(rr, cc) || visited[rr][cc]) continue;
                        char s = board.getCellState(rr, cc);
                        if (s == '#' || (s == 'G' && !(rr == er && cc == ec))) continue;
                        visited[rr][cc] = true;
                        prt[rr][cc] = r; pct[rr][cc] = c;
                        q.add(new int[]{rr, cc});
                    }
                }
                if (visited[prow][pcol]) {
                    java.util.List<int[]> path = new java.util.ArrayList<>();
                    int cr = prow, cc = pcol;
                    while (!(cr == er && cc == ec)) {
                        path.add(new int[]{cr, cc});
                        int prr = prt[cr][cc];
                        int pcc = pct[cr][cc];
                        if (prr == -1) break;
                        cr = prr; cc = pcc;
                    }
                    if (!path.isEmpty()) {
                        int[] next = path.get(path.size() - 1);
                        newR = next[0]; newC = next[1]; moved = true;
                        if (newR == er - 1) chosenDir = GameConstants.DIRECTION_UP;
                        else if (newR == er + 1) chosenDir = GameConstants.DIRECTION_DOWN;
                        else if (newC == ec - 1) chosenDir = GameConstants.DIRECTION_LEFT;
                        else if (newC == ec + 1) chosenDir = GameConstants.DIRECTION_RIGHT;
                    }
                }
                // add unpredictability
                if (moved && chosenDir == reverseDir(e.lastDir) && random.nextInt(100) < 60) {
                    moved = false;
                }
            }

            if (!moved) {
                java.util.List<Integer> choices = new java.util.ArrayList<>(java.util.Arrays.asList(GameConstants.DIRECTION_UP, GameConstants.DIRECTION_DOWN, GameConstants.DIRECTION_LEFT, GameConstants.DIRECTION_RIGHT));
                java.util.Collections.shuffle(choices, random);
                for (int dir : choices) {
                    if (dir == reverseDir(e.lastDir)) continue;
                    int candR = er, candC = ec;
                    switch (dir) {
                        case GameConstants.DIRECTION_UP: candR = er - 1; break;
                        case GameConstants.DIRECTION_DOWN: candR = er + 1; break;
                        case GameConstants.DIRECTION_LEFT: candC = ec - 1; break;
                        case GameConstants.DIRECTION_RIGHT: candC = ec + 1; break;
                    }
                    if (!board.isValidPosition(candR, candC)) continue;
                    char s = board.getCellState(candR, candC);
                    if (s == '#' || s == 'G') continue;
                    newR = candR; newC = candC; chosenDir = dir; moved = true; break;
                }
                if (!moved) {
                    for (int dir : java.util.Arrays.asList(GameConstants.DIRECTION_UP, GameConstants.DIRECTION_DOWN, GameConstants.DIRECTION_LEFT, GameConstants.DIRECTION_RIGHT)) {
                        int candR = er, candC = ec;
                        switch (dir) {
                            case GameConstants.DIRECTION_UP: candR = er - 1; break;
                            case GameConstants.DIRECTION_DOWN: candR = er + 1; break;
                            case GameConstants.DIRECTION_LEFT: candC = ec - 1; break;
                            case GameConstants.DIRECTION_RIGHT: candC = ec + 1; break;
                        }
                        if (!board.isValidPosition(candR, candC)) continue;
                        char s = board.getCellState(candR, candC);
                        if (s == '#' || s == 'G') continue;
                        newR = candR; newC = candC; chosenDir = dir; moved = true; break;
                    }
                }
            }

            // If move results in pacman collision
            if (newR == prow && newC == pcol) {
                SoundManager.playDeathSound();
                return true;
            }

            // Restore previous cell (keep dots/wall if present)
            board.setCellState(er, ec, e.prevState);

            // Capture what's under the new cell so we can restore it later
            char prevAtNew = board.getCellState(newR, newC);
            e.prevState = prevAtNew;
            e.row = newR;
            e.col = newC;
            board.setCellState(newR, newC, 'G');
            board.setCellIcon(newR, newC, e.icon);

            // Play enemy move sound
            SoundManager.playEnemySound();

            e.lastDir = chosenDir;
            e.delay = 1 + random.nextInt(Math.max(1, Math.max(1, 4 - level)));
        }
        return false;
    }

    public boolean isGameWon() {
        return state.getDotsRemaining() <= 0;
    }

    public void nextLevel() {
        level++;
        board.resetBoard(level);
        state.reset();
        state.setDotsRemaining(board.countDots());
        // ensure pacman initial position is correct
        board.setCellState(state.getPacmanRow(), state.getPacmanCol(), 'P');
        createEnemiesForLevel(level);
    }
}
