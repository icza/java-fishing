package p;

import static p.GameScene.SCENE_HEIGHT;
import static p.GameScene.SCENE_WIDTH;
import static p.WaterSurface.SEA_LEVEL;
import p.GameScene.ControlKeys;

/**
 * This is the boat with the net what we can move to catch fishes.<br>
 * Stores a reference to the water surface for asking the drifting velocity
 * and to determine the y coordinate of the boat (it depends (equals to) the water level
 * at the position of the boat).<br>
 * The boat is a MovingObject with some restrictions:
 * The boat can move only horizontally, the water level determines its y coordinate.
 * So the y attribute of the MovingObject will mean the y coordinate of the net of the boat
 * in LOCAL COORDINATION SYSTEM, which means y will be the length of the netrope.
 * And a new makeMove() method will be implemented to handle the special movements of the boat.
 * 
 * @author Andras Belicza
 */
class Boat extends MovingObject {

	/** Value of vertical speed of the boat.  */
	private static final double VX          = 5.0;
	/** Value of horizontal speed of the net. */
	private static final double VY          = 9.0;
	/** The width of the boat.                */
	public  static final int    BOAT_WIDTH  = 80; 
	/** The height of the boat.               */
	public  static final int    BOAT_HEIGHT = 40; 
	/** The size of the net.                  */
	public  static final int    NET_SIZE    = 36; 
	
	/** Reference to the water surface.                   */
	private final WaterSurface waterSurface;
	
	/**
	 * Creates a new boat.
	 * @param waterSurface reference to the water surface
	 */
	public Boat( final WaterSurface waterSurface ) {
		super( SCENE_WIDTH / 2.0, ( NET_SIZE + BOAT_HEIGHT ) / 2, VX, VY );
		this.waterSurface = waterSurface;
	}

	/**
	 * Returns the y coordiante of the boat.
	 * This value is not stored, it is the water level at the x position of the boat.
	 * @return the y coordinate of the boat.
	 */
	public int getBoatY() {
		return waterSurface.getWaterLevelAt( getX() );
	}
	
	/**
	 * Makes a move of the boat and/or the net.
	 * @param controlKeyStates states of the control keys
	 *
	 */
	public void makeMove( final boolean[] controlKeyStates ) {
		x += waterSurface.getDriftingVelocity();
		

		// Now we handle the moves neccessary for the control keys
		if ( controlKeyStates[ ControlKeys.LEFT.ordinal() ] )
		   x -= vx;
		
		if ( controlKeyStates[ ControlKeys.RIGHT.ordinal() ] )
			x += vx;
		
		if ( controlKeyStates[ ControlKeys.UP.ordinal() ] )
			y -= vy;
		
		if ( controlKeyStates[ ControlKeys.DOWN.ordinal() ] )
			y += vy;

	
		// We check the positions whether they are outside the valid domains
		if ( x < 0.0 )
			x = 0.0;
		if ( x > SCENE_WIDTH - 1.0 )
			x = SCENE_WIDTH - 1.0;

		if ( y < ( NET_SIZE + BOAT_HEIGHT ) / 2 )
			y = ( NET_SIZE + BOAT_HEIGHT ) / 2;
		if ( y > SCENE_HEIGHT - SEA_LEVEL - 1.0 )
			y = SCENE_HEIGHT - SEA_LEVEL - 1.0;
	}
	
}
