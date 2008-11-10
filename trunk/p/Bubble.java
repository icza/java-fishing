package p;

import static p.GameScene.SCENE_HEIGHT;

/**
 * Class representing a bubble in the water.<br>
 * The bubbles are simply light circles.
 * 
 * @author Andras Belicza
 */
class Bubble extends MovingObject {

	/** Size of the bubbles (diameter).                        */
	public  static final int    BUBBLE_SIZE                  = 5;
	/** Ascending velocity of the bubbles.                     */
	private static final double BUBBLE_ASCENDING_VELOCITY    = -1.6;
	/** Amplitude of the horizontal oscillation of the bubble. */
	private static final double BUBBLE_OSCILLATION_AMPLITUDE =  5.0;
	
	/** The phase of the horizontal oscillation of the bubble. */
	private double oscillationPhase = 0.0;
	
	/**
	 * Creates a new Bubble.
	 * @param x the x coordinate of the bubble
	 */
	public Bubble( final int x ) {
		super( x, SCENE_HEIGHT, 0.0, BUBBLE_ASCENDING_VELOCITY );
	}
	
	/**
	 * Returns the x coordiante of the bubble as an integer.<br>
	 * Overrides MovingObject.getX() because we want a little horizontal oscillation. 
	 * @return x coordiante of the bubble as an integer
	 */
	public int getX() {
		return (int) ( x + BUBBLE_OSCILLATION_AMPLITUDE* Math.sin( oscillationPhase ) );
	}
	
	/**
	 * Makes this bubble to step one.<br>
	 * Overrides MovingObject.makeStep() because we have other things to do,
	 * we have to step the oscillation phase too.
	 * Stepping means increasing position with the velocity.
	 */
	public void makeStep() {
		super.makeStep();
		oscillationPhase += 0.15;
	}
	
	
}
