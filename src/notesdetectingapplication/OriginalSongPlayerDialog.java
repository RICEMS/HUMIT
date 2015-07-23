/*************************************************************************
 * This class is used to handle operations related to Song playing (as a WAV file or MIDI file)
 * Category: Module Classes/ GUI classes
 *************************************************************************/
package notesdetectingapplication;

import java.util.ArrayList;

import nd.hackrice.backend.Context;

public class OriginalSongPlayerDialog extends Context {

    //to print notes on UI
    private int ArrayListIndex = 0;
    private int TimeTicksCounter = 0;
    private int NoteDisplayTimeInterval = 50;
    private int VolumeLevel = 0;
    private NotesDetectingApplicationForm parent;
    private ArrayList<MIDIData> MIDINotesList;
    private MIDIFileCreator midiFileCreator;

    //Create sound engine instance
    SoundIO soundEngine = new SoundIO();
    private static final int KEY_ON = 144;   //as defined in MIDI API for java
    private static double[] dAverage;
    private static int dAverageCount = 0;
    private static double playingTimeValue = 0;
    private static boolean playingStarted = false;

    /** Creates new form OriginalSongPlayerDialog */
    public OriginalSongPlayerDialog(Context parent, boolean modal) {
        
        
        this.parent = (NotesDetectingApplicationForm) parent;
        this.MIDINotesList = this.parent.MIDINotesList;
        this.midiFileCreator = this.parent.midiFileCreator;
        setClipLength(this.parent.ClipLength * 1000);
        
        setDetectedInstrument();
    }


    public void play(boolean isOri,int volume)
    {
    	playAccording2Arg(isOri, volume);
    }
    private void playAccording2Arg(boolean isOri,int volume)
    {
    	doPlayJobs();
    	if(isOri)
    	{
    		soundEngine.play(parent.choosenWaveFilePath, volume, 44100);
    	}
    	else
    	{
    		this.midiFileCreator.playTemporatyTrack();
    	}
    }
    private void doPlayJobs()
    {
    	playingStarted = true;

        int N = parent.dataFromWaveFile.length;
        double[] d = new double[N / 2];
        dAverage = new double[d.length / 105];

        //convert bytes to short values
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((parent.dataFromWaveFile[2 * i + 1] & 0xFF) << 8) + (parent.dataFromWaveFile[2 * i] & 0xFF))) / ((double) Short.MAX_VALUE);
        }

        //assign the averaged values to the avarage array
        for (int i = 0; i < ((d.length / 110) - 1); i++) {
            double average = 0;
            int tempCount = 0;
            double[] temp = new double[110];
            for (int j = i * 110; j < ((i + 1) * 110); j++) {
                temp[tempCount] = d[j];
                tempCount++;

            }
            average = getAverage(i, temp);
            dAverage[i] = average;
        }

        //begin a new player iinstance
        TimeTicksCounter = 0;
        ArrayListIndex = 0;
        dAverageCount = 0;
        playingTimeValue = 0;

        //Start the progress bar update thread
        new Thread(new ProgressUpdatingThread()).start();
    }

    public void setInstrument(int type)
    {
    	this.midiFileCreator.createTempTrack(type, parent.MIDINotesList);
    }


    /**
     * Average out original amplitude data to make plotting less intensive
     * @param start
     * @param d
     * @return 
     */
    public static double getAverage(int start, double[] d) {
        double average = 0;
        for (int i = 0; i < 110; i++) {
            average = average + d[i];
        }
        return average / 110;
    }

    /**
     * Update clip length
     * @param pmilliseconds 
     */
    private void setClipLength(double pmilliseconds) {
        int lenInMilliseconds = (int) pmilliseconds;
        int hours = lenInMilliseconds / (1000 * 60 * 60);
        int minutes = (lenInMilliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((lenInMilliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        int milliseconds = ((lenInMilliseconds % (1000 * 60 * 60)) % (1000 * 60) % 1000);
        //clipLengthLabel.setText(hours + ":" + minutes + ":" + seconds + ":" + milliseconds);
    }

    //The thread
    public static class ProgressUpdatingThread implements Runnable {

        public void run() {
            int value = 0;
            double originalValue;
            double maxdAverage = 0;
            int currentPlayTimeInMiliseconds = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;


            //find maximum from the averaged  data list
            for (int i = 0; i < dAverage.length; i++) {
                if (maxdAverage < dAverage[i]) {
                    maxdAverage = dAverage[i];
                }
            }
            while (playingStarted == true) {
                originalValue = (dAverage[dAverageCount] / maxdAverage) * 100;
                value = (int) originalValue + 50;
                //Set value
                if (value < 100) {
//                    leftProgressBar.setValue(value);
//                    rightProgressBar.setValue(value);
                } else {
//                    leftProgressBar.setValue(100);
//                    rightProgressBar.setValue(100);
                }
                //set bars to zero when playing is over
                if (dAverageCount == dAverage.length - 1) {
//                    leftProgressBar.setValue(0);
//                    rightProgressBar.setValue(0);
                }
                //Refresh graphics
//                leftProgressBar.repaint();
//                rightProgressBar.repaint();

                currentPlayTimeInMiliseconds = (int) playingTimeValue * 1000;

                hours = currentPlayTimeInMiliseconds / (1000 * 60 * 60);
                minutes = (currentPlayTimeInMiliseconds % (1000 * 60 * 60)) / (1000 * 60);
                seconds = ((currentPlayTimeInMiliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
                milliseconds = ((currentPlayTimeInMiliseconds % (1000 * 60 * 60)) % (1000 * 60) % 1000);
                //currentPositionLabel.setText(hours + ":" + minutes + ":" + seconds + ":" + milliseconds);
                try {
                    Thread.sleep(50);
                } //Sleep 50 lenInMilliseconds
                catch (InterruptedException err) {
                }
            }
        }
    }

    
    /**
     * Set the detected instrument to the ComboBox
     */
    private void setDetectedInstrument()
    {
        int detectedInstrument = midiFileCreator.getInitialInstrument();
        int selectedIndex;
        switch (detectedInstrument) {
            case 41:
                selectedIndex = 0;
                break;
            case 7:
                selectedIndex = 1;
                break;
            case 26:
                selectedIndex = 2;
                break;
            case 1:
                selectedIndex = 3;
                break;
            case 74:
                selectedIndex = 4;
                break;
            case 79:
                selectedIndex = 5;
                break;
            default:
                //set piano as the instrument
                selectedIndex = 1;
        }
    }
    
}
