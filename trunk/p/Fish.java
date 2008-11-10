package p;

/**
 * Class representing a fish.
 * 
 * @author Andras Belicza
 */
class Fish extends MovingObject {

	/** Width of a fish.                                  */
	public static final int FISH_WIDTH  = 26;
	/** Height of a fish.                                 */
	public static final int FISH_HEIGHT = 10;
	
	/**
	 * Creates a new Fish.
	 * @param x  the initial x coordinate
	 * @param y  the initial y coordinate
	 * @param vx horizontal component of the velocity of the fish
	 * @param vy vertical component of the velocity of the fish
	 */
	public Fish( final double x, final double y, final double vx, final double vy ) {
		super( x, y, vx, vy );
	}
	
	/**
	 * Tells whether this fish heading right (from left to right on the game scene).
	 * @return true if this fish heading right; false otherwise (if heading to left)
	 */
	public boolean headingRight() {
		return vx > 0.0;
	}
	
}
