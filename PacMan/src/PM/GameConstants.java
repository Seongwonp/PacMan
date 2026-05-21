package PM;

/**
 * 게임의 모든 상수를 정의하는 클래스
 * 매직 넘버를 제거하고 게임 설정을 중앙 집중식으로 관리
 */
public class GameConstants {
    // 게임 보드
    public static final int BOARD_SIZE = 14;
    // Smaller tile for pixel-style look
    public static final int BOARD_CELL_SIZE = 30;
    
    // 팩맨 초기 위치
    public static final int PACMAN_START_ROW = 12;
    public static final int PACMAN_START_COL = 7;
    
    // 적 초기 위치
    public static final int ENEMY_START_ROW = 7;
    public static final int ENEMY_START_COL = 7;
    
    // 게임 상태
    public static final int INITIAL_DOTS = 79;
    public static final int ENEMY_DELAY_START = 2; // 적 움직임 시작 지연
    // Faster tick for smoother movement
    public static final int TIMER_INTERVAL = 300; // 적 이동 타이머 (밀리초)
    
    // 프레임 크기
    public static final int FRAME_WIDTH = 750;
    public static final int FRAME_HEIGHT = 750;
    
    // 적 특수 벽 생성
    public static final int WALL_CREATE_ROW = 5;
    public static final int WALL_CREATE_COL = 7;
    public static final int WALL_SPAWN_ROW = 6;
    public static final int WALL_SPAWN_COL = 7;
    
    // 움직임 방향
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;
}
