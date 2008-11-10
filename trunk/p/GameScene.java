package p;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import static p.Boat.BOAT_HEIGHT;
import static p.Boat.BOAT_WIDTH;
import static p.Boat.NET_SIZE;
import static p.Fish.FISH_HEIGHT;
import static p.Fish.FISH_WIDTH;
import static p.Bubble.BUBBLE_SIZE;

/**
 * The game scene.<br>
 * This is the view layer of Fishing in the MVC arhitecture. 
 *  
 * @author Andras Belicza
 */
class GameScene extends JComponent implements KeyListener {
	
	/** Width of the game scene in pixels.                                                  */
	public  static final int     SCENE_WIDTH              = 500; 
	/** Height of the game scene in pixels.                                                 */
	public  static final int     SCENE_HEIGHT             = 500;
	/** Color of the air at the left side of the scene (nice red => sunset color).          */
	private static final Color   LEFT_AIR_COLOR           = new Color( 187,   7,   7 );
	/** Color of the air at the right ide of the scene (nice orange => sunrise color).      */
	private static final Color   RIGHT_AIR_COLOR          = new Color( 251, 185,  53 );
	/** Color of the water part of the scene.                                               */
	private static final Color   WATER_COLOR              = new Color(   8,  74, 174 );
	/** Color of the bubbles.                                                               */
	private static final Color   BUBBLE_COLOR             = new Color( 160, 160, 255 );
	/** Color of the fishes.                                                                */
	private static final Color   FISH_COLOR               = new Color( 202, 201, 212 );
	/** Color of the boat (wihtout the net).                                                */
	private static final Color   BOAT_COLOR               = new Color( 115, 228, 117 );
	/** Color of the net.                                                                   */
	private static final Color   NET_COLOR                = new Color( 202, 201, 112 );
	/** Color of the texts.                                                                 */
	private static final Color   TEXT_COLOR               = new Color( 255, 255, 255 );
	/** Surface following degree of the boat (for example 0.7 means 70%: if surface 
	 * deviates 100 degrees from the horizontal, the boat will deviate 70 degrees).         */
	private static final double  SURFACE_FOLLOWING_DEGREE = 0.7;
	/** Degree of sinking of the boat to be in rest (for example 0.15 means 15%).           */
	private static final double  SINKING_DEGREE           = 0.15;
	/** Text for game paused message.                                                       */
	private static final String  GAME_PAUSED_TEXT         = "Game paused";
	/** Text for game over message.                                                         */
	private static final String  GAME_OVER_TEXT           = "Game over!";
	/** Text for resuming the game message.                                                 */
	private static final String  KEY_TO_CONTINUE_TEXT     = "Press the SPACE key to start";
	
	/** Polygon of the boat.                                                                */
	private static final Polygon BOAT_POLYGON;
	/** Polygon of the fish heading right.                                                  */
	private static final Polygon FISH_POLYGON_RIGHT;
	/** Polygon of the fish heading left.                                                   */
	private static final Polygon FISH_POLYGON_LEFT;

	/**
	 * Static initializator for creating the polygon of the boat and the fish.
	 */
	static {
		// The X and Y sizes of the boat in a 1x1 sized square.
		final double[] boatXs = new double[] { 0.0, 1.0/6.0, 5.0/6.0, 1.0, 4.0/6.0, 3.0/6.0, 2.0/6.0 };
		final double[] boatYs = new double[] { 0.5, 1.0    , 1.0    , 0.5, 0.5    , 0.0    , 0.5     };

		BOAT_POLYGON = new Polygon();
		for ( int i = 0; i < boatXs.length; i++ )  // All we have to do is to zoom the boat to its game size and to move to its center
			BOAT_POLYGON.addPoint( (int) ( boatXs[ i ] * BOAT_WIDTH ) - BOAT_WIDTH/2, (int) ( boatYs[ i ] * BOAT_HEIGHT ) - BOAT_HEIGHT/2 );

		// The X and Y sizes of the fish in a 1x1 sized  square.
		final double[] fishXs = new double[] { 1.0, 0.9 , 0.8, 0.55, 0.4, 0.25, 0.0, 0.0, 0.25, 0.4, 0.55, 0.75, 0.9, 1.0  };
		final double[] fishYs = new double[] { 0.3, 0.15, 0.0, 0.0 , 0.2, 0.35, 0.0, 1.0, 0.65, 0.8, 1.0 , 1.0 , 0.8, 0.65 };
		FISH_POLYGON_RIGHT = new Polygon();
		for ( int i = 0; i < fishXs.length; i++ )  // All we have to do is to zoom the fish to its game size and to move to its center
			FISH_POLYGON_RIGHT.addPoint( (int) ( fishXs[ i ] * FISH_WIDTH ) - FISH_WIDTH/2, (int) ( fishYs[ i ] * FISH_HEIGHT ) - FISH_HEIGHT/2 );

		// The polygon of fish heading left has the same coordinates multiplied by -1
		FISH_POLYGON_LEFT = new Polygon();
		for ( int i = 0; i < FISH_POLYGON_RIGHT.npoints; i++ )
			FISH_POLYGON_LEFT.addPoint( -FISH_POLYGON_RIGHT.xpoints[ i ], -FISH_POLYGON_RIGHT.ypoints[ i ] );
	}
	
