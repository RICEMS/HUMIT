/*************************************************************************
 * This is the class representing MIDI file creator module
 * It uses Java Sound API
 * Category: Module Classes

 * MIDI file consists of a sequence of MIDI events.(which is added to a sequencer in sequence) MIDI event means (MIDI message, time to execute that command) pair.
 * MIDI message says what to do. MIDI message takes several arguements to create.
 * 1. Type of the message. for eg 144 means NOTES_ON.
 * 2. The channel. The note can be of vivid instruments. What instrument is denoted by this arguement. (1-keyboard, 9-drum)
 * 3. Which note is to paly. (eg: middle C)(0-127)
 * 4. At velocity the note has to be played. For eg in piano if the key is pressed hard the quality of the note get changed.(0-you hear nothing 100 is good)
 *
 * Make a MIDI message and pass the above details by setMessage()
 * MIDI message says what to do and MIDI event says when to trigger the message
 * Changing the note, changing the duration of a note (change note off event - 128),  change instrument(type- 192, should be called before being played)
 * There are tree levels in the hierarchy sequencer, track, MIDI event
 * sequencer>>MIDI track>>MIDI event>>MIDI messeage>>data bytes
 * Standard MIDI files of type 0 by definition contains only one track.*/
//********************************************************************************************************************************************************************
//TIMING IN MIDI

/*What is the duration of a tick? It can vary between sequences (but not within a sequence), and its value is stored in the header of a standard MIDI file. The size of a tick is given in one of two types of units:

Pulses (ticks) per quarter note, abbreviated as PPQ
Ticks per frame, also known as SMPTE time code (a standard adopted by the Society of Motion Picture and Television Engineers)

If the unit is PPQ, the size of a tick is expressed as a fraction of a quarter note, which is a relative, not absolute, time value. A quarter note is a musical duration value that often corresponds to one beat of the
music (a quarter of a measure in 4/4 time). The duration of a quarter note is dependent on the tempo, which can vary during the course of the music if the sequence
contains tempo-change events. So if the sequence's timing increments (ticks) occur, say 96 times
per quarter note, each event's timing value measures that event's position in musical terms, not as an absolute time value.
On the other hand, in the case of SMPTE, the units measure absolute time, and the notion of tempo is inapplicable. There are actually four different SMPTE conventions available, which refer to the number of motion-picture frames per second. The number of frames per second 
can be 24, 25, 29.97, or 30. With SMPTE time code, the size of a tick is expressed as a fraction of a frame.

In the Java Sound API, you can invoke Sequence.getDivisionType to learn which type of unit—namely, PPQ or one of the SMPTE units—is used in a particular sequence. You can then calculate the size of a tick after invoking Sequence.getResolution. The latter method returns 
the number of ticks per quarter note if the division type is PPQ, or per SMPTE frame if the division type is
one of the SMPTE conventions. You can get the size of a tick using this formula in the case of PPQ:

ticksPerSecond =
resolution * (currentTempoInBeatsPerMinute / 60.0);
tickSize = 1.0 / ticksPerSecond;
and this formula in the case of SMPTE:

framesPerSecond =
(divisionType == Sequence.SMPTE_24 ? 24
: (divisionType == Sequence.SMPTE_25 ? 25
: (divisionType == Sequence.SMPTE_30 ? 30
: (divisionType == Sequence.SMPTE_30DROP ?

29.97))));
ticksPerSecond = resolution * framesPerSecond;
tickSize = 1.0 / ticksPerSecond;
The Java Sound API's definition of timing in a sequence mirrors that of the Standard MIDI Files specification. However, there's one important difference. The tick values contained in MidiEvents measure cumulative time, rather than delta time. In a standard MIDI file, each event's timing information measures the amount of time elapsed since the onset of the previous event in the
sequence. This is called delta time. But in the Java Sound API, the ticks aren't delta values; they're the previous event's time value plus the delta value. In other words, in the Java Sound API the timing value for each event is always greater than that of the previous event in the sequence (or equal, if the events are supposed to be simultaneous). Each event's timing value
measures the time elapsed since the beginning of the sequence.*/
//************************************************************************************************************************************************************************************************************************************************************************************************************
package notesdetectingapplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequencer;

