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

package collisioneditor;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import collisioneditor.gui.dialogs.ProgressDialog;
import collisioneditor.utils.Settings;

public class EditCanvas extends Canvas implements Runnable, KeyListener, MouseWheelListener, MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
	
	private boolean running = false;
	
	private BufferedImage im;
	private File imageFile;
	
	private float drag_or_x=0;
	private float drag_or_y=0;
	private float drag_x = 0;
	private float drag_y = 0;
	private float dx=0;
	private float dy=0;
	
	private boolean mousePressed = false;
	
	private ArrayList<Rectangle> rectangles = null;
	
	private Rectangle selectedRect = null;

	public EditCanvas(File imageFile) {
		this.imageFile = imageFile;
		rectangles = new ArrayList<Rectangle>();
		setBackground(Color.WHITE);
		addKeyListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	public void init(){
		setIgnoreRepaint(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		try {
			im = ImageIO.read(imageFile);
			loadRectangles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(){
		Settings settings = Settings.getInstance();
		if(!isVisible())
			return;
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.clearRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(im, 0, 0, getWidth(), getHeight(), null);
		
		if(settings.getBoolean(Settings.EDIT_COLLISIONS, true))
		{
			float w = getWidth();
			float h = getHeight();
			synchronized(rectangles)
			{
				for(Rectangle rect : rectangles)
				{
					if(selectedRect != null && selectedRect == rect)
						g.setColor(new Color(0, 128, 128, 128));
					else
						g.setColor(new Color(255, 128, 0, 128));
					g.fillRect((int) (w*rect.x), (int) (h*rect.y), (int) (w*rect.width), (int) (h*rect.height));
				}
			}
	//		g.setColor(new Color(255, 0, 0, 128));
			if(mousePressed)
			{
				g.setColor(Color.black);
				g.drawRect(
						(int) (w*(dx < 0 ? drag_x : drag_or_x)),
						(int) (h*(dy < 0 ? drag_y : drag_or_y)),
						(int) (w*(dx < 0 ? -dx : dx)), 
						(int) (h*(dy < 0 ? -dy : dy))
						);
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawString("CollisionsEditor v"+CollisionsEditor.VERSION, 5, 15);
		g.drawString("Number of rectangles : " + rectangles.size(), 5, 30);
		
		strategy.show();
	}
	
	@Override
	public void run() {
		if(!running)
		{
			running = true;
			init();
			
			while(running)
			{
				render();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Notify end of rendering
				synchronized(this){
					notifyAll();
				}
			}
		}
	}
	
	public void stop(){
		running = false;
		
		//wait for end of rendering
		synchronized(this){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void autoDetectCollisions(){
		ProgressDialog dial = new ProgressDialog(CollisionsEditor.getInstance(), "Detecting collisions");
		dial.start();
		synchronized(rectangles){
			rectangles.clear();
			detectRectangles(0, 0, im.getWidth(), im.getHeight(), 0);
		}
		dial.stop();
	}
	
	private void detectRectangles(float x, float y ,float w, float h, float n){
		
		for(float j = y; j < y+h; j++)
		{
			for(float i = x; i < x+w; i++)
			{
				if(new Color(im.getRGB((int)i, (int)j), true).getAlpha() >= 250) //TODO : Alpha sensibility should be specified by user in a dialog box
				{
					if(n+1 >= 8 || w/2f <= 0 || h/2f <= 0) // TODO : n_max should be specified by user in a dialog box (7 is good for merge speed, 8 is better for precision)
						rectangles.add(0,new Rectangle(x/(float)im.getWidth(), y/(float)im.getHeight(), w/(float)im.getWidth(), h/(float)im.getHeight()));
					else
					{
						detectRectangles(x, y, w/2f, h/2f, n+1);
						detectRectangles(x+w/2f, y, w/2f, h/2f, n+1);
						detectRectangles(x, y+h/2f, w/2f, h/2f, n+1);
						detectRectangles(x+w/2f, y+h/2f, w/2f, h/2f, n+1);
					}
					return;
				}
			}
		}
	}
	
	public void autoMergeRectangles(){
		ProgressDialog dial = new ProgressDialog(CollisionsEditor.getInstance(), "Merging collisions");
		dial.start();
		synchronized(rectangles){
			mergeRectangles(true);
			mergeRectangles(false);
		}
		dial.stop();
	}
	
	private void mergeRectangles(boolean horizontal){
		//TODO : Manage merging precision
		int m = rectangles.size();
		for(int j = 0; j < m; j++)
		{
			for(int i = 0; i < rectangles.size(); i++)
			{
				Rectangle rectangle = rectangles.get(i);
				for(Rectangle rect : rectangles)
				{
					if(rect.equals(rectangle))
						continue;
					if(horizontal && rect.x == rectangle.x + rectangle.width && rect.y == rectangle.y && rect.height == rectangle.height)
					{
						rectangles.remove(rect);
						rectangles.remove(rectangle);
						rectangles.add(new Rectangle(rectangle.x, rect.y, rectangle.width + rect.width, rect.height));
						break;
					}
					else if(!horizontal && rect.y == rectangle.y + rectangle.height && rect.x == rectangle.x && rect.width == rectangle.width)
					{
						rectangles.remove(rect);
						rectangles.remove(rectangle);
						rectangles.add(new Rectangle(rectangle.x, rectangle.y, rectangle.width, rect.height + rectangle.height));
						break;
					}
				}
			}
		}
	}
	
	public void loadRectangles() {
		synchronized(rectangles){
			rectangles.clear();
			String filename = imageFile.getAbsolutePath() + ".col";
			
			try {
				FileInputStream in = new FileInputStream(filename);
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = r.readLine()) != null)
				{
					Rectangle rect = Rectangle.parse(line);
					if(rect != null)
						rectangles.add(rect);
				}
				r.close();
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveRectangles(){
		synchronized(rectangles){
			if(rectangles == null || rectangles.isEmpty())
				return;
			String filename = imageFile.getAbsolutePath() + ".col";
			
			
			try {
				FileOutputStream out = new FileOutputStream(filename);
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
				
				w.write("# Collision file, generated by CollisionsEditor v" + CollisionsEditor.VERSION +"\n" +
						"# Image file : " + imageFile.getName() + "\n#\n" +
						"# Format : x:y:width:height\\n\n" +
						"# Each value is a float value between 0 and 1\n\n");
				
				for(Rectangle rect : rectangles)
					w.write(rect.toString()+"\n");
				w.flush();
				w.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void clearRectangles(){
		synchronized(rectangles){
			rectangles.clear();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(Settings.getInstance().getBoolean(Settings.EDIT_COLLISIONS, true))
		{
			if(selectedRect != null && e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				synchronized(rectangles){
					rectangles.remove(selectedRect);
				}
				selectedRect = null;
			}
			else if(e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown())
			{
				synchronized(rectangles){
					if(rectangles.size() > 0)
						rectangles.remove(0);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		drag_or_x = (float)e.getX()/getWidth();
		drag_or_y = (float)e.getY()/getHeight();
		mouseDragged(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		drag_x = (float)e.getX()/getWidth();
		drag_y = (float)e.getY()/getHeight();
		dy = - drag_or_y + drag_y;
		dx = - drag_or_x + drag_x;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		if(dx == 0 || dy == 0)
			return;
		if(Settings.getInstance().getBoolean(Settings.EDIT_COLLISIONS, true))
		{
			synchronized(rectangles){
				rectangles.add(0,
						new Rectangle(
							dx < 0 ? drag_x : drag_or_x, 
							dy < 0 ? drag_y : drag_or_y, 
							dx < 0 ? -dx : dx , 
							dy < 0 ? -dy : dy)
						);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(Settings.getInstance().getBoolean(Settings.EDIT_COLLISIONS, true))
		{
			int w = getWidth();
			int h = getHeight();
			
			int x = e.getX();
			int y = e.getY();
			synchronized(rectangles){
				for (Rectangle rect : rectangles)
				{
					if(x > w*rect.x && x < w*(rect.x + rect.width) && y > h*rect.y && y < h*(rect.y + rect.height))
					{
						selectedRect = rect;
						return;
					}
				}
			}
			selectedRect = null;
		}
		return;
	}
	
	/* UNUSED IMPLEMENTED METHODS */
	
	
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	
}
