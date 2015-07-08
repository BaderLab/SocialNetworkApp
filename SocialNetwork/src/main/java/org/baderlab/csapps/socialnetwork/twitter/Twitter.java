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

package org.baderlab.csapps.socialnetwork.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;

/**
 * Methods and fields for manipulating Twitter data
 *
 * @author Victor Kofia
 */
public class Twitter {

    public static void main(String[] args) throws Exception {

        Twitter http = new Twitter();
        System.out.println("Testing 1 - Send a request");
        http.sendRequest("followers/ids", "GET");

    }

    private Map<String, String> oauthMap = null;
    final private String BASE_URL = "https://api.twitter.com/1.1/";

    final private String USER_AGENT = "Mozilla/5.0";

    private String BuildOAuthHeader() {
        String header = "OAuth ";
        for (Entry<String, String> entry : this.getOAuthMap().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                header += URLEncoder.encode(key, "UTF-8") + "\\\"" + URLEncoder.encode(value, "UTF-8") + "\\\",";
            } catch (UnsupportedEncodingException e) {
                // ??
            }
        }
        // Remove last comma from string (cosmetic purposes)
        return header.substring(0, header.length() - 1);
    }

    private void constructOAuthMap(String oauthNonce, String oauthSignature, String oauthTimeStamp, String oauthToken) {
        this.oauthMap = new HashMap<String, String>();
        this.oauthMap.put("oauth_consumer_key", "YMrFw7eXnL2GiNusZ4Pj3Q");
        this.oauthMap.put("oauth_nonce", oauthNonce);
        this.oauthMap.put("oauth_signature", oauthSignature);
        this.oauthMap.put("oauth_signature_method", "HMAC-SHA1");
        this.oauthMap.put("oauth_timestamp", oauthTimeStamp);
        this.oauthMap.put("oauth_token", oauthToken);
        this.oauthMap.put("oauth_version", "1.0");
    }

    private String createOAuthNonce() {
        SecureRandom random = new SecureRandom();
        String oAuthNonce = new BigInteger(130, random).toString(32);
        return oAuthNonce;
    }

    private String createOAuthSignature() {
        String oAuthSignature = "";
        return oAuthSignature;
    }

    private String createOAuthTimeStamp() {
        long oauthTimeStamp = System.currentTimeMillis() / 1000L;
        return Long.toString(oauthTimeStamp);
    }

    private HttpURLConnection createRequest(String resourceURL, String httpMethod) {

        try {
            URL obj = new URL(this.BASE_URL + resourceURL + ".json");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(httpMethod);
            con.setRequestProperty("User-Agent", this.USER_AGENT);
            con.setRequestProperty("X-HostCommonName", "api.twitter.com");
            con.setRequestProperty("Authorization", this.BuildOAuthHeader());
            con.setRequestProperty("Host", "api.twitter.com");
            con.setRequestProperty("X-Target-URI", "https://api.twitter.com");
            con.setRequestProperty("Connection", "Keep Alive");
            return con;
        } catch (MalformedURLException e) {
            CytoscapeUtilities.notifyUser("Twitter! DERP!!");
        } catch (IOException e) {
            CytoscapeUtilities.notifyUser("Twitter! DERP!!");
        }
        return null;

    }

    private Map<String, String> getOAuthMap() {
        if (this.oauthMap == null) {
            this.constructOAuthMap(this.createOAuthNonce(), this.createOAuthSignature(), this.createOAuthTimeStamp(), this.getOAuthToken());
        }
        return this.oauthMap;
    }

    private String getOAuthToken() {
        String oAuthToken = "";
        return oAuthToken;
    }

    private void sendRequest(String resourceURL, String httpMethod) throws Exception {

        HttpURLConnection con = this.createRequest(resourceURL, httpMethod);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(response.toString());

    }

    private void setOAuthMap(Map<String, String> oauthMap) {
        this.oauthMap = oauthMap;
    }

}
