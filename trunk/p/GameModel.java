package p;

import java.util.Vector;

/**
 * This is the game model. Holds the datas for a Fishing game.
 * 
 * @author Andras Belicza
 */
class GameModel {

	/** The water surface of the 'sea'.                     */
	public WaterSurface     waterSurface;
	/** Boat of the game.                                   */
	public Boat             boat;
	/** Vector of the fishes.                               */
	public Vector< Fish   > fishes;
	/** Vector of the bubbles.                              */
	public Vector< Bubble > bubbles;
	/** Number of fishes the player caught.                 */ 
	public int              fishesCaught;
	/** Number of fishes the player caught.                 */ 
	public int              fishesMissed;
	/** Iteration counter, tells which iteration are we in. */
	public int              iterationCounter;
	
	/**
	 * Creates a new GameModel.
	 * Simply calls the init() method.
	 */
	public GameModel() {
		init();
	}
	
	/**
	 * Inits the game model, so a new game can begin.
	 */
	public void init() {
		fishes           = new Vector< Fish   >();
		bubbles          = new Vector< Bubble >();
		waterSurface     = new WaterSurface();
		boat             = new Boat( waterSurface );
		fishesCaught     = 0;
		fishesMissed     = 0;
		iterationCounter = 0;
	}
	
}
