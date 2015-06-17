package flatlc.levels;



/**
 * This class models a PruningRegion necessary for <code>Semi-Pareto</code>. A
 * pruning region consists of two elements, each a pruning level for the left
 * and the right part of the Semi-Pareto preference.
 * 
 * @author endresma
 * 
 */
public class FlatLCPruningRegion extends FlatLCPartialLevelCombinationA {

	public FlatLCPruningRegion(double leftPruningLevel, double rightPruningLevel) {
		super(leftPruningLevel, rightPruningLevel);	
	}

	
	

}
