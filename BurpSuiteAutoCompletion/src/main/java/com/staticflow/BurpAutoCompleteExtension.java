package com.staticflow;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionStateListener;
import burp.ITab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class BurpAutoCompleteExtension implements IBurpExtender, AWTEventListener, IExtensionStateListener, ITab {

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks iBurpExtenderCallbacks) {
		ExtensionState.setCallbacks(iBurpExtenderCallbacks);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
		iBurpExtenderCallbacks.registerExtensionStateListener(this);
		iBurpExtenderCallbacks.addSuiteTab(this);
	}

	@Override
	public void extensionUnloaded() {
		ExtensionState.getInstance().getCallbacks().printOutput("removing listeners");
		System.out.println(Arrays.toString(Toolkit.getDefaultToolkit().getAWTEventListeners()));

		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		System.out.println(Arrays.toString(Toolkit.getDefaultToolkit().getAWTEventListeners()));
		for (AutoCompleter listener : ExtensionState.getInstance().getListeners()) {
			listener.detachFromSource();
			listener.getSource().getDocument().removeDocumentListener(listener);
		}
	}

	/**
	 * This hooks keyboard events for the entire application. Only textareas are
	 * considered. Practically, this includes Repeater, Intruder, and any extension
	 * which uses JTextArea.
	 * 
	 * @param event keyboard event
	 */
	
	public int chk = 0;
	public int chk1 = 0;
	
	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getSource() instanceof JTextArea) {
			JTextArea source = ((JTextArea) event.getSource());

			if (source.getClientProperty("hasListener") == null
					|| !((Boolean) source.getClientProperty("hasListener"))) {
				ExtensionState.getInstance().getCallbacks().printOutput("Adding Listener");
				AutoCompleter t = new AutoCompleter(source);
				source.getDocument().addDocumentListener(t);
				source.putClientProperty("hasListener", true);

				ExtensionState.getInstance().addListener(t);

				source.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						// Check : Ctrl + Shift + N --> Show
						if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == 78) {
							if(chk1==0) {
								t.suggestionPane.setVisible(true);
								t.suggestionPane.toFront();
								chk1=1;
							} else {
								t.suggestionPane.setVisible(false);
								chk1=0;
							}
							
							
						}
//                		// Check : Ctrl + Shift + M  -> Show all
						if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == 77) {
							
							if(chk==0) {
								t.suggestionPane.setVisible(true);
								t.suggestionPane.toFront();
								t.suggestionsModel.addAll(ExtensionState.getInstance().keywords);
								
								Point p = MouseInfo.getPointerInfo().getLocation();	
				            	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				            	t.suggestionPane.setSize(350, (screenSize.height*2)/3);
								t.suggestionPane.setLocation(p.x, screenSize.height/3);
								
								chk=1;
							} else {
								t.suggestionPane.setVisible(false);
								chk=0;
							}
							
						}

					}
				});
			}
		}
	}

	@Override
	public String getTabCaption() {
		return "AutoPayloads";
	}

	@Override
	public Component getUiComponent() {
		return ExtensionState.getInstance().getAutoCompleterTab();
	}
}
