package main.java.org.baderlab.csapps.socialnetwork;


/**
 * Two nodes
 * @author Victor Kofia
 */
public class Consortium {
	/**
	 * A node (designated node1 to distinguish from node2)
	 */
	private AbstractNode node1 = null;
	/**
	 * A node (designated node2 to distinguish from node1)
	 */
	private AbstractNode node2 = null;

	/**
	 * Create a new consortium composed of node1 and node2
	 * @param AbstractNode node1
	 * @param AbstractNode node2
	 */
	public Consortium(AbstractNode node1, AbstractNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	/**
	 * Get node1
	 * @param null
	 * @return AbstractNode author1
	 */
	public AbstractNode getNode1() {
		return this.node1;
	}
	
	/**
	 * Get node2
	 * @param null
	 * @return AbstractNode author2
	 */
	public AbstractNode getNode2() {
		return this.node2;
	}

	/**
	 * Return Consortium hash code. Used to determine a consortium's identity. 
	 * Current system considers consortiums composed of the same nodes
	 * but stored in different variables (i.e. Victor stored in node2 instead
	 * of node1) as being one and the same
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
	 * Return true iff consortium is equal (in all the important aspects)
	 * to other
	 * @param Object Consortium
	 * @return boolean 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Consortium other = (Consortium) obj;
		if (node1 == null) {
			if (other.node1 != null)
				return false;
		// Consortiums are considered equal regardless of order (i.e. node1 and node2
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
}
