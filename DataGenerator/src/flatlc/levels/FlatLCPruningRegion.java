/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
