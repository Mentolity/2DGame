package game;
import gfx.Colors;
import gfx.Fonts;
import gfx.Screen;
import gfx.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

// extends the canvas to the class and implements runnables
// not exactly sure what this means but its required to use JFrames I think
public class Game extends Canvas implements Runnable{
	//serial don't know why but its needed
	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160; //width of the game
	public static final int HEIGHT = WIDTH/12*9; //height of the game
	public static final int SCALE = 3; //allows you to scale the game
	public static final String NAME = "Game"; //name of the game

	private JFrame frame;	//may want to change some of the variables latter which is why tutorial put it in here
							//a JFrame is basically like a canvas; "A writable area we can put stuff on
							//Used for application devolopment *like eclipse*
	
	public boolean running = false;
	public int tickCount = 0;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); //Creates a new buffered image object
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();//Represents how many pixels are inside the image
	public int[] colors = new int[6*6*6]; //array of possible colors
	
	private Screen screen;
	public InputHandler input;
	

	//constructor
	public Game(){
		//sets the canvas to within these bounds
		setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		
		frame = new JFrame(NAME); //creates the JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //when the games down running just get rid of it
		frame.setLayout(new BorderLayout()); //makes sure the sizing is okay by changing the way it sets up the JFrame by centering it
		frame.add(this, BorderLayout.CENTER);	
		frame.pack(); //pack it so that it sets the frame so that its at or above the correct size keeps everything sized correctly	
		frame.setResizable(false); //not resizable	
		frame.setLocationRelativeTo(null); //not relative to anything just centered	
		frame.setVisible(true); //makes the frame visable
	}
	
	//adds a new screen
	public void init(){
		int index = 0;
		for(int r=0; r<6; r++){
			for(int g=0; g<6; g++){
				for(int b=0; b<6; b++){
					int rr = (r*255/5);
					int gg = (g*255/5);
					int bb = (b*255/5);
					
					colors[index++] = rr<<16 | gg<<8 |bb;; //something about setting the colors to an array that we can reference
				}
			}
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/spritesheet.png"));
		input = new InputHandler(this);
	}	
	
	
	//not sure what synchronized does something about applets
	//start method
	private synchronized void start() {
		//don't need to know about threading but it is an instance of runnable which is why we "implements Runnable"
		//whenever this thread is started its going to run the run function
		running = true;
		new Thread(this).start();
	}
	
	//stop method
	private synchronized void stop(){
		running = false;
	}
	
	public void run() {
		//main game loop is in this function
		
		long lastTime = System.nanoTime(); //gets the time when it is called	
		double nsPerTick = 1000000000D/60D; //how many nanoSecounds per Tick
		int frames = 0; //how many frames have occured
		int ticks = 0; //how many ticks have occured
		long lastTimer = System.currentTimeMillis(); //gets the current system time
		double delta = 0; //how many nano-secounds have gone by so far;	
		
		init();
		
		while(running){
			long now = System.nanoTime(); //current time
			delta+=(now - lastTime)/nsPerTick; //how much time has past between each tick
			lastTime = now; //updates lastTime each tick
			boolean shouldRender = true; //should render is to limit the frames that can be render per tick
			//runs for one tick
			while(delta>=1){
				ticks++;
				tick();
				delta--;
				shouldRender = true; //render every time a tick passes
			}
			
			//if not limiting through should render it keeps the thread from overloading
			try{
				Thread.sleep(10);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			if (shouldRender){
				frames++;
				render();
			}
			//if the current time minus the last time is greater than 1000 update
			//this is so that it updates onces per tick instead of as fast as my computer can handle
			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer+=1000;
				System.out.println(frames + " frames, " + ticks + " ticks");
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	//tick is going to update the game *the logic
	public void tick(){
		tickCount++;
		
		if(input.up.isPressed()){
			screen.yOffset--;
		}
		if(input.down.isPressed()){
			screen.yOffset++;
		}
		if(input.left.isPressed()){
			screen.xOffset--;
		}
		if(input.right.isPressed()){
			screen.xOffset++;
		}
	}
	
	//will render the game
	public void render(){
		BufferStrategy bs = getBufferStrategy(); //An object that allows us to organize the data on the canvas
		if(bs == null){
			createBufferStrategy(3); 	//if its not already buffering buffer it
										//number of times the images are buffered 
			return;
		}	


		for(int y=0; y<32;y++){
			for(int x=0; x<32;x++){
				screen.render(x<<3, y<<3, 0, Colors.get(555, 505, 055, 550), false, false);
			}
		}
		
		String msg = "This is my game!";
        Fonts.render(msg, screen,
                        screen.xOffset + screen.width / 2 - (msg.length() * 8 / 2),
                        screen.yOffset + screen.height / 2,
                        Colors.get(-1, -1, -1, 000));
		
		
		for(int y=0; y<screen.height;y++){
			for(int x=0; x<screen.width;x++){
				int colorCode = screen.pixels[x+y * screen.width];
				if(colorCode < 255) pixels[x + y * WIDTH] = colors[colorCode];
			}
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); //gets whatever the content of image is and puts it on the screen
		
		g.dispose();//frees up memory that the graphics object isn't using
		bs.show(); // shows the content of the buffer
	}
	
	public static void main(String[] args){
		new Game().start();
	}
}
