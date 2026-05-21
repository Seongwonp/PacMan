package PM;

/**
 * 게임의 상태를 관리하는 클래스
 * 팩맨, 적, 게임 변수들을 한곳에서 관리
 */
public class GameState {
    // 팩맨 위치
    private int pacmanRow;
    private int pacmanCol;
    
    // 적 위치
    private int enemyRow;
    private int enemyCol;
    
    // 게임 변수
    private int dotsRemaining;
    private int enemyDelayCounter;
    private int lastEnemyDirection;
    private int pacmanDirection;
    
    // 임시 저장용
    private Object[] tempIcons = new Object[4];
    
    public GameState() {
        reset();
    }
    
    public void reset() {
        pacmanRow = GameConstants.PACMAN_START_ROW;
        pacmanCol = GameConstants.PACMAN_START_COL;
        enemyRow = GameConstants.ENEMY_START_ROW;
        enemyCol = GameConstants.ENEMY_START_COL;
        dotsRemaining = GameConstants.INITIAL_DOTS;
        enemyDelayCounter = GameConstants.ENEMY_DELAY_START;
        lastEnemyDirection = 1;
        pacmanDirection = GameConstants.DIRECTION_RIGHT;
    }
    
    // Getter 메서드
    public int getPacmanRow() { return pacmanRow; }
    public int getPacmanCol() { return pacmanCol; }
    public int getEnemyRow() { return enemyRow; }
    public int getEnemyCol() { return enemyCol; }
    public int getDotsRemaining() { return dotsRemaining; }
    public int getEnemyDelayCounter() { return enemyDelayCounter; }
    public int getLastEnemyDirection() { return lastEnemyDirection; }
    public int getPacmanDirection() { return pacmanDirection; }
    public Object getTempIcon(int index) { return tempIcons[index]; }
    
    // Setter 메서드
    public void setPacmanRow(int row) { pacmanRow = row; }
    public void setPacmanCol(int col) { pacmanCol = col; }
    public void setEnemyRow(int row) { enemyRow = row; }
    public void setEnemyCol(int col) { enemyCol = col; }
    public void setDotsRemaining(int dots) { dotsRemaining = Math.max(0, dots); }
    public void decrementDots() { dotsRemaining--; }
    public void decrementEnemyDelay() { 
        if (enemyDelayCounter > 0) {
            enemyDelayCounter--;
        }
    }
    public void setLastEnemyDirection(int direction) { lastEnemyDirection = direction; }
    public void setPacmanDirection(int dir) { pacmanDirection = dir; }
    public void setTempIcon(int index, Object icon) { tempIcons[index] = icon; }
}