	/**
	 * Enum constants for the control keys.
	 * @author Andras Belicza
	 */
	enum ControlKeys {
		/** Moving to the left.  */
		LEFT,
		/** Moving to the right. */
		RIGHT,
		/** Moving to shallow.   */
		UP,
		/** Moving to deeper.    */
		DOWN
	}
	
	
	
	/** States of the control keys.                             
	 *     false - released
	 *     true  - pressed                */
	private boolean[]       controlKeyStates = new boolean[ ControlKeys.values().length ];
	/** Reference to the game model.      */
	private final GameModel gameModel;
	/** Reference to the fishing control. */
	private final Fishing   fishing; 

	/**
	 * Creates a new GameScene.
	 * @param gameModel reference to the game model
	 * @param fishing   reference to the fishing control
	 */
	public GameScene( final GameModel gameModel, final Fishing fishing ) {
		this.gameModel = gameModel;
		this.fishing   = fishing;
		
		setPreferredSize( new Dimension( SCENE_WIDTH, SCENE_HEIGHT ) );
		addKeyListener( this );
	}

	/**
	 * Returns the array of states of control keys.<br>
	 * @return the array of states of control keys
	 */
	public boolean[] getControlKeyStates() {
		return controlKeyStates;
	}
	
	/**
	 * Paints the actual look of the component, the game scene.
	 * @param graphics the graphics context in which to paint
	 */
	public void paintComponent( final Graphics graphics ) {
		drawWaterAndAir( graphics );
		drawDecorations( graphics );
		drawFishes     ( graphics );
		drawBoat       ( graphics );
		drawTexts      ( graphics );
	}

	/**
	 * Draws the water and the air part of the scene.
	 * @param graphics the graphics context in which to paint 
	 */
	private void drawWaterAndAir( final Graphics graphics ) {
		final int MIN_WATER_LEVEL = WaterSurface.SEA_LEVEL + WaterSurface.MAX_SURFACE_AMPLITUDE;
		for ( int i = 0; i < SCENE_WIDTH; i++ ) {
			final int waterLevel = gameModel.waterSurface.getWaterLevelAt( i );
			
			// Nice gradient fill for the AIR from left to right
			// Note: using of java.awt.GradientPaint for this resulted in much higher cpu loading!!!
			final int MIN_RED     = LEFT_AIR_COLOR .getRed  ();
			final int MIN_GREEN   = LEFT_AIR_COLOR .getGreen();
			final int MIN_BLUE    = LEFT_AIR_COLOR .getBlue ();
			final int DELTA_RED   = RIGHT_AIR_COLOR.getRed  () - MIN_RED  ;
			final int DELTA_GREEN = RIGHT_AIR_COLOR.getGreen() - MIN_GREEN;
			final int DELTA_BLUE  = RIGHT_AIR_COLOR.getBlue () - MIN_BLUE ;
			
			graphics.setColor( new Color( MIN_RED + DELTA_RED*i/SCENE_WIDTH, MIN_GREEN + DELTA_GREEN*i/SCENE_WIDTH, MIN_BLUE + DELTA_BLUE*i/SCENE_WIDTH ) );
			graphics.drawLine( i, 0, i, waterLevel );
			graphics.setColor( WATER_COLOR );
			graphics.drawLine( i, waterLevel, i, MIN_WATER_LEVEL );
		}
		graphics.fillRect( 0, MIN_WATER_LEVEL+1, SCENE_WIDTH, SCENE_HEIGHT - MIN_WATER_LEVEL );  // The paint color is WATER_COLOR now
	}
	
