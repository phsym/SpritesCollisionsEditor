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

package collisioneditor;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import collisioneditor.gui.menu.MainMenuBar;
import collisioneditor.utils.Settings;

/**
 * 
 * @author Pierre-Henri Symoneaux
 *
 */
public class CollisionsEditor extends JFrame implements WindowListener, ComponentListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String VERSION = "0.1";
	
	private JTabbedPane pane;
	
	private MainMenuBar menuBar;
	
	private static CollisionsEditor _instance = null;
	
	public static CollisionsEditor getInstance(){
		if(_instance == null)
			_instance = new CollisionsEditor("CollisionsEditor v" +VERSION);
		return _instance;
	}

	private CollisionsEditor(String name) throws HeadlessException {
		super(name);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		addComponentListener(this);
		
		menuBar = new MainMenuBar();
		setJMenuBar(menuBar);
		pane = new JTabbedPane();
		add(pane);
		
		String w = Settings.getInstance().get(Settings.FRAME_WIDTH, "800");
		String h = Settings.getInstance().get(Settings.FRAME_HEIGHT, "600");
		setSize(Integer.parseInt(w), Integer.parseInt(h));
		
		String x = Settings.getInstance().get(Settings.FRAME_X, "0");
		String y = Settings.getInstance().get(Settings.FRAME_Y, "0");
		setLocation(Integer.parseInt(x), Integer.parseInt(y));
		
		try {
			setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("icons/MainIcon.png")));
		} catch (IOException e) {
		}
	}
	
	public void load(File file)
	{
		if(file.getAbsolutePath().endsWith(".col"))
			file = new File(file.getAbsolutePath().replaceAll("\\.col$", ""));
		if(!file.exists())
			return;
		Settings.getInstance().set(Settings.LAST_LOADED_PATH, file.getParent());
		
		EditCanvas canvas = new EditCanvas(file);
		pane.addTab(file.getName(), canvas);
		new Thread(canvas).start();
		pane.setSelectedIndex(pane.getComponentCount()-1);
		Settings.getInstance().addRecentlyUsed(file.getAbsolutePath());
	}
	
	public void load(){
		JFileChooser fc = new JFileChooser();
		
		String path = Settings.getInstance().get(Settings.LAST_LOADED_PATH , "");
		if(path != null && !path.isEmpty())
			fc.setCurrentDirectory(new File(path));
		fc.setMultiSelectionEnabled(false);
		
		int rc = fc.showOpenDialog(this);
		
		if(rc == JFileChooser.APPROVE_OPTION)
			load(fc.getSelectedFile());
	}
	
	public void save(boolean all){
		if(!all)
		{
			EditCanvas canvas = getSelectedCanvas();
			if(canvas != null)
				canvas.saveRectangles();
		}
		else
		{
			for(int i = 0; i < pane.getTabCount(); i++)
				((EditCanvas) pane.getComponentAt(i)).saveRectangles();
		}
	}
	
	public void close(){
		EditCanvas canvas = getSelectedCanvas();
		if(canvas != null)
		{
			canvas.stop();
			pane.remove(canvas);
		}
	}
	
	public EditCanvas getSelectedCanvas() {
		return (EditCanvas) pane.getSelectedComponent();
	}
	
	public void exit(){
		Settings.getInstance().saveSettings();
		while(pane.getTabCount() > 0)
		{
			EditCanvas canvas = getSelectedCanvas();
			if(canvas != null)
			{
				canvas.stop();
				pane.remove(canvas);
			}
		}
		setVisible(false);
		dispose();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		exit();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Settings.getInstance().set(Settings.FRAME_WIDTH, ""+getWidth());
		Settings.getInstance().set(Settings.FRAME_HEIGHT, ""+getHeight());
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
		Settings.getInstance().set(Settings.FRAME_X, ""+getX());
		Settings.getInstance().set(Settings.FRAME_Y, ""+getY());
	}
	
	public static void main(String[] args) {
		
		// Get the native look and feel class name
		String nativeLF = UIManager.getSystemLookAndFeelClassName();

		// Install the look and feel
		try {
		    UIManager.setLookAndFeel(nativeLF);
		} catch (Exception e) {
			System.err.println("Could not set native look and feel");
		}
		getInstance().setVisible(true);
	}
	
	/* UNUSED IMPLEMENTED METHODS */
	
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void componentHidden(ComponentEvent arg0) {}
	@Override
	public void componentShown(ComponentEvent e) {}
}
