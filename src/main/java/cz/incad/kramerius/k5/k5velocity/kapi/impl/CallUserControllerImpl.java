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
package cz.incad.kramerius.k5.k5velocity.kapi.impl;

import cz.incad.kramerius.k5.k5velocity.kapi.AdminUser;
import cz.incad.kramerius.k5.k5velocity.kapi.CallUserController;
import cz.incad.kramerius.k5.k5velocity.kapi.ClientUser;
import cz.incad.kramerius.k5.k5velocity.kapi.ProfileDelegator;
import cz.incad.kramerius.k5.k5velocity.kapi.User;

public class CallUserControllerImpl implements CallUserController {

	
	private AdminUser aUser;
	private ClientUser cUser;
	private ProfileDelegator pDelegator;
	
	
	@Override
	public void createCaller(String name, String pswd,Class<? extends User> clz) {
		if (clz.equals(AdminUser.class)) {
			this.aUser = new AdminUserImpl(name, pswd);
		} else if (clz.equals(ClientUser.class)) {
			this.cUser = new ClientUserImpl(name, pswd);
		} else if (clz.equals(ProfileDelegator.class)) {
			this.pDelegator = new ProfileDelegatorImpl(name, pswd);
		} else throw new IllegalArgumentException("cannot create caller for instance '"+clz+"'");
	}

	@Override
	public AdminUser getAdminCaller() {
		return this.aUser;
	}

	@Override
	public ClientUser getClientCaller() {
		return this.cUser;
	}

	@Override
	public ProfileDelegator getProfileDelegator() {
		return this.pDelegator;
	}

	
}
