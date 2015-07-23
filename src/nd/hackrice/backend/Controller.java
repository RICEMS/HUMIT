package nd.hackrice.backend;

import java.util.ArrayList;

import notesdetectingapplication.InstrumentClassificationDialog;
import notesdetectingapplication.MIDIData;
import notesdetectingapplication.MIDIFileCreator;
import notesdetectingapplication.NotesData;
import notesdetectingapplication.NotesDetectingApplicationForm;
import notesdetectingapplication.NotesEditingLibrary;
import notesdetectingapplication.OriginalSongPlayerDialog;
import notesdetectingapplication.ResultsEditorDialog;
import notesdetectingapplication.SoundIO;
import notesdetectingapplication.InstrumentClassification.InstrumentClassifier;

public class Controller 
{
	NotesDetectingApplicationForm app;
	String filePath;
	boolean isOriginal=false;
	int volume=10;
	int instrumentType=1;
	public Controller()
	{
		app=new NotesDetectingApplicationForm();
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
		this.app.setFilePath(filePath);
	}
	public boolean isOriginal() {
		return isOriginal;
	}
	public void setOriginal(boolean isOriginal) {
		this.isOriginal = isOriginal;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public int getInstrumentType() {
		return instrumentType;
	}
	public void setInstrumentType(int instrumentType) {
		this.instrumentType = instrumentType;
		app.setInitialInstrument(instrumentType);
	}
	public void setNDResDir(String dir)
	{
		app.setnDResDir(dir);
	}
	public int getInstrument(String name)
    {
        if(name.equalsIgnoreCase("Flute"))
        {
        	setInstrumentType(74);
            return 74;
        }
        else if(name.equalsIgnoreCase("Violin"))
        {
        	setInstrumentType(41);
            return 41;
        }
        else if(name.equalsIgnoreCase("Piano"))
        {
        	setInstrumentType(1);
            return 1;
        }
        else if(name.equalsIgnoreCase("Guitar"))
        {
        	setInstrumentType(26);
            return 26;
        }
        else
        {
            return 1;
        }
    }
	public void analyse(String outpath)
	{
		app.analyse(this.instrumentType);
		app.createMIDIFile(outpath);
	}
}
