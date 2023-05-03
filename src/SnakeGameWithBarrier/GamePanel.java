package SnakeGameWithBarrier;

import java.awt.*;

import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class GamePanel extends JPanel implements ActionListener{
	
	static final int SCREEN_WIDTH = 600, SCREEN_HEIGHT = 600, UNIT_SIZE = 25, GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE, DELAY = 100;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6, applesEaten = 0, appleX, appleY, count = 0, rgb1, rgb2, rgb3;
	char direction = 'W'; //R - L - U - D
	boolean running = false, gameOver = false, record = false;
	String player, directory;
	Color colorForApple;
	//boolean canStart = false;
	Timer timer;
	Random random;
	JButton startGameButton, restart, enter, store, exit;
	JTextField text;
	int[] bestScore = new int[5];
	String [] bestPlayers = new String[5];
	Map<Integer, String> best = new TreeMap<>();
	

	public GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGameButton = new JButton("Start Game");
		text = new JTextField();
		restart = new JButton("Restart");
		enter = new JButton("Enter");
		store = new JButton("Store");
		exit = new JButton("Exit");
		for(int i=0; i<5; i++) bestScore[i] = 0;	
		directory = "C:/Users/Maria/Desktop/Eclipse/SnakeTest/src/ScoresBarrier.txt";
	}
	
	public void initiate(Graphics g) {
		
		readScores();

		this.setBackground(new Color(247, 192, 91));
		
		
		//Text  Options: Stencil, Lucida Sans Unicode, Arial Rounded MT Bold,
		//ISOCTEUR, Bodoni MT Black, Ebrima
		g.setColor(Color.red);
		g.setFont(new Font("Bodoni MT Black", Font.BOLD, 60));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Start Game", (SCREEN_WIDTH - metrics1.stringWidth("Start Game"))/2, g.getFont().getSize()+100);
		
		//Enter your name
		//Consolas, Perpetua, RomanP, Arial, Calibri
		
		int rectWidth = 80;
		int rectHeight = 25;
		int diference = (rectHeight/2)+5;
		int space = 20;
		int startGameWidth = 120;
		int startGameHeight = 40;
		int diferenceWidth = startGameWidth/2;
		
		g.setColor(Color.black);
		g.setFont(new Font("Consolas", Font.BOLD, 25));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Your name: ", ((SCREEN_WIDTH-metrics2.stringWidth("Your name :"))/3)-space, (SCREEN_HEIGHT/2));
		
		//Rect to write the name
		text.setBounds((SCREEN_WIDTH/2)-space, (SCREEN_HEIGHT/2)-diference, rectWidth, rectHeight);
		text.setVisible(true);
		this.add(text);
		
		//Button to accept the name 
		enter.setVisible(true);
		enter.setLocation((SCREEN_WIDTH/2)+rectWidth, (SCREEN_HEIGHT/2)-diference);
		enter.setSize(rectWidth, rectHeight);
		this.add(enter);
		
		//Get the name and put the Start Game button
		//startGameButton.setVisible(false);
		this.add(startGameButton);
		enter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!text.getText().isEmpty()) {
					startGameButton.setVisible(true);
					startGameButton.setLocation((SCREEN_WIDTH/2)-diferenceWidth, SCREEN_HEIGHT-200);
					startGameButton.setSize(startGameWidth, startGameHeight);
					startGameButton.setBackground(Color.black);
					startGameButton.setForeground(new Color(247, 192, 91));
					player = text.getText();
				}
				else text.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			}
		});

		//Start Game
		startGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGameButton.setVisible(false);
				enter.setVisible(false);
				text.setVisible(false);
				startGame();
			}
		});
	}
	
	private void readScores() {
		try {
			FileReader file = new FileReader(directory);
			BufferedReader br = new BufferedReader(file);
			for(int i=0; i<5; i++) {
				String line = br.readLine();
				String array[] = line.split(", ");
				best.put(Integer.parseInt(array[0]), array[1]);
			}
		}
		catch(IOException ioe) {}
		
	}

	public void startGame() {
		this.setBackground(Color.black);
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		if(running) {
			for(int i=0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0,i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			//Draw Apple
			g.setColor(colorForApple);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i=0; i<bodyParts; i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			score(g);
			
		
		}
		else if(gameOver) gameOver(g);
		else initiate(g);
		
	}
	
	public void newApple() {
		//Color of Apple
		choseColorOfApple();
		
		//Location of Apple
		appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
		appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
	}
	
	private void choseColorOfApple() {
		Color[] colors = {Color.blue, Color.red, Color.green, Color.orange, Color.cyan, Color.darkGray, Color.magenta, Color.pink, Color.yellow};
		colorForApple = colors[random.nextInt(colors.length)];
	}

	public void move() {
		for(int i=bodyParts; i>0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U': y[0] = y[0] - UNIT_SIZE; break;
		case 'D': y[0] = y[0] + UNIT_SIZE; break;
		case 'R': x[0] = x[0] + UNIT_SIZE; break;
		case 'L': x[0] = x[0] - UNIT_SIZE; break;
		case 'W': x[0] = x[0]; break;
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX)&&(y[0] == appleY)) {
			bodyParts ++; 
			applesEaten ++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		if(direction != 'W') {
			//checks if head collides with body
			for(int i=bodyParts; i>0; i--) {
				if((x[0] == x[i])&&(y[0] == y[i])) {
					running = false;
					gameOver = true;
				}
			}
			//check if head touches left border
			if(x[0] < 0) { 
				direction = 'D';
				x[0] = 0;
				move();
			}
			//check if head touches right border
			if(x[0] > SCREEN_WIDTH) {
				direction='U';
				x[0] = SCREEN_WIDTH-UNIT_SIZE;
			}
			//check if head touches top border
			if(y[0] < 0) {
				direction = 'L';
				y[0] = 0;
			}
			//check if head touches bottom border
			if(y[0] > SCREEN_HEIGHT) {
				direction = 'R';
				y[0] = SCREEN_HEIGHT-UNIT_SIZE;
			}
			
			if(!running) {
				timer.stop();
				best.put(applesEaten, player);
			}
		}
	}
	
	public void gameOver(Graphics g) {
		//Score
		score(g);
	
		//Game Over text
		//,,,,,
		//,,,,,,, 
		
		/*
		 * Score: Papyrus, Stencil, Lucida Sans Unicode, ISOCTEUR, Ebrima, Consolas, RomanP, Calibri
		 * Game Over: Lucida Sans Unicode, ISOCTEUR, Perpetua, RomanP
		 * Best: Arial Rounded MT Bold, Ebrima, Consolas, RomanP, Arial, Calibri
		 */
		
		
		g.setFont(new Font("Lucida Sans Unicode", Font.BOLD, 75));
		g.setColor(new Color(122, 18, 2));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER"))/2+7, (SCREEN_HEIGHT/3));
		g.setColor(Color.red);
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER"))/2, (SCREEN_HEIGHT/3));
		
		//BestScore
		bestScore(g);
		
		//Restart Option
		restart.setVisible(true);
		restart.setLocation((SCREEN_WIDTH/2)-50, SCREEN_HEIGHT-100);
		restart.setSize(100, 30);
		
		this.add(restart);
		
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restart();
				startGame();
		
			}
		});
		
		//Store option
		store.setVisible(true);
		store.setLocation((SCREEN_WIDTH/6)*4, SCREEN_HEIGHT-100);
		store.setSize(100, 30);
		
		this.add(store);
		
		store.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Going to store. . . ");
				//TODO 1: Store the bestScore
				BufferedWriter bw;
				try {
					FileWriter file = new FileWriter(directory);
					bw = new BufferedWriter(file);
					for(int i=0; i<5; i++) {
						bw.write(bestScore[i]+", "+bestPlayers[i]);
						bw.newLine();
					}
					bw.close();
				}
				catch(IOException ioe) { System.out.println("SALTO");}
				System.out.println("Finalizao ");
			}
			
		});
		
		//Exit option
		exit.setVisible(true);
		exit.setLocation((SCREEN_WIDTH/6), SCREEN_HEIGHT-100);
		exit.setSize(100, 30);
		
		this.add(exit);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exit");
				//exit();
				//TODO 2: No funciona correctamente HELP!
				System.exit(0);
			}	
		});
	}
	
	private void bestScore(Graphics g) {
		count = 0;
		best.entrySet()
		  .stream()
		  .sorted(Map.Entry.<Integer, String>comparingByKey(Comparator.reverseOrder()))
		  .forEach(entry -> {
			  if(count == 5) return;
			  bestScore[count] = entry.getKey();
			  bestPlayers[count] = entry.getValue();
			  count++;
		  });
	
		
		//Best: Arial Rounded MT Bold, Ebrima, Consolas, RomanP, Arial, Calibri
		g.setColor(new Color(122, 18, 2));
		g.fillRect( (SCREEN_WIDTH/3)+15, (SCREEN_HEIGHT/2)-50+15, 200, 160);
		g.setColor(new Color(148, 144, 143));
		g.fillRect( (SCREEN_WIDTH/3), (SCREEN_HEIGHT/2)-50, 200, 160);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Calibri", Font.BOLD, 20));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Best Score", (SCREEN_WIDTH - metrics2.stringWidth("Best Score"))/2, (SCREEN_HEIGHT/2)-20);
		for(int i = 0; i<bestScore.length; i++) {
			if(bestScore[i]!=0)
				g.drawString((i+1)+". "+bestPlayers[i]+": "+bestScore[i], (SCREEN_WIDTH/3)+20, (SCREEN_HEIGHT/2)+(i*20)+10);
			
		}
	}
	
	public void isYourRecord() {
		int indexPlayer = -1;
		if(player == null) return;
		for(int i=0; i<bestPlayers.length; i++) {
			if(bestPlayers[i]==null) return;
			if(bestPlayers[i].equalsIgnoreCase(player)) {
				indexPlayer= i; break; }
		}
		if(indexPlayer != -1) {
			//Check if is they record
			if(applesEaten > bestScore[indexPlayer]) {
				record = true;
			}
		}
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running){
			move();
			checkApple();
			checkCollisions();
			isYourRecord();
		}
		repaint();
		
	}
	
	private void restart() {
		bodyParts = 6;
		applesEaten = 0;
		x[0] = 0;
		y[0] = 0;
		for(int i = 0; i<UNIT_SIZE;i++) { x[i] = 0; y[i] = 0; }
		direction = 'W'; 
		running = false;
		gameOver = false;
		record = false;
		timer.stop();
		restart.setVisible(false);
		store.setVisible(false);
		exit.setVisible(false);
	}
	
	private void score(Graphics g) {
		
		//Score: Papyrus, Lucida Sans Unicode, ISOCTEUR, Ebrima, Consolas, RomanP, Calibri
		g.setColor(Color.red);
		g.setFont(new Font("Consolas", Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') 
					direction = 'L';
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') 
					direction = 'R';
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D')
					direction = 'U';
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U')
					direction = 'D';
				break;
			}
		}
	}

}
