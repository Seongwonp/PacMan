/*
 * JB
 * 간단한 프로젝트
 * 팩맨 게임 프로그램
 * 'pacman'과 enemy 객체를 생성하고 'pacman'이 필요한 'dot'을 먹는 게임
 * 모든 'dot'을 먹었다면 성공 화면을 띄우고 프로그램이 종료됨
 * 만약 'pacman'이 'enemy'에게 잡히면 'pacman'이 지고 프로그램이 종료됨
 */

package PM;
//사운드 추가
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class PacMan {
	//'enemy'가 움직일때 나는소리
	public static void playEnemySound() {
	    try {
	        File soundFile = new File("Pac_Man_Ghost_Sound.wav");
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// 팩맨이 죽을 때 사운드 재생하는 메서드
	public static void playDeathSound() {
	    try {
	        File soundFile = new File("pacman_end_effect.wav");
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	// 팩맨이 움직일 때 사운드 재생하는 메서드
	public static void playMoveSound() {
	    try {
	        File soundFile = new File("Pac_Man_Waka_Waka.wav");
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// 창이 켜질 때 사운드 재생하는 메서드
	public static void playStartSound() {
	    try {
	        File soundFile = new File("Pacman_BGM.wav");
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {
		final JFrame frame = new JFrame(); // 게임 창 프레임 생성
		playStartSound();
		// 아이콘 이미지를 불러오는 ImageIcon 클래스
		final ImageIcon successIcon = new ImageIcon("successIcon.png");
		final ImageIcon failIcon = new ImageIcon("failIcon.png");
		final ImageIcon smallDot = new ImageIcon("smallDot.png");
		final ImageIcon bigDot = new ImageIcon("bigDot.png");
		final ImageIcon wall = new ImageIcon("wall.png");
		final ImageIcon enemy = new ImageIcon("enemy.png");
		final ImageIcon pacman = new ImageIcon("pacman.png");
		final ImageIcon empty = new ImageIcon("empty.png"); 

		// 성공, 실패를 나타내는 버튼 생성
		final JButton success = new JButton(successIcon);
		final JButton fail = new JButton(failIcon);
		final CardLayout card = new CardLayout();
		final JDialog dialog = new JDialog(); // 게임 결과를 표시할 다이얼로그

		// 랜덤 객체 생성 및 다이얼로그 초기 설정
		random = new Random();
		dialog.setSize(690, 650);
		dialog.setVisible(false);

		// 'pacman'과 'enemy'의 초기 위치 설정 및 게임 변수 초기화
		pmH = 12;
		pmW = 7;
		enemyH = 7;
		enemyW = 7;
		numOfDot = 79;
		start = 2;
		temp = empty;

		// 14x14 크기의 JLabel 배열 생성
		final JLabel[][] f = new JLabel[14][14];
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 14; j++) {
				f[i][j] = new JLabel();
			}
		}

		// 종료 버튼을 클릭했을 때 실행될 리스너 클래스
		class Blistener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		}

		// 타이머의 ActionListener 클래스
		class TListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				// 'start'가 0 이하이면 'where'에 1에서 4까지의 랜덤 값을 할당
                // 0 이상이면 'where'에 1을 할당하고 'start'를 감소
				if (start <= 0)
					where = 1 + random.nextInt(4);
				else {
					where = 1;
					start--;
				}
				
				// where 값에 따라 'enemy'를 이동시키는 switch 문
                // 'enemy'가 이동하려는 위치가 벽이 아니면 이동
				switch (where) {
				case 1:
					playEnemySound();
					if (!(f[enemyH - 1][enemyW].getIcon()).equals(wall)) {
						temp1 = f[enemyH - 1][enemyW].getIcon();
						f[enemyH - 1][enemyW].setIcon(enemy);
						f[enemyH][enemyW].setIcon(temp);
						temp = temp1;
						enemyH--;
					}
					break;
				case 2:
					playEnemySound();
					if (!(f[enemyH + 1][enemyW].getIcon()).equals(wall)) {
						temp2 = f[enemyH + 1][enemyW].getIcon();
						f[enemyH + 1][enemyW].setIcon(enemy);
						f[enemyH][enemyW].setIcon(temp);
						temp = temp2;
						enemyH++;
					}
					break;
				case 3:
					playEnemySound();
					if (!(f[enemyH][enemyW - 1].getIcon()).equals(wall)) {
						temp3 = f[enemyH][enemyW - 1].getIcon();
						f[enemyH][enemyW - 1].setIcon(enemy);
						f[enemyH][enemyW].setIcon(temp);
						temp = temp3;
						enemyW--;
					}
					break;
				case 4:
					playEnemySound();
					if (!(f[enemyH][enemyW + 1].getIcon()).equals(wall)) {
						temp4 = f[enemyH][enemyW + 1].getIcon();
						f[enemyH][enemyW + 1].setIcon(enemy);
						f[enemyH][enemyW].setIcon(temp);
						temp = temp4;
						enemyW++;
					}
					break;
				}
				// enemy가 pacman의 위치와 같다면 패배 다이얼로그를 표시
				if (enemyH == pmH && enemyW == pmW) {
					f[enemyH][enemyW].setIcon(enemy);
					dialog.add(fail);
					playDeathSound();
					dialog.setVisible(true);
				}
				
				// 특정 위치에 도달했을 때 벽을 생성
				if (enemyH == 5 && enemyW == 7) {
					f[6][7].setIcon(wall);
				}
				System.out.println(where); // 이동 방향을 출력
			}
		}
		
		// 키 입력을 처리하기 위한 KeyListener 클래스
		class KListener extends KeyAdapter {
			public void keyPressed(KeyEvent e) {
				
				// 모든 점(dot)을 먹었다면 성공 다이얼로그를 띄움
				if (numOfDot <= 0) {
					dialog.add(success);
					dialog.setVisible(true);
				}
				
				// 키 입력에 따라 'pacman'의 위치를 변경하고, 점(dot)을 먹으면 numOfDot 감소
				int key1 = e.getKeyCode();
				switch (key1) {
				
				case KeyEvent.VK_UP: 
					/* 위쪽 화살표 키
					   'pacman'이 위로 이동할 수 있는 경우
					   작은 점(smallDot)이나 큰 점(bigDot)을 먹거나, 비어있는 경우(empty) 이동
					   적(enemy)과 만나면 실패 다이얼로그를 띄움
					 */
					playMoveSound();
					if ((f[pmH - 1][pmW].getIcon()).equals(smallDot) || (f[pmH - 1][pmW].getIcon()).equals(empty)) {
						if ((f[pmH - 1][pmW].getIcon()).equals(smallDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH - 1][pmW].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmH--;
					} else if ((f[pmH - 1][pmW].getIcon()).equals(bigDot) || (f[pmH - 1][pmW].getIcon()).equals(empty)) {
						if ((f[pmH - 1][pmW].getIcon()).equals(bigDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH - 1][pmW].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmH--;
					} else if ((f[pmH - 1][pmW].getIcon()).equals(enemy)) {
						f[enemyH][enemyW].setIcon(enemy);
						dialog.add(fail);
						playDeathSound();
						dialog.setVisible(true);
					}
					break;
					
				case KeyEvent.VK_DOWN:
					/* 아래쪽 화살표 키
					  'pacman'이 아래로 이동할 수 있는 경우
 					  작은 점이나 큰 점을 먹거나, 비어있는 경우 이동
					  적과 만나면 실패 다이얼로그를 띄움
					 */
					playMoveSound();
					if ((f[pmH + 1][pmW].getIcon()).equals(smallDot) || (f[pmH + 1][pmW].getIcon()).equals(empty)) {
						if ((f[pmH + 1][pmW].getIcon()).equals(smallDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH + 1][pmW].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmH++;
					} else if ((f[pmH + 1][pmW].getIcon()).equals(bigDot) || (f[pmH + 1][pmW].getIcon()).equals(empty)) {
						if ((f[pmH + 1][pmW].getIcon()).equals(bigDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH + 1][pmW].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmH++;
					} else if ((f[pmH + 1][pmW].getIcon()).equals(enemy)) {
						f[enemyH][enemyW].setIcon(enemy);
						dialog.add(fail);
						playDeathSound();
						dialog.setVisible(true);
					}
					break;
					
				case KeyEvent.VK_LEFT:
					/* 왼쪽 화살표 키
					   'pacman'이 왼쪽으로 이동할 수 있는 경우
					   작은 점이나 큰 점을 먹거나, 비어있는 경우 이동
					   적과 만나면 실패 다이얼로그를 띄움
					 */
					playMoveSound();
					if ((f[pmH][pmW - 1].getIcon()).equals(smallDot) || (f[pmH][pmW - 1].getIcon()).equals(empty)) {
						if ((f[pmH][pmW - 1].getIcon()).equals(smallDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH][pmW - 1].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmW--;
					} else if ((f[pmH][pmW - 1].getIcon()).equals(bigDot) || (f[pmH][pmW - 1].getIcon()).equals(empty)) {
						if ((f[pmH][pmW - 1].getIcon()).equals(bigDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH][pmW - 1].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmW--;
					} else if ((f[pmH][pmW - 1].getIcon()).equals(enemy)) {
						f[enemyH][enemyW].setIcon(enemy);
						dialog.add(fail);
						playDeathSound();
						dialog.setVisible(true);
					}
					break;
					
				case KeyEvent.VK_RIGHT:
					/* 오른쪽 화살표 키
					  'pacman'이 오른쪽으로 이동할 수 있는 경우
					  작은 점이나 큰 점을 먹거나, 비어있는 경우 이동
					  적과 만나면 실패 다이얼로그를 띄움
					 */
					playMoveSound();
					if ((f[pmH][pmW + 1].getIcon()).equals(smallDot) || (f[pmH][pmW + 1].getIcon()).equals(empty)) {
						if ((f[pmH][pmW + 1].getIcon()).equals(smallDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH][pmW + 1].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmW++;
					} else if ((f[pmH][pmW + 1].getIcon()).equals(bigDot) || (f[pmH][pmW + 1].getIcon()).equals(empty)) {
						if ((f[pmH][pmW + 1].getIcon()).equals(bigDot) && (f[pmH][pmW].getIcon()).equals(pacman))
							numOfDot--;
						f[pmH][pmW + 1].setIcon(pacman);
						f[pmH][pmW].setIcon(empty);
						pmW++;
					} else if ((f[pmH][pmW + 1].getIcon()).equals(enemy)) {
						f[enemyH][enemyW].setIcon(enemy);
						dialog.add(fail);
						playDeathSound();
						dialog.setVisible(true);
					}
					break;
				}
				System.out.println(numOfDot); // 남아있는 점의 개수 출력
			}
		}
		
		// 버튼의 액션 이벤트를 처리하는 KListener, TListener 클래스 생성
		KListener listener = new KListener();
		TListener tListener = new TListener();

		// success 버튼과 fail 버튼에 동일한 액션 리스너('Blistener') 추가
		success.addActionListener(new Blistener());
		fail.addActionListener(new Blistener());

		// 500밀리초 간격으로 tListener를 실행하는 타이머 생성 및 시작
		Timer t = new Timer(500, tListener);
		t.start();

		// JPanel 생성 및 14x14 그리드 레이아웃 설정
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(14, 14));

		// 'frame'에 키 리스너 추가 및 포커스 요청
		frame.requestFocus();
		frame.addKeyListener(new KListener());

		// 기본 JPanel을 14x14 크기로 설정하고, 각 셀에 wall 아이콘과 키 리스너 추가
		for (int i = 0; i < 14; i++) {
		    for (int j = 0; j < 14; j++) {
		        f[i][j].setIcon(wall); // 각 셀에 wall 아이콘 설정
		        f[i][j].addKeyListener(listener); // 각 셀에 키 리스너 추가
		        panel.add(f[i][j]); // 패널에 셀 추가
		    }
		}

		
		// wall 이미지를 채운 JPanel에 특정 위치에 smallDot, bigDot, empty, pacman, enemy 이미지를 설정합니다.
		f[1][1].setIcon(smallDot);    f[2][1].setIcon(smallDot);    f[3][1].setIcon(smallDot);    f[4][1].setIcon(smallDot);    f[5][1].setIcon(smallDot);
		f[5][2].setIcon(bigDot);      f[5][3].setIcon(smallDot);    f[1][3].setIcon(smallDot);    f[2][3].setIcon(smallDot);    f[3][3].setIcon(smallDot);
		f[4][3].setIcon(smallDot);    f[1][4].setIcon(smallDot);    f[1][5].setIcon(smallDot);    f[1][6].setIcon(smallDot);    f[1][7].setIcon(smallDot);
		f[1][8].setIcon(smallDot);    f[1][9].setIcon(smallDot);    f[1][10].setIcon(smallDot);   f[1][11].setIcon(smallDot);   f[1][12].setIcon(smallDot);
		f[2][9].setIcon(smallDot);    f[2][12].setIcon(smallDot);   f[3][12].setIcon(smallDot);   f[4][12].setIcon(smallDot);   f[5][12].setIcon(smallDot);
		f[3][4].setIcon(smallDot);    f[3][5].setIcon(smallDot);    f[3][10].setIcon(smallDot);   f[3][11].setIcon(smallDot);
		f[4][5].setIcon(smallDot);    f[4][6].setIcon(smallDot);    f[4][7].setIcon(smallDot);    f[4][8].setIcon(smallDot);    f[4][9].setIcon(smallDot);
		f[4][10].setIcon(smallDot);   f[2][7].setIcon(smallDot);    f[3][7].setIcon(smallDot);    f[4][10].setIcon(smallDot);   f[4][11].setIcon(bigDot);
		f[5][11].setIcon(smallDot);   f[6][11].setIcon(smallDot);   f[7][11].setIcon(smallDot);   f[7][12].setIcon(smallDot);   f[8][12].setIcon(smallDot);
		f[9][12].setIcon(smallDot);   f[10][12].setIcon(smallDot);  f[11][12].setIcon(smallDot);  f[5][7].setIcon(empty);      f[6][2].setIcon(smallDot);
		f[7][1].setIcon(smallDot);    f[9][11].setIcon(bigDot);     f[11][1].setIcon(bigDot);     f[11][3].setIcon(smallDot);   f[11][11].setIcon(smallDot);
		f[7][2].setIcon(smallDot);    f[7][3].setIcon(smallDot);    f[8][1].setIcon(smallDot);    f[9][1].setIcon(smallDot);    f[9][2].setIcon(smallDot);
		f[9][3].setIcon(smallDot);    f[9][4].setIcon(smallDot);    f[9][5].setIcon(smallDot);    f[9][6].setIcon(smallDot);    f[12][1].setIcon(smallDot);
		f[12][2].setIcon(smallDot);   f[12][3].setIcon(smallDot);   f[12][4].setIcon(smallDot);   f[12][5].setIcon(smallDot);   f[12][9].setIcon(smallDot);
		f[12][10].setIcon(smallDot);  f[12][11].setIcon(smallDot);  f[10][5].setIcon(smallDot);   f[11][5].setIcon(smallDot);   f[10][6].setIcon(smallDot);
		f[10][7].setIcon(smallDot);   f[10][8].setIcon(smallDot);   f[10][9].setIcon(smallDot);   f[11][7].setIcon(smallDot);   f[12][7].setIcon(pacman);
		f[9][8].setIcon(smallDot);    f[9][9].setIcon(smallDot);    f[11][9].setIcon(smallDot);   f[6][5].setIcon(empty);      f[6][6].setIcon(empty);
		f[6][7].setIcon(empty);       f[6][8].setIcon(empty);       f[6][9].setIcon(empty);       f[7][5].setIcon(empty);      f[7][6].setIcon(empty);
		f[7][7].setIcon(enemy);       f[7][8].setIcon(empty);       f[7][9].setIcon(empty);

		// JFrame에 패널을 추가하고 x를 누르면 프로그램이 종료되도록 설정합니다.
				frame.add(panel);
				frame.setTitle("게임 창");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
			}

			// 게임 JFrame의 변수들을 초기화합니다.
			private static Random random;
			private static int pmH, pmW, enemyH, enemyW, numOfDot, where, start;
			private static Icon temp1, temp2, temp3, temp4, temp;
			private static final int FRAME_WIDTH = 690;
			private static final int FRAME_HEIGHT = 650;
}
