package p;

/**
 * Represents an object which is positioned in plane and can move in both x and y direction.<br>
 * Position is determined by x and y coordiantes.<br>
 * The coordinates and the components of velocity are in double precision.<br>
 * The position is visible in int precision, in all cases it is enough, and where we use the position
 * outside of the class needed as int too!<br>
 * 
 * @author Andras Belicza
 */
class MovingObject {

	/** x coordinate of the object.                                   */
	protected double       x;
	/** y coordinate of the object.                                   */
	protected double       y;
	/** Horizontal component of the velocity of the object.
	 * Note: in our all cases velocity does not change, can be final! */
	protected final double vx;
	/** Vertical component of the velocity of the object.
	 * Note: in our all cases velocity does not change, can be final! */
	protected final double vy;

	/**
	 * Creates a new MovingObject.
	 * Initializes the attributes of the class
	 * @param x  the initial value of the x coordinate
	 * @param y  the initial value of the x coordinate
	 * @param vx the vetrical component of the velocity
	 * @param vy the vetrical component of the velocity
	 */
	public MovingObject( final double x, final double y, final double vx, final double vy ) {
		this.x  = x;
		this.y  = y;
		this.vx = vx;
		this.vy = vy;
	}
	
	/**
	 * Returns the x as an integer.<br>
	 * @return x as an integer
	 */
	public int getX() {
		return (int) x;
	}
	
	/**
	 * Returns the y as an integer.<br>
	 * @return y as an integer
	 */
	public int getY() {
		return (int) y;
	}
	
	/**
	 * Makes this moving object to step one.
	 * Stepping means increasing position with the velocity.
	 */
	public void makeStep() {
		x += vx;
		y += vy;
	}
	
}
