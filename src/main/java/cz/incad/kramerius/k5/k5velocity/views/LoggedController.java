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
package cz.incad.kramerius.k5.k5velocity.views;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.generic.ValueParser;

import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;

/**
 * Controls whether current session is authenticated
 * @author pavels
 */
public class LoggedController {

	protected HttpServletRequest req;

	public void configure( Map props) {
    	req = (HttpServletRequest) props.get("request");
    	//req = (HttpServletRequest) props.get("request");
    }
	
	/**
	 * Returns true if the current session is authenticated
	 * @return
	 */
	public boolean isLogged() {
		return req.getSession() != null && req.getSession().getAttribute(CallUserController.KEY) != null;
	}
	
}

