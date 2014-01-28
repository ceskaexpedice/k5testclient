/*
 * Copyright (C) 2013 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.kramerius.k5.k5velocity.socialauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;









import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import cz.incad.kramerius.k5.k5velocity.kapi.AdminUser;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.ClientUser;
import cz.incad.kramerius.k5.k5velocity.kapi.ProfileDelegator;
import cz.incad.kramerius.k5.k5velocity.kapi.impl.CallUserControllerImpl;

/**
 * OpenID support -> login via fb or gplus
 * @author pavels
 */
public class OpenIDSupport {
	
	//TODO: from configuration
	public static final String ADMIN_NAME = "krameriusAdmin";
	public static final String ADMIN_PSWD = "krameriusAdmin";
	
	public Profile getProfile(HttpSession session, HttpServletRequest request, HttpServletResponse resp)
			throws Exception {
		// get the social auth manager from session
		SocialAuthManager manager = (SocialAuthManager) session
				.getAttribute("authManager");

		// call connect method of manager which returns the provider object.
		// Pass request parameter map while calling connect method.
		AuthProvider provider = manager.connect(SocialAuthUtil
				.getRequestParametersMap(request));
		
		// get profile
		Profile p = provider.getUserProfile();
		return p;
	}

	public void providerLogin(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// Create an instance of SocialAuthConfgi object
		SocialAuthConfig config = SocialAuthConfig.getDefault();

		InputStream in = OpenIDSupport.class.getClassLoader()
				.getResourceAsStream("oauth_consumer.properties");

		
		// load configuration. By default load the configuration from
		// oauth_consumer.properties.
		// You can also pass input stream, properties object or properties file
		// name.
		config.load(in);

		// Create an instance of SocialAuthManager and set config
		SocialAuthManager manager = new SocialAuthManager();
		manager.setSocialAuthConfig(config);

		// URL of YOUR application which will be called after authentication
		String successUrl = "http://localhost:8080/k5velocity-1.0-SNAPSHOT/gpluslogged.jsp";

		// get Provider URL to which you should redirect for authentication.
		// id can have values "facebook", "twitter", "yahoo" etc. or the OpenID
		// URL
		String url = manager.getAuthenticationUrl("googleplus", successUrl);
		
		// Store in session
		session.setAttribute("authManager", manager);
		resp.sendRedirect(url);
	}

	private static String calculateUserName(Profile p) {
		return p.getProviderId()+"_"+p.getValidatedId();
	}

	// create user
    public static String createUser(Profile profile) throws JSONException {
    	Client c = Client.create();

        WebResource r = c.resource("http://localhost:8080/search/api/v5.0/admin/users");
        r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));
        JSONObject object = new JSONObject();

        object.put("lname", calculateUserName(profile));
        
        object.put("firstname", profile.getFirstName());
        object.put("surname", profile.getLastName());
        object.put("password",calculateUserName(profile));
        
        String t = r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(object.toString(), MediaType.APPLICATION_JSON).post(String.class);
        return t;
    }

	// user exists ?
    public static boolean userExists(Profile p) throws JSONException {
    	Client c = Client.create();
    	
        WebResource r = c.resource("http://localhost:8080/search/api/v5.0/admin/users?lname="+calculateUserName(p));
        r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));

        String t = r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(String.class);
        JSONArray jsonArr = new JSONArray(t);
        return jsonArr.length() > 0;
    }
    
	public void provideRedirection(HttpServletRequest req, HttpServletResponse resp) throws Exception  {

		Profile profile = getProfile(req.getSession(), req, resp);
		
		if (!userExists(profile)) {
			createUser(profile);
		}
		
		CallUserController controller = new CallUserControllerImpl();
		// load from props 
		controller.createCaller("krameriusAdmin", "krameriusAdmin", AdminUser.class);
		controller.createCaller(calculateUserName(profile), calculateUserName(profile), ClientUser.class);
		controller.createCaller(calculateUserName(profile), "invalid password", ProfileDelegator.class);
		
		req.getSession(true).setAttribute(CallUserController.KEY, controller);
		
		resp.sendRedirect("index.vm");
	}
	
	
	public void login(HttpServletRequest request, HttpServletResponse resp) {
		try {
			HttpSession session = request.getSession(true);
			providerLogin(session, request, resp);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
