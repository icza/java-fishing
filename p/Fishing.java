package p;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import static p.Boat.NET_SIZE;
import static p.Fish.FISH_HEIGHT;
import static p.Fish.FISH_WIDTH;
import static p.GameScene.SCENE_HEIGHT;
import static p.GameScene.SCENE_WIDTH;
import static p.WaterSurface.MAX_SURFACE_AMPLITUDE;
import static p.WaterSurface.SEA_LEVEL;
import static p.Bubble.BUBBLE_SIZE;

/**
 * Main class of Fishing.<br>
 * <br>
 * This program has been created for the event of the Java technology turns 10!.<br>
 * <br>
 * This is the controller layer of Fishing in the MVC architecture.
 * 
 * @author Andras Belicza
 */
public class Fishing {

	/** Approximatelly number of iterations per sec. (Note: same as fps) */
	private static final int ITERATIONS_PER_SEC  = 20;
	/** Max allowed  missed fishes. Reaching this is the end of game.    */
	private static final int MAX_FISHES_MISSED   = 10;
	
	
	/** The game model.                                          */
	private final GameModel  gameModel  = new GameModel();
	/** The game scene.                                          */
	private final GameScene  gameScene  = new GameScene( gameModel, this );
	/** Reference to the main frame (method showHelp() uses it). */
	private final JFrame     mainFrame;
	/** Tells whether game is paused.                            */
	private volatile boolean gamePaused = true;

	
	
	/**
	 * The entry point of the program.
	 * Creates the main frame of the game and makes it visible. 
	 * @param arguments used to take arguments from the running environment - not used here
	 */
	public static void main( final String[] arguments ) {

		// We create and initialize a frame for the application
		final JFrame mainFrame = new JFrame( "Fishing" );
		mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		mainFrame.setResizable( false );
		mainFrame.setLocation( 100, 100 );
		
		
		// We create the fishing object and add the game scene to the main frame
		final Fishing fishing = new Fishing( mainFrame );
		mainFrame.getContentPane().add( fishing.gameScene );
		mainFrame.pack();
		mainFrame.setVisible( true );
		
		// This is where key inputs go, focus must be owned
		fishing.gameScene.requestFocusInWindow();
		
		// We show some infos and helps about the program
		fishing.showHelp();
		
		// All set, we can play now
		fishing.controlGame();
	}

	/**
	 * Creates a new Fishing
	 * @param mainFrame reference to the main frame
	 */
	private Fishing( final JFrame mainFrame ) {
		this.mainFrame = mainFrame;
	}
	
	/**
	 * Shows help informations about the application.
	 */
	public void showHelp() {
		JOptionPane.showMessageDialog( mainFrame, new Object[] { 
				new JLabel( "Fishing", JLabel.CENTER ),
				new JSeparator(),
				new JLabel( "A 10 kB Java application created for 'Java Technology Turns 10!'", JLabel.CENTER ),
				new JSeparator(),
				new JLabel( "Your goal is to catch the fishes.", JLabel.CENTER ),
				new JLabel( "Game ends when you miss " + MAX_FISHES_MISSED + " fishes.", JLabel.CENTER ),
				new JSeparator(),
				"Control keys:",
				" F1 - this help",
				" Space - pause/resume",
				" left/right - move the boat to left/right",
				" up/down - move your net to up/down",
				new JSeparator(),
				new JLabel( "Created by Andr\u00e1s Belicza", JLabel.CENTER ),
				new JLabel( "2005 Hungary", JLabel.CENTER ),
				new JSeparator(),
			}, "Fishing help", JOptionPane.INFORMATION_MESSAGE );
		
		gameScene.requestFocusInWindow();  // In linux, game scene can lose the focus after the dialog window (or rather not gain back)
	}
	
	/**
	 * Controls the game, manages the game iterations.
	 */
	private void controlGame() {
		try {
			while ( true ) {
				
				while ( gamePaused )
					Thread.sleep( 1l );

				if ( isGameOver() )                              // If game ended (game over), we start a new game
					gameModel.init();
				
				while ( !gamePaused ) {
					nextIteration();
					gameScene.repaint();
					Thread.sleep( 1000l / ITERATIONS_PER_SEC ); // This causes the approximation: painting and calculating takes some time, but this works perfectly for us, no need for better timing
					if ( isGameOver()  )
						gamePaused = true;
				}
				gameScene.repaint();                            // We want the 'Game paused' or 'Game over' text displayed...
			}
		}
		catch ( final InterruptedException ie ) {
		}
	}

