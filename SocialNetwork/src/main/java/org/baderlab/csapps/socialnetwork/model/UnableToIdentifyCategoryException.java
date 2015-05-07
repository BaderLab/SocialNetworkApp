package org.baderlab.csapps.socialnetwork.model;

/**
 * This exception is thrown when a category can't be identified
 *
 * @author Victor Kofia
 */
public class UnableToIdentifyCategoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String categoryName;
	
	public UnableToIdentifyCategoryException(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		// TODO Auto-generated method stub
		return super.getStackTrace();
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return this.categoryName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
