package gui;

import java.io.InputStream;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Console {
	
	private HashMap<Integer, Boolean> streamFinishedObserver;
	
	public Console() {
		streamFinishedObserver = new HashMap<Integer, Boolean>();
	}
	
	public void redirectOutput(JTextArea displayPane, JLabel label, InputStream out, InputStream err, int index)
    {
		streamFinishedObserver.put(index, false);
		
		SimpleTextAreaWriter console = new SimpleTextAreaWriter(this, index, displayPane, err);
        new Thread(console).start();
        
        RoundAndTextAreaWriter lWriter = new RoundAndTextAreaWriter(this, index, label, displayPane, out);
        new Thread(lWriter).start();
    }
	
	protected synchronized void streamFinished(int id) {
		if (streamFinishedObserver.get(id)) {
			// zweiter Stream mit dieser ID ist fertig -> Hook Methode aufrufen
			onStreamEnd(id);
		} else {
			streamFinishedObserver.put(id, true);
		}
	}
	
	public void onStreamEnd(int id) {
		// Hook-Methode
	}
}
