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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import collisioneditor.CollisionsEditor;
import collisioneditor.EditCanvas;
import collisioneditor.utils.Settings;

public class EditMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4320096380562610537L;

	public EditMenu(){
		super("Edit");
		setMnemonic(KeyEvent.VK_E);
		
		final JCheckBoxMenuItem hideRectItem = new JCheckBoxMenuItem("Edit collisions",Settings.getInstance().getBoolean(Settings.EDIT_COLLISIONS, false));
		hideRectItem.setMnemonic(KeyEvent.VK_C);
		hideRectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
		hideRectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.getInstance().set(Settings.EDIT_COLLISIONS, hideRectItem.isSelected());
			}
		});
		add(hideRectItem);
		
		addSeparator();
		
		JMenuItem detectItem = new JMenuItem("Detect collisions");
		detectItem.setMnemonic(KeyEvent.VK_D);
		detectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D , InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
		detectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						EditCanvas c = CollisionsEditor.getInstance().getSelectedCanvas();
						if(c!= null)
							c.autoDetectCollisions();
					}
				}).start();
			}
		});
		add(detectItem);
		
		JMenuItem mergeItem = new JMenuItem("Merge collisions");
		mergeItem.setMnemonic(KeyEvent.VK_M);
		mergeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
		mergeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						EditCanvas c = CollisionsEditor.getInstance().getSelectedCanvas();
						if(c!= null)
							c.autoMergeRectangles(); //TODO : Select mode (horizontal, vertical, ...) & precision
					}
				}).start();
			}
		});
		add(mergeItem);
		
		JMenuItem clearItem = new JMenuItem("Clear collisions");
		clearItem.setMnemonic(KeyEvent.VK_L);
		clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L , InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
		clearItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditCanvas c = CollisionsEditor.getInstance().getSelectedCanvas();
				if(c!= null)
					c.clearRectangles();
			}
		});
		add(clearItem);
	}
}
