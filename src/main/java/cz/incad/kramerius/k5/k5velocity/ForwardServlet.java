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
package cz.incad.kramerius.k5.k5velocity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.brickred.socialauth.Profile;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;

import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.User;
import cz.incad.kramerius.k5.k5velocity.socialauth.BasicAuthenticationFilter;

public class ForwardServlet extends HttpServlet {

	
	public static final Logger LOGGER = Logger.getLogger(ForwardServlet.class.getName());
	
	public static enum TypeOfCall {
		ADMIN {
			@Override
			public User getUser(CallUserController cus) {
				if (cus != null) {
					return cus.getAdminCaller();
				} else return null;
			}
		}, 
		USER {
			@Override
			public User getUser(CallUserController cus) {
				if(cus != null){
					return cus.getClientCaller();
                }else{
                    return null;
                }
			}
		};

		public abstract User getUser(CallUserController cus);
	}
	

    public static String method(String url,SupportedMethods method, JSONObject jsonObject, String userName, String pswd) throws JSONException {
    	Client c = Client.create();

    	c.getProperties().put(
    	        ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

        WebResource r = c.resource(url);
        if (userName != null && pswd != null) {
            r.addFilter(new BasicAuthenticationFilter("krameriusAdmin", "krameriusAdmin"));
        }
    	Builder builder = r.accept(MediaType.APPLICATION_JSON);
        //String t = r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(object.toString(), MediaType.APPLICATION_JSON).post(String.class);

    	if (jsonObject != null) {
    		builder = builder.type(MediaType.APPLICATION_JSON).entity(jsonObject.toString(), MediaType.APPLICATION_JSON);
    	}
    	
    	switch(method) {
    		case GET:
    			return builder.get(String.class);
    		case PUT:
    			return builder.put(String.class);
    		case DELETE:
				return builder.delete(String.class);
    		case POST:
				return builder.post(String.class);
    		default:
    			throw new IllegalStateException("usupported type of method");
    	}
    }

	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			TypeOfCall tc = disectTypeOfCall(req);
			User user = tc.getUser((CallUserController) req.getSession().getAttribute(CallUserController.KEY));

			String k5addr = getInitParameter("k5prefix");
			
			String queryString = req.getQueryString();
			String requestedURI = req.getRequestURI();
			int index = requestedURI.indexOf("api")+"api".length();
			String rest = requestedURI.substring(index);
			
			String replaceURL =k5addr+rest;
			if (StringUtils.isAnyString(queryString)) {
				replaceURL = replaceURL+"?"+queryString;
			}
			LOGGER.info("requesting url "+replaceURL);
			
			JSONObject jsonObj = null;
			if (req.getContentLength() > 0) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ServletInputStream iStream = req.getInputStream();
				IOUtils.copyStreams(iStream, bos);
				
				String t = new String(bos.toByteArray(), Charset.forName("UTF-8"));
				
				if ( (!t.startsWith("{"))  && (!t.endsWith("}")) ) {
					// hack because of jquery
					
					jsonObj = new JSONObject("{"+t+"}");
					
				} else {
					jsonObj = new JSONObject(t);
				}
			}
			
			String  str = null;
			if (user != null) {
				str = method(replaceURL,SupportedMethods.POST,jsonObj, user.getUserName(), user.getPassword());
			} else {
				str = method(replaceURL,SupportedMethods.POST,jsonObj, null,null);
			}
			
			resp.setContentType("application/json; charset=utf-8");
			resp.getWriter().write(str);
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE,e.getMessage(),e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		TypeOfCall tc = disectTypeOfCall(req);
		User user = tc.getUser((CallUserController) req.getSession().getAttribute(CallUserController.KEY));

		String k5addr = getInitParameter("k5prefix");
		
		String queryString = req.getQueryString();
		String requestedURI = req.getRequestURI();
		int index = requestedURI.indexOf("api")+"api".length();
		String rest = requestedURI.substring(index);
		
		String replaceURL =k5addr+rest;
		if (StringUtils.isAnyString(queryString)) {
			replaceURL = replaceURL+"?"+queryString;
		}
		LOGGER.info("requesting url "+replaceURL);
                
		InputStream inputStream = null;
		if (user != null) {
			inputStream = RESTHelper.inputStream(replaceURL, user.getUserName(), user.getPassword());
		} else {
			inputStream = RESTHelper.inputStream(replaceURL);
		}
		resp.setContentType("application/json; charset=utf-8");
		IOUtils.copyStreams(inputStream, resp.getOutputStream());

	}


	private TypeOfCall disectTypeOfCall(HttpServletRequest req) {
		String requestedURI = req.getRequestURI();
		return requestedURI.contains("admin") ? TypeOfCall.ADMIN : TypeOfCall.USER;
	}
	
	private static enum SupportedMethods {
		
		PUT, GET, POST, DELETE;
	}
}
