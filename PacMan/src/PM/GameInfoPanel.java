package PM;

import javax.swing.*;
import java.awt.*;

/**
 * 게임 상태 정보를 표시하는 패널
 * 점수, 남은 점의 개수, 게임 상태 등을 표시
 */
public class GameInfoPanel extends JPanel {
    private int dotsRemaining;
    private String gameStatus;
    
    public GameInfoPanel() {
        setBackground(GameTheme.BG_PANEL);
        setBorder(BorderFactory.createLineBorder(GameTheme.BORDER, 2));
        setPreferredSize(new Dimension(0, 50));
        dotsRemaining = GameConstants.INITIAL_DOTS;
        gameStatus = "Ready";
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(GameTheme.FONT_NORMAL);
        g2d.setColor(GameTheme.TEXT_PRIMARY);
        
        // 남은 점 개수
        g2d.drawString("남은 점: " + dotsRemaining, 20, 35);
        
        // 게임 상태
        g2d.drawString(gameStatus, getWidth() / 2 - 50, 35);
        
        // 조작 가이드
        g2d.setFont(GameTheme.FONT_SMALL);
        g2d.setColor(GameTheme.TEXT_SECONDARY);
        g2d.drawString("← → ↑ ↓: 이동 | P: 일시정지 | R: 리스타트", getWidth() - 280, 35);
    }
    
    public void setDotsRemaining(int dots) {
        this.dotsRemaining = Math.max(0, dots);
        repaint();
    }
    
    public void setGameStatus(String status) {
        this.gameStatus = status;
        repaint();
    }
}
