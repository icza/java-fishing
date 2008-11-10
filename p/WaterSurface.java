package p;

/**
 * This class simulates the water surface which is basically a sin function.<br>
 * The surface function is:<br>
 *  Amplitude*sin(phi+omega*x)
 * 
 * @author Andras Belicza
 */
class WaterSurface {

	/** The sea level (without waves).          */
	public  static final int    SEA_LEVEL             = 100;
	/** Maximum value of surface amplitude.     */
	public  static final int    MAX_SURFACE_AMPLITUDE =  44;
	/** Maximum value of the drifting velocity. */
	private static final double MAX_DRIFTING_VELOCITY = 3.5;
	/** Omega of the sin wave of the surface.   */
	private static final double SURFACE_OMEGA         = 0.033;
	

	/** Water surface is a sin function. This is its phase.     */
	public double surfacePhase;
	/** Water surface is a sin function. This is its amplitude. */
	public double surfaceAmplitude;

	/**
	 * WaterSurface creates a new WaterSurface.
	 */
	public WaterSurface() {
		surfacePhase     = 0.0;
		surfaceAmplitude = 2.0;
	}
	
	/**
	 * Returns the water level at a specified position.
	 * @param x position where we want to know the water level
	 * @return the water level at the specified posision
	 */
	public int getWaterLevelAt( final int x ) {
		return SEA_LEVEL + (int) ( surfaceAmplitude * Math.sin( surfacePhase + SURFACE_OMEGA * x ) );
	}
	
	/**
	 * Returns the drifting velocity of the water.
	 * @return the drifting velocity of the water.
	 */
	public double getDriftingVelocity() {
		// Negative because waves go from right to left
		return -Math.min( surfaceAmplitude / 10.0, MAX_DRIFTING_VELOCITY );
	}
	
	/**
	 * Returns the angle of the surface at a specified position.<br>
	 * The angle of surface is calculated by the derivated surface function.
	 * We know that the cos function gives the derivation of the sin. We gives this to an archus tangent function.
	 * @param x position where we want to know the angle of surface
	 * @return angle of the surface at the specified position
	 */
	public double getSurfaceAngleAt( final int x ) {
		return Math.atan( SURFACE_OMEGA * surfaceAmplitude * Math.cos( surfacePhase + SURFACE_OMEGA * x ) );
	}

}
