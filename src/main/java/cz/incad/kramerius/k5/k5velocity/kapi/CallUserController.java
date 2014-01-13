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
package cz.incad.kramerius.k5.k5velocity.kapi;

/**
 * Controller for
 * @author pavels
 *
 */
public interface CallUserController {
	
	public static final String KEY = "usersController";
	
	public void createCaller(String name, String pswd, Class<? extends User> user);
	
	public AdminUser getAdminCaller();
	
	public ClientUser getClientCaller();
	
	public ProfileDelegator getProfileDelegator();
}
