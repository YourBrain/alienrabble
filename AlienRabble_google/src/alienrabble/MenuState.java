/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import alienrabble.logging.ARDataLoadandSave;

import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.GameSettings;
import com.jme.util.TextureManager;
import com.jmex.game.state.CameraGameState;
import com.jmex.game.state.GameState;




/** 
 * @author Per Thulin
 */
public class MenuState extends CameraGameState {
	private static final long serialVersionUID = 1L;
	
	//the different possible menu states
	public static final int MENU_START = 0;
	public static final int MENU_RULE_INSTRUCTIONS = 1;
	public static final int MENU_RULE_NEWBLOCK = 2;
	public static final int MENU_RULE_NEWROUND = 3;
	public static final int MENU_SORT_INSTRUCTIONS = 4;
	public static final int MENU_SORT_FEEDBACK = 5;
	public static final int MENU_FINISH = 6;

	//what type of menu should be displaying 
	public int menuStatus;
	
	/** The cursor node which holds the mouse gotten from input. */
	private Node cursor;
	
	/** Our display system. */
	private DisplaySystem display;
	
	private GameSettings settings;

	// the objects for displaying the instruction text
    private Text text;
    private Text text1;
    private Text text2;
    private Text text3;
    
    private String participantname; 

    private InputHandler input;
    private Mouse mouse;
    
    public MenuState(String name, GameSettings settings, String participantname) {
    	this(name,settings);
    	this.participantname = participantname;
 	
    }
    public MenuState(String name, GameSettings settings) {
        super(name);

        display = DisplaySystem.getDisplaySystem();
        this.settings =  settings;
        initInput();
        initCursor();
        initText();
        menuStatus = MENU_RULE_INSTRUCTIONS;
        writeText();
//        initGUI();	
        
        rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);
        rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
    }
	
	/**
	 * @see com.jmex.game.state.CameraGameState#onActivate()
	 */
	public void onActivate() {
		display.setTitle("Alien Rabble- Menu");
		super.onActivate();
		writeText();
		if (menuStatus == MENU_FINISH){
			//all done so save the output file
			ARDataLoadandSave.getInstance().saveAllData();
		}
	}
	
	/**
	 * Inits the input handler we will use for navigation of the menu.
	 */
	protected void initInput() {
		input = new MenuHandler( this, settings );

        DisplaySystem display = DisplaySystem.getDisplaySystem();
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(),
                display.getHeight());
        mouse.registerWithInputHandler( input );
	}
	
	
	/**
	 * Creates a pretty cursor.
	 */
	private void initCursor() {		
		Texture texture =
	        TextureManager.loadTexture(
	    	        MenuState.class.getClassLoader().getResource(
	    	        "alienrabble/data/cursor/cursor1.png"),
	    	        Texture.MinificationFilter.Trilinear,
	    	        Texture.MagnificationFilter.Bilinear);
		
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(texture);
		
		BlendState alpha = display.getRenderer().createBlendState();
		alpha.setBlendEnabled(true);
		alpha.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		alpha.setDestinationFunction(BlendState.DestinationFunction.One);
		alpha.setTestEnabled(true);
		alpha.setTestFunction(BlendState.TestFunction.GreaterThan);
		alpha.setEnabled(true);
		
		mouse.setRenderState(ts);
        mouse.setRenderState(alpha);
        mouse.setLocalScale(new Vector3f(1, 1, 1));
		
		cursor = new Node("Cursor");
		cursor.attachChild( mouse );
		
		rootNode.attachChild(cursor);
	}
	
	/**
	 * Inits the button placed at the center of the screen.
	 */
	private void initText() {
        text = Text.createDefaultTextLabel( "welcome" );
        text.getLocalTranslation().set( 400, 720, 0 );
        rootNode.attachChild( text );

		text1 = Text.createDefaultTextLabel( "instructions 1" );
        text1.getLocalTranslation().set( 400, 600, 0 );
        rootNode.attachChild( text1 );

        text2 = Text.createDefaultTextLabel( "instructions 2" );
        text2.getLocalTranslation().set( 400, 400, 0 );
        rootNode.attachChild( text2 );
        
        text3 = Text.createDefaultTextLabel( "info" );
        text3.getLocalTranslation().set( 400, 200, 0 );
        rootNode.attachChild( text3 );
 	}
	 
	private void writeText(){
		if (menuStatus == MENU_START){
		}else if (menuStatus == MENU_RULE_INSTRUCTIONS){
			String name = ARDataLoadandSave.getInstance().getXmlExperimentData().getName();
			text.print("Welcome " + name + "!");
			text1.print( "Collect all the good aliens and avoid the bad ones." );
			text2.print( "Use the arrow keys to navigate round the space." );
			text3.print( "press ENTER to begin" );
		}else if (menuStatus == MENU_RULE_NEWBLOCK){
			text.print("New Block");
			text1.print( "This is a different set of aliens" );
			text2.print( "They may have different families" );
			text3.print( "press ENTER to begin" );			
		}else if (menuStatus == MENU_RULE_NEWROUND){
			text.print("Great");
			text1.print( "Now do the same again!" );
			text2.print( "Only faster and  no mistakes" );
			text3.print( "press ENTER to begin" );
		}else if (menuStatus == MENU_SORT_INSTRUCTIONS){
			text.print("");
		    text1.print( "Use mouse to select individual aliens." );
	        text2.print( "Then click on the box you want to place them in." );
	        text3.print( "press ENTER to begin" );
		}else if (menuStatus == MENU_SORT_FEEDBACK){
			
		}else if (menuStatus == MENU_FINISH){
			text.print("");
			text1.print( "Thank you for your participation" );
	        text2.print( "" );
			text3.print( "press ENTER to finish" );
		}		
	}
	
	
		
	/**
	 * Updates input and button.
	 * 
	 * @param tpf The time since last frame.
	 * @see GameState#update(float)
	 */
	protected void stateUpdate(float tpf) {
		input.update(tpf);
		// Check if the button has been pressed.
		rootNode.updateGeometricState(tpf, true);
	}

	
}