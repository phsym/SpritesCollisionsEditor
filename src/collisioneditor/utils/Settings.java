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

package collisioneditor.utils;
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
import java.util.Hashtable;

import collisioneditor.CollisionsEditor;


public class Settings {
	
	private static int MAX_RECENTLY_USED = 5;
	
	public static final String LAST_LOADED_PATH = "LastLoadedPath";
	public static final String FRAME_WIDTH = "FrameWidth";
	public static final String FRAME_HEIGHT = "FrameHeight";
	public static final String FRAME_X = "FrameX";
	public static final String FRAME_Y = "FrameY";
	
	public static final String EDIT_COLLISIONS = "HideRect";
	
	private static final String RECENTLY_USED = "RecentlyUsed";
	
	private File file = new File(System.getProperty("user.home") + File.separator + ".CollisionsEditor");
	
	private Hashtable<String, String> settings;
	private ArrayList<String> recentlyUsed;
	
	private static Settings _instance = null;
	
	public static Settings getInstance(){
		if(_instance == null)
			_instance = new Settings();
		return _instance;
	}

	private Settings() {
		settings = new Hashtable<String, String>();
		recentlyUsed = new ArrayList<String>(5);
		loadSettings();
	}
	
	public void loadSettings(){
		settings.clear();
		try {
			if(!file.exists())
			{
				file.createNewFile();
				return;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = in.readLine()) != null)
			{
				line.replaceAll(" ", "");
				if(!line.isEmpty() && !line.startsWith("#"))
				{
					String[] tmp = line.split("=");
					if(tmp.length == 2)
					{
						if(tmp[0].equals(RECENTLY_USED))
							recentlyUsed.add(tmp[1]);
						else
							settings.put(tmp[0], tmp[1]);
					}
				}
			}
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveSettings(){
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			
			w.write("# Preferences file for CollisionsEditor v" + CollisionsEditor.VERSION + "\n\n");
			
			for(String k : settings.keySet())
				w.write(k + "=" + settings.get(k)+"\n");
			for(String r : recentlyUsed)
				w.write(RECENTLY_USED + "=" + r+"\n");
			
			w.flush();
			w.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addRecentlyUsed(String path){
		if(recentlyUsed.contains(path))
			return;
		if(recentlyUsed.size() >= MAX_RECENTLY_USED)
			recentlyUsed.remove(0);
		recentlyUsed.add(path);
	}
	
	public ArrayList<String> getRecentlyUsed() {
		return recentlyUsed;
	}
	
	public String get(String setting, String def){
		String str = settings.get(setting);
		if(str == null)
			return def;
		return str;
	}
	
	public void set(String setting, String value){
		settings.put(setting, value);
	}
	
	public boolean getBoolean(String setting, boolean def){
		return get(setting, def ? "1" : "0").equals("1");
	}
	
	
	public void set(String setting, boolean value){
		set(setting, (value ? "1" : "0"));
	}
	
	public int getInt(String setting, int def){
		int val;
		try {
			val = Integer.parseInt(get(setting, String.valueOf(def)));
		} catch (Exception e) {
			val = def;
		}
		return val;
	}
	
	public void set(String setting, int value){
		set(setting, String.valueOf(value));
	}
}
