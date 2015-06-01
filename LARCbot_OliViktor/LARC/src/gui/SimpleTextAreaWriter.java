package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class SimpleTextAreaWriter implements Runnable
{
    JTextArea textArea;
    BufferedReader reader;
    
    private Console parent;
    private int id;

    protected SimpleTextAreaWriter(Console parent, int id, JTextArea textArea, InputStream stream)
    {
        this.textArea = textArea;

        reader = new BufferedReader( new InputStreamReader(stream) );
        
        this.parent = parent;
        this.id = id;
    }

    public void run()
    {
        String line = null;

        try
        {
            while ((line = reader.readLine()) != null)
            {
            	textArea.append( line + "\n" );
            	textArea.setCaretPosition( textArea.getDocument().getLength() );
            }
            
            parent.streamFinished(id);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null,
                "Error redirecting output : "+ioe.getMessage());
        }
    }
}