public class MIDIFileCreator {

    private int velocity = 64;
    private double duration_of_a_segment = 0.0;
    private static final int FRAMES_PER_SECOND = 25;    // According to theory this should be 24 but has to tally with input
    private static int TICS_PER_FRAME = 1;
    private static final int KEY_ON = 144;   //as defined in MIDI API for java
    private static final int KEY_OFF = 128;  //as defined in MIDI API for java
    private static int MIDI_CHANNEL = 1;      //always use channel 1. MIDI can use upto 16 channels
    private int initialInstrument = 41;     //default violene
    private Sequence sequence = null;
    Sequence tempSequence =null;
    private Sequencer sequencer;
    private double[] noteFrequencies = {
        8.176, 8.662, 9.177, 9.723, 10.301, 10.913, 11.562, 12.25, 12.978, 13.75, 14.568, 15.434, 16.352, 17.324, 18.354, 19.445, 20.601, 21.826, 23.124,
        24.499, 25.956, 27.5, 29.135, 30.867, 32.703, 34.648, 36.708, 38.89, 41.203, 43.653, 46.249, 48.999, 51.913, 55, 58.27, 61.735, 65.406, 69.295,
        73.416, 77.781, 82.406, 87.307, 92.499, 97.998, 103.82, 110, 116.54, 123.47, 130.81, 138.59, 146.83, 155.56, 164.81, 174.61, 184.99, 195.99,
        207.65, 220, 233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 391.99, 415.31, 440, 466.16, 493.88, 523.25, 554.37, 587.33, 622.25,
        659.26, 698.46, 739.99, 783.99, 830.61, 880, 932.32, 987.77, 1046.5, 1108.7, 1174.7, 1244.5, 1318.5, 1396.9, 1480, 1568, 1661.2, 1760, 1864.7, 1975.5,
        2093, 2217.5, 2349.3, 2489, 2637, 2793.8, 2960, 3136, 3322.4, 3520, 3729.3, 3951.1, 4186, 4434.9, 4698.6, 4978, 5274, 5587.7, 5919.9, 6271.9, 6644.9, 7040,
        7458.6, 7902.1, 8372, 8869.8, 9397.3, 9956.1, 10548.1, 11175.3, 11839.8, 12543.9
    };
    //Array containing symbols of the notes
    private String[] notesArr = {"C -1", "C# -1", "D -1", "D# -1", "E -1", "F -1", "F# -1", "G -1", "G# -1",
        "A -1", "A# -1", "B -1", "C 0", "C# 0", "D 0", "D# 0", "E 0", "F 0", "F# 0", "G 0",
        "G# 0", "A 0", "A# 0", "B 0", "C 1", "C# 1", "D 1", "D# 1", "E 1", "F 1", "F# 1",
        "G 1", "G# 1", "A 1", "A# 1", "B 1", "C 2", "C# 2", "D 2", "D# 2", "E 2", "F 2",
        "F# 2", "G 2", "G# 2", "A 2", "A# 2", "B 2", "C 3", "C# 3", "D 3", "D# 3", "E 3",
        "F 3", "F# 3", "G 3", "G# 3", "A 3", "A# 3", "B 3", "C 4", "C# 4", "D 4", "D# 4",
        "E 4", "F 4", "F# 4", "G 4", "G# 4", "A 4", "A# 4", "B 4", "C 5", "C# 5", "D 5",
        "D# 5", "E 5", "F 5", "F# 5", "G 5", "G# 5", "A 5", "A# 5", "B 5", "C 6", "C# 6",
        "D 6", "D# 6", "E 6", "F 6", "F# 6", "G 6", "G# 6", "A 6", "A# 6", "B 6", "C 7",
        "C# 7", "D 7", "D# 7", "E 7", "F 7", "F# 7", "G 7", "G# 7", "A 7", "A# 7", "B 7", "C 8",
        "C# 8", "D 8", "D# 8", "E 8", "F 8", "F# 8", "G 8", "G# 8", "A 8", "A# 8", "B 8", "C 9",
        "C# 9", "D 9", "D# 9", "E 9", "F 9", "F# 9", "G 9"};
    //Array containing ASCII Value to pass to the notation generator
    private String[] ASCIIValueArr = {"C,,,", "^C,,,", "D,,,", "^D,,,", "E,,,", "F,,,", "^F,,,", "G,,,", "^G,,,", "A,,,", "^A,,,", "B,,,",
        "C,,", "^C,,", "D,,", "^D,,", "E,,", "F,,", "^F,,", "G,,", "^G,,", "A,,", "^A,,", "B,,", "C,",
        "^C,", "D,", "^D,", "E,", "F,", "^F,", "G,", "^G,", "A,", "^A,", "B,", "C", "^C",
        "D", "^D", "E", "F", "^F", "G", "^G", "A", "^A", "B", "c", "^c", "d",
        "^d", "e", "f", "^f", "g", "^g", "a", "^a", "b", "c'", "^c'", "d'", "^d'",
        "e'", "f'", "^f'", "g'", "^g'", "a'", "^a'", "b'", "c''", "^c''", "d''", "^d''", "e''",
        "f''", "^f''", "g''", "^g''", "a''", "^a''", "b''", "c'''", "^c'''", "d'''", "^d'''",
        "e'''", "f'''", "^f'''", "g'''", "^g'''", "a'''", "^a'''", "b'''", "c''''",
        "^c''''", "d''''", "^d''''", "e''''", "f''''", "^f''''", "g''''",
        "^g''''", "a''''", "^a''''", "b''''", "c'''''", "^c'''''", "d'''''",
        "^d'''''", "e'''''", "f'''''", "^f'''''", "g'''''", "^g'''''",
        "a'''''", "^a'''''", "b'''''", "c''''''", "^c''''''", "d''''''", "^d''''''",
        "e''''''", "f''''''", "^f''''''", "g''''''"};
    //Hash map for finding MiDI data object according to frequency
    private HashMap<Double, MIDIData> MIDIMap = new HashMap<Double, MIDIData>(127);

