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

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;

import cz.incad.kramerius.k5.k5velocity.kapi.AdminUser;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.ClientUser;
import cz.incad.kramerius.k5.k5velocity.kapi.ProfileDelegator;
import cz.incad.kramerius.k5.k5velocity.kapi.impl.CallUserControllerImpl;

public class OpenIDSupport {

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

	private String calculateUserName(Profile p) {
		String ident = p.getProviderId()+"_"+p.getEmail();
		return ident;
	}
	
	
	
	public void provideRedirection(HttpServletRequest req, HttpServletResponse resp, Profile profile)  {
		CallUserController controller = new CallUserControllerImpl();
		// load from props 
		controller.createCaller("krameriusAdmin", "krameriusAdmin", AdminUser.class);
		controller.createCaller("krameriusAdmin", "krameriusAdmin", ClientUser.class);
		controller.createCaller(calculateUserName(profile), "invalid password", ProfileDelegator.class);
		
		req.getSession(true).setAttribute(CallUserController.KEY, controller);
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
