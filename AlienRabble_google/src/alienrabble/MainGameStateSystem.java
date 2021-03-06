/*
 * Code Copyright (c) 2007-2009 Caspar Addyman
 * 
 * 
 * Original Code Copyright (c) 2003-2007 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package alienrabble;

import java.util.logging.Level;
import java.util.logging.Logger;

import alienrabble.logging.ARDataLoadandSave;
import alienrabble.logging.ARXMLExperimentData.GameType;

import com.jme.app.AbstractGame;
import com.jme.app.BaseGame;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;

import com.jme.input.joystick.JoystickInput;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;


/**
 * <p>
 * This test shows how to use the game state system. It can not extend
 * SimpleGame because a lot of SimpleGames functions (e.g. camera, rootNode and input)
 * has been delegated down to the individual game states. So this class is
 * basically a stripped down version of SimpleGame, which inits the
 * GameStateManager and launches a MenuState.
 * </p>
 * 
 * <p>
 * It also has a special way to reach the finish method, using a singleton instance
 * and a static exit method.
 * </p>
 * 
 * @author Per Thulin
 */
public class MainGameStateSystem extends BaseGame {
    private static final Logger logger = Logger
            .getLogger(MainGameStateSystem.class.getName());
	
	/** Only used in the static exit method. */
	private static AbstractGame instance;
	
	public static String ARVersionNumber = "1.0"; 
	
	/** High resolution timer for jME. */
	public Timer timer;
	
	/** The object that handles data logging */
	public ARDataLoadandSave datalogger;
	
	/** Simply an easy way to get at timer.getTimePerFrame(). */
	private float tpf;
	
	/**
	 * This is called every frame in BaseGame.start()
	 * 
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#update(float interpolation)
	 */
	protected final void update(float interpolation) {
		// Recalculate the framerate.
		timer.update();
		tpf = timer.getTimePerFrame();
		
		// Update the current game state.
		GameStateManager.getInstance().update(tpf);
	}
	
	/**
	 * This is called every frame in BaseGame.start(), after update()
	 * 
	 * @param interpolation unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected final void render(float interpolation) {	
		// Clears the previously rendered information.
		display.getRenderer().clearBuffers();
		// Render the current game state.
		GameStateManager.getInstance().render(tpf);
	}
	
	/**
	 * Creates display, sets  up camera, and binds keys.  Called in BaseGame.start() directly after
	 * the dialog box.
	 * 
	 * @see AbstractGame#initSystem()
	 */
	protected final void initSystem() {
		logger.info("initialzing system caspee");
		
		try {
			/** Get a DisplaySystem according to the renderer selected in the startup box. */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer());
			/** Create a window with the startup box's information. */
			display.createWindow(
					settings.getWidth(),
					settings.getHeight(),
					settings.getDepth(),
					settings.getFrequency(),
					settings.getBoolean("FULLSCREEN", false ));
			/** Create a camera specific to the DisplaySystem that works with
			 * the display's width and height*/			
		}
		catch (JmeException e) {
			/** If the display system can't be initialized correctly, exit instantly. */
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit(1);
		}
		
		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();
		
		//set up the logging classes
		initDataLogging();	
	}
	
	/**
	 * Called in BaseGame.start() after initSystem().
	 * 
	 * @see AbstractGame#initGame()
	 */
	protected final void initGame() {		
		instance = this;
		display.setTitle("Alien Rabble");
		
		// Creates the GameStateManager. Only needs to be called once.
		GameStateManager.create();
		// Adds a new GameState to the GameStateManager. In order for it to get
		// processed (rendered and updated) it needs to get activated.
		GameState menu = new MenuState("menu", settings);
		menu.setActive(true);
		GameStateManager.getInstance().attachChild(menu);
	
	}
	
	private void initDataLogging(){
		datalogger = ARDataLoadandSave.getInstance();
		datalogger.setUpLoadandSaveDocs("init_dyn.xml");	
		// load the participant and model init data 
		datalogger.getXmlExperimentData().loadExperimentInit();
		if(datalogger.getXmlExperimentData().getGameType() == GameType.RULEDISCOVERY){
			//What models were used in this experiment. 
			for(int block=0;block<datalogger.getXmlExperimentData().getNumBlocks();block++){
//				datalogger.getXmlModelData_RuleDiscovery(block).setModelSetName(datalogger.getXmlExperimentData().getBlockModelFile(block));
//				datalogger.getXmlModelData_RuleDiscovery(block).
				datalogger.getXmlModelData_RuleDiscovery(block).loadModelPaths();
			}
		}else{
			//this next line is hideous.. I apologize!					
			datalogger.getXmlModelData_Grab().setModelSetName(datalogger.getXmlExperimentData().getModelSet_Grab());
			datalogger.getXmlModelData_Grab().loadModelPaths();
			//same again for sort models
			datalogger.getXmlModelData_Sort().setModelSetName(datalogger.getXmlExperimentData().getModelSet_Sort());
			datalogger.getXmlModelData_Sort().loadModelPaths();			
		}
	}
	
	/**
	 * Empty.
	 * 
	 * @see AbstractGame#reinit()
	 */
	protected void reinit() { 
	}
	
	/**
	 * Cleans up the keyboard and game state system.
	 * 
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		logger.info("Cleaning up resources.");
		
		// Performs cleanup on all loaded game states.
		GameStateManager.getInstance().cleanup();
		
        KeyInput.destroyIfInitalized();
        MouseInput.destroyIfInitalized();
        JoystickInput.destroyIfInitalized();
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainGameStateSystem app = new MainGameStateSystem();
		app.setConfigShowMode(AbstractGame.ConfigShowMode.AlwaysShow, MainGameStateSystem.class.getClassLoader()
	             .getResource("alienrabble/data/texture/splashscreen_v1_0.png"));
		app.start();
	}
	
	/**
	 * Static method to finish this application.
	 */
	public static void exit() {
		instance.finish();
	}
}