	/**
	 * Draws the decorations of the game (bubbles only for now).
	 * @param graphics the graphics context in which to paint 
	 */
	private void drawDecorations( final Graphics graphics ) {
		graphics.setColor( BUBBLE_COLOR );

		// We cannot use enhanced for here, because painting runs in a different thread, and game controller can modify this during a repainting
		// Enhanced for uses iterator, ConcurrentModificationException would (may) be thrown
		for ( int i = 0; i < gameModel.bubbles.size(); i++ ) { 
			final Bubble bubble = gameModel.bubbles.get( i );
			graphics.drawOval( bubble.getX() - BUBBLE_SIZE/2, bubble.getY() - BUBBLE_SIZE/2, BUBBLE_SIZE, BUBBLE_SIZE );
		}
	}
	
	/**
	 * Draws the fishes.
	 * @param graphics the graphics context in which to paint 
	 */
	private void drawFishes( final Graphics graphics ) {
		graphics.setColor( FISH_COLOR );
		// We cannot use enhanced for here, because painting runs in a different thread, and game controller can modify this during a repainting
		// Enhanced for uses iterator, ConcurrentModificationException would (may) be thrown
		for ( int i = 0; i < gameModel.fishes.size(); i++ ) { 
			final Fish fish = gameModel.fishes.get( i );
			final Polygon fishPolygon = fish.headingRight() ? FISH_POLYGON_RIGHT : FISH_POLYGON_LEFT;

			fishPolygon.translate(  fish.getX(),  fish.getY() );
			graphics.fillPolygon (  fishPolygon );
			fishPolygon.translate( -fish.getX(), -fish.getY() );
		}
	}
	
	/**
	 * Draws the boat with the net which is part of the boat.
	 * @param graphics the graphics context in which to paint 
	 */
	private void drawBoat( final Graphics graphics ) {
		final Boat boat = gameModel.boat;

		// First we draw the net
		// The net consists of the rope which connects it to the boat,
		// and a circle with 3 horizontal and 3 vertical lines 
		final int CHORD_LENGTH = (int) ( NET_SIZE * 0.433 );   // This is the length of the half of the chord which is parallel with the diameter of the circle, and is at he half of the radius. 0.433=sqrt(3)/2/2
		
		final int netX = boat.getX();                          // center x coordinate of the net
		final int netY = boat.getBoatY() + boat.getY();        // center y coordinate of the net
		graphics.setColor( NET_COLOR );
		// The outline of the net
		graphics.drawOval( netX - NET_SIZE/2, netY - NET_SIZE/2, NET_SIZE, NET_SIZE );
		// The rope of the net
		graphics.drawLine( boat.getX()      , boat.getBoatY(), netX, netY - NET_SIZE );
		graphics.drawLine( netX - NET_SIZE/2, netY           , netX, netY - NET_SIZE );
		graphics.drawLine( netX + NET_SIZE/2, netY           , netX, netY - NET_SIZE );
		// The vertical lines of net
		graphics.drawLine( netX - NET_SIZE/4, netY - CHORD_LENGTH, netX - NET_SIZE/4, netY + CHORD_LENGTH );
		graphics.drawLine( netX             , netY - NET_SIZE/2  , netX             , netY + NET_SIZE/2   );
		graphics.drawLine( netX + NET_SIZE/4, netY - CHORD_LENGTH, netX + NET_SIZE/4, netY + CHORD_LENGTH );
		// The horizontal lines of net
		graphics.drawLine( netX - CHORD_LENGTH, netY - NET_SIZE/4, netX + CHORD_LENGTH     , netY - NET_SIZE/4 );
		graphics.drawLine( netX - NET_SIZE/2  , netY             , boat.getX() + NET_SIZE/2, netY              );
		graphics.drawLine( netX - CHORD_LENGTH, netY + NET_SIZE/4, netX + CHORD_LENGTH     , netY + NET_SIZE/4 );

		
		// ...and the boat
		final Graphics2D      graphics2D            = (Graphics2D) graphics;
		final AffineTransform storedAffineTransform = graphics2D.getTransform(); // We store the transform, because we draw the boat rotated.
		
		graphics2D.rotate( gameModel.waterSurface.getSurfaceAngleAt( boat.getX() ) * SURFACE_FOLLOWING_DEGREE, boat.getX(), boat.getBoatY() );
		graphics2D.translate( boat.getX(), boat.getBoatY() - (int) ( BOAT_HEIGHT * (0.5-SINKING_DEGREE) ) );
		graphics2D.setColor( BOAT_COLOR );
		graphics2D.fillPolygon( BOAT_POLYGON );
		graphics2D.setTransform( storedAffineTransform );

	}
	