    /**
     * Create a MidiFile Creator Instance
     */
    public MIDIFileCreator() {

        //create map between frequencies and mididata having notes and ASCIIValues
        for (int i = 0; i < noteFrequencies.length; i++) {
            MIDIData midiData = new MIDIData();
            midiData.setMidiKey(i);
            midiData.setMIDINote(notesArr[i]);
            midiData.setASCIIValue(ASCIIValueArr[i]);
            MIDIMap.put(noteFrequencies[i], midiData);
        }
    }

    /**
     * set Channel
     * @param channel 
     */
    public void setChannel(int channel) {
        MIDI_CHANNEL = channel;
    }

    /**
     * set Note Velocity
     * @param noteVelocity 
     */
    public void setNoteVelocity(int noteVelocity) {
        velocity = noteVelocity;
    }

    /**
     * set Time Resolution
     * @param timeResolution 
     */
    public void setTimeResolution(int timeResolution) {
        TICS_PER_FRAME = timeResolution;
    }

    /**
     * set Initial Instrument
     * @param initialInstrumentId 
     */
    public void setInitialInstrument(int initialInstrumentId) {
        initialInstrument = initialInstrumentId;
    }

    /**
     * create MIDIFile using MIDI data Array
     * @param input 
     */
    public void createMIDIFile(ArrayList<MIDIData> input) {
        sequence = null;
        try {
            //Sequence.SMPTE 24 means 24 frames per a second. The second parameter means the number of ticks per a frame.
            sequence = new Sequence(Sequence.SMPTE_24, 1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
        /* Track objects cannot be created by invoking their constructor
        directly. Instead, the Sequence object does the job. So we
        obtain the Track there. This links the Track to the Sequence
        automatically. MIDI allows 16 tracks maximum.
         */
        Track track = sequence.createTrack();
        ArrayList<MIDIData> processedData = input;

        //set the instrument
        track.add(changeInstrumentToPlay(initialInstrument, 0));

        for (int i = 0; i < processedData.size(); i++) {
            MIDIData data = processedData.get(i);
            int command = data.getMidiCommand();
            int key = data.getMidiKey();
            long Ticks = data.getTicNumber();
            //create a MIDI event and add it to the track
            track.add(createNoteEvent(command, key, velocity, Ticks));
        }

    }

    /**
     * Now we just save the Sequence to the file we specified.
    The '0' (second parameter) means saving as SMF type 0.
    Since we have only one Track, this is actually the only option
    (type 1 is for multiple tracks).
     * @param fileName
     * @return 
     */
    public boolean writeMidiFileToDisk(String fileName) {

        File outputFile = new File(fileName);
        try {
            int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);
            if (allowedTypes.length == 0) {
                return false;
            } else {
                MidiSystem.write(sequence, allowedTypes[0], outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ;
        return true;
    }

    /**
     * convert Frequency To MIDIData
     * @param frequency
     * @return 
     */
    private MIDIData convertFrequencyToMIDIData(double frequency) {
        MIDIData currentMIDIData = MIDIMap.get(frequency);
        MIDIData MIDIDataToReturn = new MIDIData();
        MIDIDataToReturn.setASCIIValue(currentMIDIData.getACIIValue());
        MIDIDataToReturn.setMIDINote(currentMIDIData.getMIDINote());
        MIDIDataToReturn.setMidiKey(currentMIDIData.getMidiKey());
        MIDIDataToReturn.setFrequency(frequency);
        System.out.println("Frequency = " + frequency + " , note = " + MIDIDataToReturn.getMIDINote());
        return MIDIDataToReturn;
    }

    /**
     * Covert absolute time to Time-tics
     * @param absoluteTime
     * @return 
     */
    private long convertTimeToTicks(double absoluteTime) {

        long timeValueToReturn = (long) ((long) FRAMES_PER_SECOND * TICS_PER_FRAME * absoluteTime);
        return timeValueToReturn;
    }

    /**
     * pre process input [time][frequency] array and create MIDI data list
     * @param input
     * @return 
     */
    public ArrayList<MIDIData> preProcessInput(double[][] input) {
        duration_of_a_segment = SoundIO.Time_Interval_for_Segment_in_Seconds;
        ArrayList outPut = new ArrayList<MIDIData>();
        for (int i = 0; i < input.length ; i++) {
            MIDIData data1 = new MIDIData();
            MIDIData data2 = new MIDIData();

            long timerTicks = convertTimeToTicks(input[i][0]);

            if (i == 0) //for the first frequency value
            {
                data1 = (convertFrequencyToMIDIData(input[i][1]));
                data1.setMidiCommand(KEY_ON);
                data1.setTickNumber(timerTicks);
                data1.setAbsoluteTime(input[i][0]);
                data1.setSegmentNumber(i);
                //System.out.println("@" + (outPut.size() + 1) + " ON_TimerTics = " + timerTicks + " ,absolute time = " + input[i][0]);
                outPut.add(data1);
            } else if (input[i][1] != input[i - 1][1]) //if frequency equal to the previous one do not add note
            {
                data1 = convertFrequencyToMIDIData(input[i][1]);
                data1.setMidiCommand(KEY_ON);
                data1.setTickNumber(timerTicks);
                data1.setAbsoluteTime(input[i][0]);
                data1.setSegmentNumber(i);
                //System.out.println("@" + (outPut.size() + 1) + " ON_TimerTics = " + timerTicks + " ,absolute time = " + input[i][0]);
                outPut.add(data1);
            }

            if (i != input.length - 1) //if frequency is equal to the next one do not close the note
            {
                if (input[i][1] != input[i + 1][1]) {
                    data2 = convertFrequencyToMIDIData(input[i][1]);
                    data2.setMidiCommand(KEY_OFF);
                    timerTicks = convertTimeToTicks(input[i][0] + duration_of_a_segment);
                    data2.setTickNumber(timerTicks);
                    data2.setSegmentNumber(i);
                    data2.setAbsoluteTime(input[i][0] + duration_of_a_segment);
                    System.out.println("@" + (outPut.size() + 1) + " OFF_TimerTics = " + timerTicks + " ,absolute time = " + input[i][0]);
                    outPut.add(data2);
                }
            } else //at the last note close the note anyway
            {
                data2 = convertFrequencyToMIDIData(input[i][1]);
                data2.setMidiCommand(KEY_OFF);
                timerTicks = convertTimeToTicks(input[i][0] + duration_of_a_segment);
                data2.setTickNumber(timerTicks);
                data2.setAbsoluteTime(input[i][0] + duration_of_a_segment);
                data2.setSegmentNumber(i);
                System.out.println("@" + (outPut.size() + 1) + " OFF_TimerTics = " + timerTicks + " ,absolute time = " + input[i][0]);
                outPut.add(data2);
            }

        }
        return outPut;
    }

    /**
     * create MIDIevents according to onsets set
     * input [time][frequency]
     * onsets[time]
     * @param input
     * @param onsets
     * @return 
     */
    public ArrayList<MIDIData> processInputforOnsets(double[][] input, double[] onsets) {

        ArrayList outPut = new ArrayList<MIDIData>();
        //reset notes sequence
        int j = 0;
        double previousMaxFrequency = 8.176;     // related to C-1
        for (int i = 0; i < onsets.length - 1; i++) {
            double noteStartTime = onsets[i];
            double noteEndTime = onsets[i + 1];
            double time = noteStartTime;
            // log starting segment of the note
            int noteStartSegment = j;
            // most common frequency
            double maxFrequency = previousMaxFrequency;
            MIDIData data1 = new MIDIData();
            MIDIData data2 = new MIDIData();

            List<Double> frequency = new ArrayList<Double>();
            List<Integer> counts = new ArrayList<Integer>();
            frequency.add(input[j][1]);
            counts.add(0);
            while (time < noteEndTime) {
                //check if the current frequency matches with any previous frequency
                int column = 0;
                while (frequency.get(column) != input[j][1]) {
                    if ((frequency.size() - 1) == column) {
                        break;
                    } else {
                        column++;
                    }
                }
                //if a match was found increase the count of relevant bin
                if (frequency.get(column) == input[j][1]) {
                    int value = counts.get(column);
                    counts.remove(column);
                    counts.add(column, value + 1);
                    //otherwise add frequency to the frquency list and add count bin
                } else {
                    frequency.add(input[j][1]);
                    counts.add(1);
                }
                time = input[j][0];
                j++;
            }
            // log last segment of the note
            int notesEndSegment = j;
            //bin position in counts array where maximum value is found
            int maxcount = 0;
            //current bin position in counts array
            int currentcount = 0;
            // go through counts array finding the bin position where max value is
            while (currentcount < frequency.size()) {
                if (maxcount < counts.get(currentcount)) {
                    maxcount = counts.get(currentcount);
                    maxFrequency = frequency.get(currentcount);
                }
                currentcount++;
            }
            //log the max frequency. If maxfrequency was not updated from above procedure
            //keep it as previous one. (can happen for notes spanning one segment)
            previousMaxFrequency = maxFrequency;
            long starttimerTicks = convertTimeToTicks(noteStartTime);
            data1 = convertFrequencyToMIDIData(maxFrequency);
            data1.setSegmentNumber(noteStartSegment);
            data1.setMidiCommand(KEY_ON);
            data1.setTickNumber(starttimerTicks);
            data1.setAbsoluteTime(noteStartTime);
            outPut.add(data1);

            long endTimerTics = convertTimeToTicks(noteEndTime);
            data2 = convertFrequencyToMIDIData(maxFrequency);
            data2.setSegmentNumber(notesEndSegment);
            data2.setMidiCommand(KEY_OFF);
            data2.setTickNumber(endTimerTics);
            data2.setAbsoluteTime(noteEndTime);
            outPut.add(data2);

        }
        return outPut;
    }

    private static MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(nCommand, MIDI_CHANNEL, nKey, nVelocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MidiEvent event = new MidiEvent(message, lTick);
        return event;
    }

    /**
     * Create a MIDI event to change the instrument played
     * @param instrumentCode
     * @param timerTics
     * @return 
     */
    private MidiEvent changeInstrumentToPlay(int instrumentCode, long timerTics) {
        MidiEvent event = null;
        try {
            ShortMessage instrumentChange = new ShortMessage();
            instrumentChange.setMessage(ShortMessage.PROGRAM_CHANGE, MIDI_CHANNEL, instrumentCode, 0);
            event = new MidiEvent(instrumentChange, timerTics);
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return event;
    }

    /**
     * Generate the string to be sent to Notation creator
     * @param MidiDataList
     * @return 
     */
    public String getNotesSequence(ArrayList<MIDIData> MidiDataList) {

        ArrayList<Double> Mathraas = getMathraaList(MidiDataList);
        String keySig = getKeySignatureByMIDIDataList(MidiDataList);
        String noteSequence = "K:" + keySig + "\n";
        int i = 0;
        for (int count = 0; count < MidiDataList.size(); count++) {
            MIDIData midiData = MidiDataList.get(count);
            if (midiData.getMidiCommand() == KEY_ON) {
                noteSequence += midiData.getACIIValue();
                double mathrawa = Double.parseDouble(Mathraas.get(i).toString());
                if (mathrawa != 1) {
                    noteSequence += Mathraas.get(i).toString();
                }
                i++;
            }
        }
        return noteSequence;
    }

    /**
     * get Key Signature Using MIDIDataList
     * @param MidiDataList
     * @return 
     */
    public String getKeySignatureByMIDIDataList(ArrayList<MIDIData> MidiDataList) {
        HashMap DurationDataSet = new HashMap();
        ArrayList Durations = new ArrayList();
        ArrayList NoteSet = new ArrayList();

        for (int count = 0; count < MidiDataList.size() - 1; count = count + 2) {
            long duration = MidiDataList.get(count + 1).getTicNumber() - MidiDataList.get(count).getTicNumber();
            Durations.add(count / 2, duration);
            if (DurationDataSet.containsKey(duration)) {
                DurationDataSet.put(duration, Integer.parseInt(DurationDataSet.get(duration).toString()) + 1);
            } else {
                DurationDataSet.put(duration, 1);
            }
            int remainder = MidiDataList.get(count).getMidiKey() % 12;
            if (!NoteSet.contains(remainder)) {
                NoteSet.add(remainder);
            }
        }
        String keySig = this.FindKeySignature(NoteSet);
        return keySig;
    }

    /**
     * Get Tones List (Mathraa is the Sinhalese name)
     * @param MidiDataList
     * @return 
     */
    public ArrayList<Double> getMathraaList(ArrayList<MIDIData> MidiDataList) {
        HashMap DurationDataSet = new HashMap();
        ArrayList Mathraas = new ArrayList();
        ArrayList Durations = new ArrayList();

        Integer max = 0;
        Integer Mod = 0;

        for (int count = 0; count < MidiDataList.size() - 1; count = count + 2) {
            long duration = MidiDataList.get(count + 1).getTicNumber() - MidiDataList.get(count).getTicNumber();
            Durations.add(count / 2, duration);
            if (DurationDataSet.containsKey(duration)) {
                DurationDataSet.put(duration, Integer.parseInt(DurationDataSet.get(duration).toString()) + 1);
            } else {
                DurationDataSet.put(duration, 1);
            }
        }

        Object[] a = DurationDataSet.values().toArray();
        Integer[] values = new Integer[a.length];

        for (int i = 0; i < a.length; i++) {
            values[i] = (Integer) a[i];
            if (max < values[i]) {
                max = values[i];
            }
        }
        Object[] DurationSetkeys = DurationDataSet.keySet().toArray();
        for (int i = 0; i < DurationSetkeys.length; i++) {
            int currentVal = Integer.parseInt(DurationDataSet.get(DurationSetkeys[i]).toString());
            if (currentVal == max) {
                Mod = Integer.parseInt(DurationSetkeys[i].toString());
            }
        }
        for (int i = 0; i < Durations.size(); i++) {
            double ratio = Integer.parseInt(Durations.get(i).toString()) * 100 * 0.01 / Mod;

            //Aproximate time ratios to 1, 2,3 , 4
            int count = 0;
            while (ratio >= count * 100 * 0.01 / 4) {
                count++;
            }

            double upper = (count) * 100 * 0.01 / 4;
            double lower = (count - 1) * 100 * 0.01 / 4;
            double approximatedVal = -1;
            if ((ratio - lower) < (upper - ratio)) {
                approximatedVal = lower;
            } else {
                approximatedVal = upper;
            }
            //if OF-LF<UF-OF LF is returned
            //if UF-OF<OF-LF UF is returned
            //if they are equal UF is returned
            Mathraas.add(i, approximatedVal * 2);
        }

        return Mathraas;
    }
/**
 * Calculates the tempo for the notaion generated
 * @param MidiDataList
 * @return
 */
    public int calculateTempo(ArrayList<MIDIData> MidiDataList){
         int tempo=0;
    double totalNUmberofBeats=0;

     double startTime=MidiDataList.get(0).getAbsoluteTime();
     double endTime=MidiDataList.get(MidiDataList.size()-1).getAbsoluteTime();
        ArrayList beats=this.getMathraaList(MidiDataList);
        for(int i=0;i<beats.size();i++){
            totalNUmberofBeats+=Double.parseDouble(beats.get(i).toString());
        }
          tempo= (int)(totalNUmberofBeats/(endTime-startTime)*60/2);
     return tempo;
    }
    /**
     * Estimate key signature 
     * @param NoteSet
     * @return 
     */
    public String FindKeySignature(ArrayList NoteSet) {
        Integer[] scales = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < NoteSet.size(); i++) {
            int currentNote = Integer.parseInt(NoteSet.get(i).toString());

            scales[(currentNote + 0) % 12] += 1;
            scales[(currentNote + 1) % 12] += 1;
            scales[(currentNote + 3) % 12] += 1;
            scales[(currentNote + 5) % 12] += 1;
            scales[(currentNote + 7) % 12] += 1;
            scales[(currentNote + 8) % 12] += 1;
            scales[(currentNote + 10) % 12] += 1;
        }
        int count = scales[0];
        int max = 0;
        for (int i = 1; i < 12; i++) {
            if (count < scales[i]) {
                count = scales[i];
                max = i;
            }
        }
        String output = "";
        switch (max) {
            case 0:
                output = "C";
                break;

            case 1:
                output = "C#";
                break;
            case 2:
                output = "D";
                break;
            case 3:
                output = "Eb";
                break;
            case 4:
                output = "E";
                break;
            case 5:
                output = "F";
                break;
            case 6:
                output = "F#";
                break;
            case 7:
                output = "G";
                break;
            case 8:
                output = "Ab";
                break;
            case 9:
                output = "A";
                break;
            case 10:
                output = "Bb";
                break;
            case 11:
                output = "B";
                break;

        }
        return output;
    }

    /**
     * get Frames Per Second
     * @return 
     */
    public static int getFramesPerSecond() {
        return FRAMES_PER_SECOND;
    }

    /**
     * get Tics Per Frame
     * @return 
     */
    public static int getTicsPerFrame() {
        return TICS_PER_FRAME;
    }

    /**
     * play MIDI File using java Sound API
     */
    public void playMIDIFile() {
        try {
            // Create a sequencer for the sequence
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            // Start playing
            sequencer.start();
        } catch (InvalidMidiDataException ex) {
        } catch (MidiUnavailableException ex) {
        }
    }

    /**
     * Stop Current playback
     */
    public void stopPlayingMIDIFile() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
        }
    }
    
    /**
     * play the temporaryTrack using same sequencer
     */
    public void playTemporatyTrack()
    {
        try {
        // Create a sequencer for the sequence
        sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(tempSequence);
        // Start playing
        sequencer.start();
        } catch (InvalidMidiDataException ex) {
        } catch (MidiUnavailableException ex) {
        }
    }
    
    /**
     * Create a temporary track to the given instrument and MIDI Data List
     * Need this separately because it takes time to create this. 
     * Should work as soon as instrument or MIDI data Input is changed temporary before play
     * @param instrumentIndex
     * @param input 
     */
    public void createTempTrack(int instrumentIndex, ArrayList<MIDIData>input)
    {
        tempSequence  = null;
        try {
            //Sequence.SMPTE 24 means 24 frames per a second. The second parameter means the number of ticks per a frame.
            tempSequence = new Sequence(Sequence.SMPTE_24, 1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
        /* Track objects cannot be created by invoking their constructor
        directly. Instead, the Sequence object does the job. So we
        obtain the Track there. This links the Track to the Sequence
        automatically. MIDI allows 16 tracks maximum.
         */
        Track track = tempSequence.createTrack();
        ArrayList<MIDIData> processedData = input;

        //set the instrument
        track.add(changeInstrumentToPlay(instrumentIndex, 0));

        for (int i = 0; i < processedData.size(); i++) {
            MIDIData data = processedData.get(i);
            int command = data.getMidiCommand();
            int key = data.getMidiKey();
            long Ticks = data.getTicNumber();
            //create a MIDI event and add it to the track
            track.add(createNoteEvent(command, key, velocity, Ticks));
        }
    }
    
    /**
     * returns initial instrument set by preferences or instrument detector
     * @return 
     */
    public int getInitialInstrument()
    {
        return initialInstrument;
    }
}

/*The following table lists the standard sequence of instruments (timbres) which are specified by General MIDI. The number to the left of the instrument name is the number for the instrument in General MIDI. For programming, you must subtract 1 from these numbers.
Piano Timbres:
1	Acoustic Grand Piano
2	Bright Acoustic Piano
3	Electric Grand Piano
4	Honky-tonk Piano
5	Rhodes Piano
6	Chorused Piano
7	Harpsichord
8	Clavinet
Chromatic Percussion:
9	Celesta
10	Glockenspiel
11	Music Box
12	Vibraphone
13	Marimba
14	Xylophone
15	Tubular Bells
16	Dulcimer
Organ Timbres:
17	Hammond Organ
18	Percussive Organ
19	Rock Organ
20	Church Organ
21	Reed Organ
22	Accordion
23	Harmonica
24	Tango Accordion

Guitar Timbres:
25	Acoustic Nylon Guitar
26	Acoustic Steel Guitar
27	Electric Jazz Guitar
28	Electric Clean Guitar
29	Electric Muted Guitar
30	Overdriven Guitar
31	Distortion Guitar
32	Guitar Harmonics
Bass Timbres:
33	Acoustic Bass
34	Fingered Electric Bass
35	Plucked Electric Bass
36	Fretless Bass
37	Slap Bass 1
38	Slap Bass 2
39	Synth Bass 1
40	Synth Bass 2
String Timbres:
41	Violin
42	Viola
43	Cello
44	Contrabass
45	Tremolo Strings
46	Pizzicato Strings
47	Orchestral Harp
48	Timpani

Ensemble Timbres:
49	String Ensemble 1
50	String Ensemble 2
51	Synth Strings 1
52	Synth Strings 2
53	Choir "Aah"
54	Choir "Ooh"
55	Synth Voice
56	Orchestral Hit
Brass Timbres:
57	Trumpet
58	Trombone
59	Tuba
60	Muted Trumpet
61	French Horn
62	Brass Section
63	Synth Brass 1
64	Synth Brass 2
Reed Timbres:
65	Soprano Sax
66	Alto Sax
67	Tenor Sax
68	Baritone Sax
69	Oboe
70	English Horn
71	Bassoon
72	Clarinet

Pipe Timbres:
73	Piccolo
74	Flute
75	Recorder
76	Pan Flute
77	Bottle Blow
78	Shakuhachi
79	Whistle
80	Ocarina
Synth Lead:
81	Square Wave Lead
82	Sawtooth Wave Lead
83	Calliope Lead
84	Chiff Lead
85	Charang Lead
86	Voice Lead
87	Fifths Lead
88	Bass Lead
Synth Pad:
89	New Age Pad
90	Warm Pad
91	Polysynth Pad
92	Choir Pad
93	Bowed Pad
94	Metallic Pad
95	Halo Pad
96	Sweep Pad

Synth Effects:
97	Rain Effect
98	Soundtrack Effect
99	Crystal Effect
100	Atmosphere Effect
101	Brightness Effect
102	Goblins Effect
103	Echoes Effect
104	Sci-Fi Effect
Ethnic Timbres:
105	Sitar
106	Banjo
107	Shamisen
108	Koto
109	Kalimba
110	Bagpipe
111	Fiddle
112	Shanai
Sound Effects:
113	Tinkle Bell
114	Agogo
115	Steel Drums
116	Woodblock
117	Taiko Drum
118	Melodic Tom
119	Synth Drum
120	Reverse Cymbal

Sound Effects:
121	Guitar Fret Noise
122	Breath Noise
123	Seashore
124	Bird Tweet

125	Telephone Ring
126	Helicopter
127	Applause
128	Gun Shot
Instruments can be selected by using the MIDI command byte (0xc0) followed by a data byte which is one of the above numbers (minus 1).*/
