import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Timer;
import java.util.stream.Stream;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MyFrame extends JFrame implements ActionListener, MouseMotionListener{

	Timer timer;
	int secCounter=0;
	
	JMenuBar menu;
	JMenu file;
	JMenuItem load;
	JMenuItem help;
	JMenuItem exit;
	
	JLabel audioName[] = new JLabel[2];
	JSlider audioTime;
	JButton num[] = new JButton[6];
	
	AudioInputStream audio;
	Clip clip;
	JFileChooser filec;
	FileNameExtensionFilter filter;
	File f;
	long count;
	
	boolean isPlaying = false;
	boolean loop = false;
	boolean random = false;
	boolean left;
	
	String contents[];
	String shuffle[] = new String[1000];
	String Filename;
	int fileIndex;
	
	File directoryPath;
	
	int t;
	
	int r = 0;
	
	
	//PLAYER FUNCTION <-- using to play music and setting the text
	void callAudio(File f){
		try {
			audioName[0].setText(contents[fileIndex]);
			audio = AudioSystem.getAudioInputStream(f);
			clip = AudioSystem.getClip();
			clip.open(audio);
			t = (int) ((clip.getMicrosecondLength()/1000000)/60);
			audioTime.setMaximum((int) (clip.getMicrosecondLength()/1000000));
			audioName[1].setText(t+":"+((clip.getMicrosecondLength()/1000000)-(t*60)));
			if(isPlaying)
				clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {}
	}
	
	//SLIDER ADJUSTOR <-- using to change position of slider according to music
	void adjustSlider(int minTime, int maxTime) {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(secCounter>maxTime) {
					timer.cancel();
					if(loop) {
						timer.cancel();
						secCounter = 0;
						audioTime.setValue(0);
						adjustSlider(audioTime.getValue(), audioTime.getMaximum());
						callAudio(f);
					}
					else if(random) {
						Random rand = new Random();
						for(int i=0; i< contents.length; i++) {
							if(shuffle[i].contains(".wav"))
								left = true;
							}
						while(left && shuffle[r].equals(""))
							r = rand.nextInt(1, contents.length);
						if(left && shuffle[r].contains(".wav")) {
							left = false;
							funcRandom();
						}
						else {
							isPlaying=false;
							clip.stop();
							timer.cancel();
							num[1].setText("PLAY");
						}
					}
					else
						next();
				}
				secCounter++;
				audioTime.setValue(secCounter-1);
			}
			
		};
		timer.schedule(task, minTime, 1000);
	}
	
	void next() {
		timer.cancel();
		clip.close();
		for(int i=1; i<=count; i++) {
			if(!contents[fileIndex+i].equals("")) {
				fileIndex+=i;
				break;
			}
		}
		f =  new File(filec.getSelectedFile().getAbsolutePath().replace(filec.getSelectedFile().getName(), contents[fileIndex]));
		Filename = contents[fileIndex];
		secCounter = 0;
		audioTime.setValue(0);
		callAudio(f);
		if(isPlaying)
			adjustSlider(audioTime.getValue(), audioTime.getMaximum());
	}
	
	void funcRandom() {
		shuffle[fileIndex] = "";
		timer.cancel();
		clip.close();
		f =  new File(filec.getSelectedFile().getAbsolutePath().replace(filec.getSelectedFile().getName(), contents[r]));
		Filename = contents[r];
		fileIndex = r;
		shuffle[r] = "";
		r=0;
		secCounter = 0;
		audioTime.setValue(0);
		adjustSlider(audioTime.getValue(), audioTime.getMaximum());
		callAudio(f);
	}
	
	MyFrame(){
		
		//LOAD ITEM <-- using to select audio file
		load = new JMenuItem("Load");
		load.addActionListener(this);
		
		//HELP ITEM <-- using to display info
		help = new JMenuItem("Help");
		help.addActionListener(this);
		
		//EXIT ITEM <-- using to exit from program
		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		
		//FILE MENU <-- using to display upper items
		file = new JMenu("File");
		file.add(load);
		file.add(help);
		file.add(exit);
		
		//MENU <-- using to display menu bar in program
		menu = new JMenuBar();
		menu.setBackground(Color.WHITE);
		menu.setBorderPainted(false);
		menu.add(file);
		
		//AUDIO NAME <-- using to display music name (filename)
		for(int i=0; i<2; i++) {
			audioName[i] = new JLabel("MUSIC NAME");
			audioName[i].setOpaque(true);
			audioName[i].setBackground(Color.LIGHT_GRAY);
			audioName[i].setForeground(Color.BLACK);
			audioName[i].setFont(new Font("MV Boli", Font.PLAIN, 15));
			audioName[i].setVisible(true);
			this.add(audioName[i]);
		}
		
		//AUDIO TIME <-- using to display end-time of audio
		audioName[0].setBounds(15, 10, 255, 30);
		audioName[1].setText("0:0");
		audioName[1].setBounds(230, 55, 40, 30);
		
		//SLIDER <-- using to display and control the time of audio
		audioTime = new JSlider(0,0);
		audioTime.getUI();
		audioTime.addMouseMotionListener(this);
		audioTime.setBackground(Color.LIGHT_GRAY);
		audioTime.setBounds(15, 55, 205, 30);
		
		//CONTROL BUTTONS <-- using to control music
		String nam[] = {"<<", "PLAY", ">>", "LOOP: OFF", "SHUFFLE: OFF", "THEME: Light"};	
		for(int i=0; i<=5; i++) {
		num[i] = new JButton(nam[i]);
		num[i].setBackground(new Color(123,100,255));
		num[i].setFont(new Font("MV Boli", Font.PLAIN, 15));
		num[i].setFocusable(false);
		num[i].setVisible(true);
		num[i].addActionListener(this);
		this.add(num[i]);
		}
		
		//BUTTON POSISIONS
		num[0].setBounds(15, 100, 75, 25);
		num[1].setBounds(95, 100, 95, 25);
		num[2].setBounds(195, 100, 75, 25);
		num[3].setBounds(15, 130, 125, 25);
		num[3].setFont(new Font("MV Boli", Font.PLAIN, 10));
		num[4].setBounds(145, 130, 125, 25);
		num[4].setFont(new Font("MV Boli", Font.PLAIN, 10));
		num[5].setBounds(15, 160, 255, 25);
		
		//FRAME <-- will contain all other elements
		this.setJMenuBar(menu);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);
		this.setSize(new Dimension(300,420));
		this.getContentPane().setBackground(Color.WHITE);
		this.add(audioTime);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//LOAD FILE <-- defining audio selection logic
		if(e.getSource()==load) {
			filec = new JFileChooser();
			filter = new FileNameExtensionFilter("WAVE FILES","wav");
			filec.setFileFilter(filter);
			if(filec.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				try (Stream<Path> files = Files.list(Paths.get(filec.getSelectedFile().getAbsolutePath().replaceAll(filec.getSelectedFile().getName(), "")))) {
					Filename = filec.getSelectedFile().getName();
					count = files.count();
					directoryPath = new File(filec.getSelectedFile().getAbsolutePath().replaceAll(filec.getSelectedFile().getName(), ""));
					contents = directoryPath.list();
					for(int i=0; i<count; i++) {
						if(!contents[i].contains(".wav"))
							contents[i] = "";
						shuffle[i] = contents[i];
					}
					for(int j=0; j<contents.length; j++) {
						if(contents[j].equalsIgnoreCase(Filename)) {
							fileIndex = j;
							break;
			    	 }
			     }
			     } catch (IOException e2) {}
				f = new File(filec.getSelectedFile().getAbsolutePath());
				callAudio(f);
			}
		}
		
		//HELP BUTTON <-- defining exit function
		if(e.getSource()==help)
			 JOptionPane.showMessageDialog(null,  "This music player is solely created by AYUSH and you can contact him for any code related help.", "Help !", JOptionPane.INFORMATION_MESSAGE);
		
		//EXIT BUTTON <-- defining exit function
		if(e.getSource()==exit)
			System.exit(0);
		
		//PREVIOUS BUTTON <-- defining previous function
		if(e.getSource()==num[0] && fileIndex>1) {
			clip.close();
			for(int i=1; i<=count; i++) {
				if(!(contents[fileIndex-i].equals("")) && fileIndex>0) {
					fileIndex-=i;
					break;
				}
			}
			f =  new File(filec.getSelectedFile().getAbsolutePath().replace(filec.getSelectedFile().getName(), contents[fileIndex]));
			Filename = contents[fileIndex];
			secCounter = 0;
			audioTime.setValue(0);
			callAudio(f);
		}
		
		//PLAY/PAUSE BUTTON <-- defining play and pause function
		if(e.getSource()==num[1]) {
			if(num[1].getText().equals("PLAY")) {
				isPlaying=true;
				clip.start();
				num[1].setText("PAUSE");
				adjustSlider(audioTime.getValue(), audioTime.getMaximum());
			}
			else {
				isPlaying=false;
				clip.stop();
				timer.cancel();
				num[1].setText("PLAY");
			}
		}
		
		//NEXT BUTTON <-- defining next function
		if(e.getSource()==num[2] && fileIndex<contents.length-1)
			next();
		
		//LOOP BUTTON
		if(e.getSource()==num[3]) {
			if(num[3].getText().equals("LOOP: OFF")) {
				num[3].setText("LOOP: ON");
				loop = true;
				num[4].setText("SHUFFLE: OFF");
				random = false;
			}
			else {
				num[3].setText("LOOP: OFF");
				loop = false;
			}
		}
		
		//RANDOM BUTTON
		if(e.getSource()==num[4]) {
			if(num[4].getText().equals("SHUFFLE: OFF")) {
				num[4].setText("SHUFFLE: ON");
				random = true;
				num[3].setText("LOOP: OFF");
				loop = false;
			}
			else {
				num[4].setText("SHUFFLE: OFF");
				random = false;
			}
		}
		
		//THEME BUTTON <-- changing theme of gui using this button
		if(e.getSource()==num[5]) {
			if(this.getContentPane().getBackground().equals(Color.WHITE)) {
				this.getContentPane().setBackground(Color.BLACK);
				menu.setBackground(Color.BLACK);
				file.setForeground(Color.WHITE);
				num[5].setText("THEME: Dark");
			}
			else {
				this.getContentPane().setBackground(Color.WHITE);
				menu.setBackground(Color.WHITE);
				file.setForeground(Color.BLACK);
				num[5].setText("THEME: Light");
			}
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(!audioName[1].getText().equalsIgnoreCase("0:0")) {
			secCounter = audioTime.getValue();
			String msTime = audioTime.getValue() + "000000";
			clip.setMicrosecondPosition(Integer.parseInt(msTime));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
