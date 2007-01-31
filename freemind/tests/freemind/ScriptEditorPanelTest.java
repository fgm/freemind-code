/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 22.01.2007
 */
/*$Id: ScriptEditorPanelTest.java,v 1.1.2.3 2007-01-31 20:20:07 christianfoltin Exp $*/
package tests.freemind;

import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.awt.Dimension;
import java.io.PrintStream;

import plugins.script.ScriptEditorPanel;
import plugins.script.ScriptEditorPanel.ScriptHolder;
import plugins.script.ScriptEditorPanel.ScriptModel;

/**
 * @author foltin
 *
 */
public class ScriptEditorPanelTest extends FreeMindTestBase {

	private static final class TestScriptModel implements ScriptModel {
		String[] scripts = {"j=0;\nfor(i in (0..6))\n	{\n		j+=i; \n		print i;\n	}; \nreturn j;\n",
		"for(i=0;i<5;++i){print i;}"};

		public int getAmountOfScripts() {
			// TODO Auto-generated method stub
			return 2;
		}

		public ScriptHolder getScript(int pIndex) {
				return new ScriptHolder("script"+pIndex, scripts[pIndex]);
		}

		public void setScript(int pIndex, ScriptHolder pScript) {
			scripts[pIndex] = pScript.getScript();
		}

		public String executeScript(int pIndex, PrintStream outStream) {
			Binding binding = new Binding();
			binding.setVariable("c", null);
			binding.setVariable("node", null);
			GroovyShell shell = new GroovyShell(binding);
		
			String script = getScript(pIndex).getScript();
			// redirect output:
			PrintStream oldOut = System.out;
			Object value;
			try {
				System.setOut(outStream);
				value = shell.evaluate(script);
			} finally {
				System.setOut(oldOut);
			}
			return (value!= null)?value.toString():null;
		}

		public void storeDialogPositions(ScriptEditorPanel pPanel, ScriptEditorWindowConfigurationStorage pStorage, String pWindow_preference_storage_property) {
			// TODO Auto-generated method stub
			
		}

		public ScriptEditorWindowConfigurationStorage decorateDialog(ScriptEditorPanel pPanel, String pWindow_preference_storage_property) {
			ScriptEditorWindowConfigurationStorage storage = new ScriptEditorWindowConfigurationStorage();
			storage.setHeight(800);
			storage.setWidth(400);
			pPanel.getRootPane().setPreferredSize(new Dimension(storage.getWidth(), storage.getHeight()));
			storage.setLeftRatio(100);
			storage.setTopRatio(500);
			return storage;
		}

        public void endDialog(boolean pIsCanceled) {
        }
	}

	public void testPanel() {
		ScriptEditorPanel scriptEditor = new ScriptEditorPanel(
				new TestScriptModel(), new FreeMindMainMock() );
		scriptEditor.pack();
		scriptEditor.setVisible(true);
	}
	
	public void testErrorLineNumbers() throws Exception {
		String error = "startup failed, Script1.groovy: 1: For statement contains unexpected tokens. Possible attempt to use unsupported Java-style for loop. at line: 1 column: 1. File: Script1.groovy @ line 1, column 1.\n1 error";
		assertEquals("find right line number" , 1 , ScriptEditorPanel.findLineNumberInString(error, -1));
	}
}
