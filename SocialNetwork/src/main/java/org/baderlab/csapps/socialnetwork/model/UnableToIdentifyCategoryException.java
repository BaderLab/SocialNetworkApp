/**
 **                       SocialNetwork Cytoscape App
 **
 ** Copyright (c) 2013-2015 Bader Lab, Donnelly Centre for Cellular and Biomolecular
 ** Research, University of Toronto
 **
 ** Contact: http://www.baderlab.org
 **
 ** Code written by: Victor Kofia, Ruth Isserlin
 ** Authors: Victor Kofia, Ruth Isserlin, Gary D. Bader
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** University of Toronto
 ** has no obligations to provide maintenance, support, updates,
 ** enhancements or modifications.  In no event shall the
 ** University of Toronto
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** University of Toronto
 ** has been advised of the possibility of such damage.
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 **/

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

    /**
     * Create a new exception for the category with the specified name
     * 
     * @param String categoryName
     */
    public UnableToIdentifyCategoryException(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        // TODO Auto-generated method stub
        return this.categoryName;
    }

    @Override
    /* (non-Javadoc)
     * @see java.lang.Throwable#getStackTrace()
     */
    public StackTraceElement[] getStackTrace() {
        // TODO Auto-generated method stub
        return super.getStackTrace();
    }

    @Override
    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

}