	/**
	 * Draws the texts must be displayed on the scene.
	 * @param graphics the graphics context in which to paint 
	 */
	private void drawTexts( final Graphics graphics ) {
		graphics.setColor( TEXT_COLOR );
		
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		graphics.drawString( "Fishes caught: " + gameModel.fishesCaught, 5, fontMetrics.getHeight() );
		
		final String fishesMissedText = "Fishes missed: " + gameModel.fishesMissed;
		graphics.drawString( fishesMissedText, SCENE_WIDTH - 6 - fontMetrics.stringWidth( fishesMissedText ), fontMetrics.getHeight() );
		
		if ( fishing.isGamePaused() ) {
			// Putting 2 texts centering to the scene
			final String GAME_TEXT = fishing.isGameOver() ? GAME_OVER_TEXT : GAME_PAUSED_TEXT; 
			graphics.drawString( GAME_TEXT, SCENE_WIDTH/2 - fontMetrics.stringWidth( GAME_TEXT )/2, SCENE_HEIGHT/2 );
			graphics.drawString( KEY_TO_CONTINUE_TEXT, SCENE_WIDTH/2 - fontMetrics.stringWidth( KEY_TO_CONTINUE_TEXT )/2, SCENE_HEIGHT/2 + fontMetrics.getHeight() );
		}
	}
	
	
	/**
	 * Handles the key typed events.
	 * @param keyEvent details of the key event
	 */
	public void keyTyped( final KeyEvent keyEvent ) {
	}

	/**
	 * Handles the key pressed events.
	 * @param keyEvent details of the key event
	 */
	public void keyPressed( final KeyEvent keyEvent ) {
		switch ( keyEvent.getKeyCode() ) {
			case KeyEvent.VK_LEFT  : controlKeyStates[ ControlKeys.LEFT .ordinal() ] = true;  break;
			case KeyEvent.VK_DOWN  : controlKeyStates[ ControlKeys.DOWN .ordinal() ] = true;  break;
			case KeyEvent.VK_RIGHT : controlKeyStates[ ControlKeys.RIGHT.ordinal() ] = true;  break;
			case KeyEvent.VK_UP    : controlKeyStates[ ControlKeys.UP   .ordinal() ] = true;  break;
			case KeyEvent.VK_SPACE : fishing.invertGamePauseState();                          break;
			case KeyEvent.VK_F1    : fishing.showHelp();                                      break;
		}
	}

	/**
	 * Handles the key released events.
	 * @param keyEvent details of the key event
	 */
	public void keyReleased( final KeyEvent keyEvent ) {
		switch ( keyEvent.getKeyCode() ) {
			case KeyEvent.VK_LEFT  : controlKeyStates[ ControlKeys.LEFT .ordinal() ] = false; break;
			case KeyEvent.VK_DOWN  : controlKeyStates[ ControlKeys.DOWN .ordinal() ] = false; break;
			case KeyEvent.VK_RIGHT : controlKeyStates[ ControlKeys.RIGHT.ordinal() ] = false; break;
			case KeyEvent.VK_UP    : controlKeyStates[ ControlKeys.UP   .ordinal() ] = false; break;
		}
	}

}
