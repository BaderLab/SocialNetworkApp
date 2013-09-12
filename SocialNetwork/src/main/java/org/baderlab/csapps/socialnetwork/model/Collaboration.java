package org.baderlab.csapps.socialnetwork.model;


/**
 * Two nodes that share an interaction
 * (or interactions)
 * @author Victor Kofia
 */
public class Collaboration {
	/**
	 * A node (~ node1)
	 */
	private AbstractNode node1 = null;
	/**
	 * A node (~ node2)
	 */
	private AbstractNode node2 = null;

	/**
	 * Create a new Collaboration object composed of node1 and node2
	 * @param AbstractNode node1
	 * @param AbstractNode node2
	 */
	public Collaboration(AbstractNode node1, AbstractNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	/**
	 * Get node1. Returns null if no node is present.
	 * @param null
	 * @return AbstractNode author1
	 */
	public AbstractNode getNode1() {
		return this.node1;
	}
	
	/**
	 * Get node2. Returns null if no node
	 * is present.
	 * @param null
	 * @return AbstractNode author2
	 */
	public AbstractNode getNode2() {
		return this.node2;
	}

	/**
	 * Return Collaboration hash code. Used to determine a collaboration's identity. 
	 * Current system considers collaborations holding the exact same nodes but in 
	 * a different ordering (i.e. Victor stored in node2 instead of node1) to be one
	 * and the same.
	 * @param null
	 * @return int hashCode
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node2 == null) ? 0 : node1.hashCode() + node2.hashCode());
		return result;
	}

	/**
	 * Return true iff collaboration is equal to other.
	 * @param Object Collaboration
	 * @return boolean 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Collaboration other = (Collaboration) obj;
		if (node1 == null) {
			if (other.node1 != null)
				return false;
		// Collaborations are considered equal regardless of order (i.e. node1 and node2
		// are interchangeable).
		} else if (! (node1.equals(other.node1) | node1.equals(other.node2)) )
			return false;
		if (node2 == null) {
			if (other.node2 != null)
				return false;
		} else if (! (node2.equals(other.node1) | node2.equals(other.node2)) )
			return false;
		return true;
	}
	
	/**
	 * Return string representation of collaboration in the format:
	 * <br> Node#1: <i>node</i>
	 * <br> Node#2: <i>node</i>
	 * @param null
	 * @return String collaboration
	 */
	public String toString() {
		return "Node#1: " + node1.toString() +"\nNode#2: " + node2.toString() + "\n\n";
	}
}
