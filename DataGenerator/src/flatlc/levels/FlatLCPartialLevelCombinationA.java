package flatlc.levels;


/**
 * This class models partial levels necessary for <code>Semi-Pareto</code>. A
 * partial level consists of two elements, each a  level for the left
 * and the right part of the Semi-Pareto preference.
 * 
 * @author endresma
 * 
 */

public class FlatLCPartialLevelCombinationA {

    	protected double leftLevel;

    	protected double rightLevel;

    	public FlatLCPartialLevelCombinationA(double leftLevel, double rightLevel) {
    		this.leftLevel = leftLevel;
    		this.rightLevel = rightLevel;
    	}

    	public double getLeftLevel() {
    		return leftLevel;
    	}

    	public double getRightLevel() {
    		return rightLevel;
    	}

    	public void setLeftLevel(double newLeftLevel) {
    		leftLevel = newLeftLevel;
    	}

    	public void setRightLevel(double newRightLevel) {
    		rightLevel = newRightLevel;
    	}

    	public void setPartialLevels(double newLeftLevel,
    			double newRightLevel) {
    		setLeftLevel(newLeftLevel);
    		setRightLevel(newRightLevel);
    	}

    	public String toString() {
    		return "(" + leftLevel + ", " + rightLevel + ")";
    	}
    	
    	/**
    	 * returns if this pruning region dominates the given pruning region pr.
    	 * 
    	 * @param partialLevels
    	 * @return true or false
    	 */
    	public boolean dominates(FlatLCPartialLevels partialLevels) {
    		if (partialLevels.getLeftLevel() > this.leftLevel
    				&& partialLevels.getRightLevel() > this.rightLevel)
    			return true;

    		return false;
    	}
    	
    	/**
    	 * returns if this pruning level combination has equal or higher levels than plc.
    	 * 
    	 * @param pr
    	 * @return true or false
    	 */
    	public boolean hasWorsePartialLevels(FlatLCPartialLevelCombinationA plc) {
    		if (plc.getLeftLevel() < this.leftLevel
    				&& plc.getRightLevel() < this.rightLevel)
    			return true;

    		return false;
    	}
    }
