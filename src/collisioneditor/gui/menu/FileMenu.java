/*
* CollisionsEditor
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import collisioneditor.CollisionsEditor;
import collisioneditor.EditCanvas;
import collisioneditor.utils.Settings;

public class FileMenu extends JMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4876382892122848397L;
	private JMenu recentItem;

	public FileMenu() {
		super("Files");
		setMnemonic(KeyEvent.VK_F);
		JMenuItem openItem = new JMenuItem("Open");
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		try {
			openItem.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/folder_image.png"))));
		} catch (IOException e1) {
		}
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						CollisionsEditor.getInstance().load();
						recentItem.removeAll();
						ArrayList<String> recents = Settings.getInstance().getRecentlyUsed();
						for(final String r : recents)
						{
							JMenuItem item = new JMenuItem(r);
							item.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									CollisionsEditor.getInstance().load(new File(r));
								}
							});
							recentItem.add(item);
						}
					}
				});
			}
		});
		add(openItem);
		
		JMenuItem reloadItem = new JMenuItem("Reload");
		reloadItem.setMnemonic(KeyEvent.VK_R);
		reloadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		try {
			reloadItem.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/arrow_refresh.png"))));
		} catch (IOException e1) {
		}
		reloadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						EditCanvas c = CollisionsEditor.getInstance().getSelectedCanvas();
						if(c!= null)
							c.loadRectangles();
					}
				});
			}
		});
		add(reloadItem);
		
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		try {
			saveItem.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/disk.png"))));
		} catch (IOException e1) {
		}
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollisionsEditor.getInstance().save(false);
			}
		});
		add(saveItem);
		
		JMenuItem saveAllItem = new JMenuItem("Save all");
		saveAllItem.setMnemonic(KeyEvent.VK_A);
		try {
			saveAllItem.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/disk_multiple.png"))));
		} catch (IOException e1) {
		}
		saveAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollisionsEditor.getInstance().save(true);
			}
		});
		add(saveAllItem);
		
		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.setMnemonic(KeyEvent.VK_C);
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollisionsEditor.getInstance().close();
				
			}
		});
		add(closeItem);
		
		addSeparator();
		
		recentItem = new JMenu("Recently used");
		recentItem.setMnemonic(KeyEvent.VK_R);
		
		ArrayList<String> recents = Settings.getInstance().getRecentlyUsed();
		for(final String r : recents)
		{
			JMenuItem item = new JMenuItem(r);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					CollisionsEditor.getInstance().load(new File(r));
				}
			});
			recentItem.add(item);
		}
		add(recentItem);
		
		addSeparator();
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollisionsEditor.getInstance().exit();
			}
		});
		add(exitItem);
		
		
	}
}
