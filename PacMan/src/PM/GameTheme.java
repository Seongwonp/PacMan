package PM;

import java.awt.*;

/**
 * 게임의 색상 테마를 관리하는 클래스
 * 복고풍 픽셀 아트 스타일 색상
 */
public class GameTheme {
    // 메인 색상 (retro)
    public static final Color PRIMARY = new Color(0, 0, 0);
    public static final Color SECONDARY = new Color(12, 12, 12);
    public static final Color ACCENT = new Color(0xFF, 0xD7, 0x00); // 팩맨 노란색
    public static final Color SUCCESS = new Color(0x4E, 0xCB, 0xC4);
    public static final Color DANGER = new Color(0xFF, 0x6B, 0x6B);

    // 텍스트 색상
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    public static final Color TEXT_SECONDARY = new Color(0xB0, 0xB0, 0xB0);

    // 배경 색상
    public static final Color BG_DARK = new Color(0, 0, 0);
    public static final Color BG_PANEL = new Color(18, 18, 18);
    public static final Color BORDER = new Color(0, 0, 64);

    // 게임 보드 색상
    public static final Color WALL = new Color(0, 0, 160); // bright blue wall
    public static final Color EMPTY = new Color(0, 0, 0);
    public static final Color DOT_SMALL = new Color(0xFF, 0xD7, 0x00);
    public static final Color DOT_BIG = new Color(0xFF, 0xA5, 0x00);

    // 폰트 (pixel/monospace feel)
    public static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 28);
    public static final Font FONT_LARGE = new Font("Monospaced", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Monospaced", Font.PLAIN, 10);
}
