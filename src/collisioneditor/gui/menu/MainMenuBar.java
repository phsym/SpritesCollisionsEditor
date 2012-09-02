/*
* SpritesCollisionsEditor
* Copyright (C) 2012 Pierre-Henri Symoneaux
* 
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 US
*/

package collisioneditor.gui.menu;

import javax.swing.JMenuBar;


public class MainMenuBar extends JMenuBar {
	
	private FileMenu fileMenu;
	private EditMenu editMenu;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2579719951946461716L;
	
	public MainMenuBar() {
		fileMenu = new FileMenu();
		add(fileMenu);
		
		editMenu = new EditMenu();
		add(editMenu);
	}
	
}
