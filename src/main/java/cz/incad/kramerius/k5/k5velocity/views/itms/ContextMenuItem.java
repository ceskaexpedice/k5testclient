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
package cz.incad.kramerius.k5.k5velocity.views.itms;

public class ContextMenuItem {
	
	private String href;
	private String name;
	private boolean multiple;
	
	public ContextMenuItem(String href, String name) {
		super();
		this.href = href;
		this.name = name;
                this.multiple = true;
	}
	
	public ContextMenuItem(String href, String name, boolean multiple) {
		super();
		this.href = href;
		this.name = name;
                this.multiple = multiple;
	}
	
	public boolean getMultiple() {
		return multiple;
	}
	
	public String getHref() {
		return href;
	}
	
	
	public String getName() {
		return name;
	}
}
