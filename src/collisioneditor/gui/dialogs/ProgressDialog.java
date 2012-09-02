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

package collisioneditor.gui.dialogs;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

//TODO : Improve this, and make it determinate
public class ProgressDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5929504616801024727L;
	
	private JProgressBar bar;

	public ProgressDialog(Frame owner, String title)
	{
		this(owner);
		setTitle(title);
	}
	
	public ProgressDialog(Frame owner) {
		super(owner, false);
		bar = new JProgressBar();
		bar.setIndeterminate(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(owner);
		setSize(200, 50);
//		setUndecorated(true);
		add(bar);
//		pack();
	}
	
	public void start(){
		setVisible(true);
	}
	
	public void stop(){
		setVisible(false);
	}

}