	/**
	 * Calculates the next iteration of the game.
	 */
	private void nextIteration() {
		// Cloning the array of states of control keys will ensure that
		// all the process of next iteration will see and use the same key states
		final boolean[] controlKeyStates = gameScene.getControlKeyStates().clone();
		
		gameModel.boat.makeMove( controlKeyStates );
		
		// Increment size of surface phase
		gameModel.waterSurface.surfacePhase += 0.12;
		
		// Increment size of surface amplitude.
		// Increased in every iteration until it reaches its limit.
		if ( gameModel.waterSurface.surfaceAmplitude < MAX_SURFACE_AMPLITUDE )
			gameModel.waterSurface.surfaceAmplitude += 0.008;
		
		// Now we check and step the fishes
		final int netX = gameModel.boat.getX();
		final int netY = gameModel.boat.getBoatY() + gameModel.boat.getY();
		final Vector< Fish > deadFishes = new Vector< Fish >();
		for ( final Fish fish : gameModel.fishes ) {
			fish.makeStep();
			
			// Is the fish being caught?
			// Fish is caught, if the center point of the fish is inside the net (which is a circle)
			//                 or if "it would swim into our net"
			if ( square( fish.getX()                                                   - netX ) + square( fish.getY() - netY ) < NET_SIZE/2*NET_SIZE/2 || 
			     square( fish.getX() + (fish.headingRight()?+FISH_HEIGHT:-FISH_HEIGHT) - netX ) + square( fish.getY() - netY ) < NET_SIZE/2*NET_SIZE/2 ) { 
				deadFishes.add( fish );
				gameModel.fishesCaught++;
			}
			
			// Did the fish just leave the scene? 
			else if ( fish.getX() < -FISH_WIDTH/2 || fish.getX() > SCENE_WIDTH - 1 + FISH_WIDTH/2 ) {
				deadFishes.add( fish );
				gameModel.fishesMissed++;
			}
		}
		if ( !deadFishes.isEmpty() )
			gameModel.fishes.removeAll( deadFishes );

		// Now we check and step the bubbles
		final Vector< Bubble > deadBubbles = new Vector< Bubble >();
		for ( final Bubble bubble : gameModel.bubbles ) {
			bubble.makeStep();
			// Did the bubble come out of the water?
			if ( bubble.getY() - BUBBLE_SIZE/2 < gameModel.waterSurface.getWaterLevelAt( bubble.getX() ) )
				deadBubbles.add( bubble );
		}		
		if ( !deadBubbles.isEmpty() )
			gameModel.bubbles.removeAll( deadBubbles );
		
		// We may "launch" a new fish. As the time goes, probability of launching fish goes higher.
		if ( Math.random() < Math.min( 0.075, 0.025 + gameModel.iterationCounter/20000.0 ) )
			gameModel.fishes.add( generateNewFish() );

		// A new bubble may appear in the water
		if ( Math.random() < 0.03 )
			gameModel.bubbles.add( new Bubble( (int) ( Math.random() * SCENE_WIDTH ) ) );
		
		gameModel.iterationCounter++;
	}
	
	/**
	 * Calculates and returns the square of an integer.
	 * @param x number whose square must be returned
	 * @return the square of the specified number
	 */
	private int square( final int x ) {
		return x * x;
	}
	
	/**
	 * Generates and returns a new Fish.
	 * @return a new fish
	 */
	private Fish generateNewFish() {
		// By FREE I mean the fish can swim there, for example, the fish cannot swim in the air or in the waves.
		final double  FREE_WATER_RANGE = SCENE_HEIGHT - SEA_LEVEL - MAX_SURFACE_AMPLITUDE - FISH_HEIGHT;
		final double  MIN_FREE_LEVEL   = SEA_LEVEL + MAX_SURFACE_AMPLITUDE;

		final boolean comingFromLeft = Math.random() < 0.5;  // 50% chance for coming from left, 50% for right
		final double  startXPos = comingFromLeft ? -FISH_WIDTH/2 : SCENE_WIDTH - 1 + FISH_WIDTH/2;
		final double  startYPos = MIN_FREE_LEVEL + Math.random() * FREE_WATER_RANGE;
		// We generate an endYPos for determining vy. We want the new fish to head to this point
		final double  endYPos   = MIN_FREE_LEVEL + Math.random() * FREE_WATER_RANGE;

		// For vx: 2.2 at the beginning, and maximum value increases 1/20 in every seconds
		// And if it comes from right, it must be negative
		final double  vx        = ( comingFromLeft ? 1 :-1 ) * ( 2.2 + Math.random() * ( gameModel.iterationCounter / ITERATIONS_PER_SEC / 20 ) );
		// v=s/t where s=endYPos-startYPos and t=SCENE_WIDTH/vx.     vy must be this, if we want the fish to head toward endYPos
		final double  vy        = ( endYPos - startYPos ) / ( (SCENE_WIDTH+FISH_WIDTH) / Math.abs( vx ) );
		
		// We now have all parameter for a new fish
		return new Fish( startXPos, startYPos, vx, vy );
	}
	
	/**
	 * Tells whether game is paused.
	 * @return true if game is paused; false otherwise
	 */
	public boolean isGamePaused() {
		return gamePaused;
	}
	
	/**
	 * Inverts the game pause state.
	 * Implemented as inverting the gamePaused attribute.
	 */
	public void invertGamePauseState() {
		gamePaused = !gamePaused;
	}
	
	/**
	 * Tells whether game is over.<br>
	 * Game is over when the player misses MAX_FISHES_MISSED or more fishes. 
	 * @return true if game is over; false otherwise
	 */
	public boolean isGameOver() {
		return gameModel.fishesMissed >= MAX_FISHES_MISSED;
	}
	
}
