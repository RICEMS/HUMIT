/*************************************************************************
 *  Compilation:  javac SoundIO.java
 *  Execution:    java SoundAudio
 *
 *  Simple library for reading, writing, and manipulating .wav files.

 *
 *  Limitations
 *  -----------
 *    - Assumes the audio is monaural, with sampling rate of 44,100.
 *
 *************************************************************************/
package notesdetectingapplication;

import java.io.*;
import javax.sound.sampled.*;
import java.net.*;
import java.applet.*;
import javax.swing.JOptionPane;
import org.jfree.data.xy.XYSeries;

/**
 *  <i>Standard audio</i>. This class provides a basic capability for
 *  creating, reading, and saving audio.
 *  <p>
 *  The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit, monaural.
 *
 *  <p>
 *  For additional documentation, see <a href="http://www.cs.princeton.edu/introcs/15inout">Section 1.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public class SoundIO {

    //The sample rate - 44,100 Hz for CD quality audio.
    public static int SAMPLE_RATE = 44100;
    private static int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
    private static int SAMPLE_BUFFER_SIZE = 4096;
    private static int SEGMENT_SIZE = 16384;                // can be changed according to tempo value(default 16384)
    private static float Sample_Sound_Length;
    private static int Num_Of_EquidistantPoint;
    private static double Time_Between_Two_Bytes;
    private static int num_of_channels;
    private static int frame_size;
    public static double Time_Interval_for_Segment_in_Seconds;  //this is also used in midi file creator
    private static boolean isBigEndian;
    private static SourceDataLine line;   // to play the sound
    private static byte[] buffer;         // our internal buffer
    private static int i = 0;             // number of samples currently in internal buffer
    private static AudioInputStream linearStream = null;
    //Object that will play the audio input stream of the sound
    private Clip clip;

    public SoundIO() {
        try {
            // 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

            // the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
            // it gets divided because we can't expect the buffered data to line up exactly with when
            // the sound card decides to push out its samples.
            buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE / 3];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // no sound gets made before this call
        line.start();
    }

    /**
     * set to CD Quality adjusting attributes
     */
    private void ResetToCDQuality() {
        SAMPLE_RATE = 44100;
        BYTES_PER_SAMPLE = 2;
        MAX_16_BIT = Short.MAX_VALUE;
        SAMPLE_BUFFER_SIZE = 4096;
    }

    /**
     * Close standard audio.
     */
    public void close() {
        line.drain();
        line.stop();
    }

    /**
     * Write one sample (between -1.0 and +1.0) to standard audio. If the sample
     * is outside the range, it will be clipped.
     */
    public void play(double in) {

        // clip if outside [-1, +1]
        if (in < -1.0) {
            in = -1.0;
        }
        if (in > +1.0) {
            in = +1.0;
        }

        // convert to bytes
        short s = (short) (MAX_16_BIT * in);
        buffer[i++] = (byte) s;
        buffer[i++] = (byte) (s >> 8);   // little Endian

        // send to sound card if buffer is full
        if (i >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            i = 0;
        }
    }

    /**
     * Write an array of samples (between -1.0 and +1.0) to standard audio. If a sample
     * is outside the range, it will be clipped.
     */
    public void play(double[] input) {
        for (int i = 0; i < input.length; i++) {
            play(input[i]);
        }
    }

    /**
     * Read audio samples from a file (in .wav or .au format) and return them as a double array
     * with values between -1.0 and +1.0.
     */
    public double[] read(String filename) {
        byte[] data = readByte(filename);
        int N = data.length;
        double[] d = new double[N / 2];
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((data[2 * i + 1] & 0xFF) << 8) + (data[2 * i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }

    /**
     * Play a sound file (in .wav or .au format) in a background thread.
     */
    public void play(String fileName, double volume, float sampleRate) {
        //Load the sound file
        File soundFile = new File(fileName);

        //Object that will hold the audio input stream for the sound
        Object currentSound = null;

        //Object that contains format information about the audio input stream
        AudioFormat format;

        //Load the audio input stream
        try {
            currentSound = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e1) {
            System.out.println("Error loading file");
        }

        try {
            //Get the format information from the Audio Input Stream
            AudioInputStream stream = (AudioInputStream) currentSound;
            format = stream.getFormat();

            //Get information about the line
            DataLine.Info info = new DataLine.Info(Clip.class,
                    stream.getFormat(),
                    ((int) stream.getFrameLength() * format.getFrameSize()));

            //Load clip information from the line information
            clip = (Clip) AudioSystem.getLine(info);

            //Write the stream onto the clip
            clip.open(stream);
            //specify a new sound level from 0 to 100
            double gain = volume;

            //create the gain control object using the clip object from the code above
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            //convert the given sound level to decibel
            gain = gain / 100;
            float dB = (float) (Math.log(gain == 0.0 ? 0.0001 : gain) / Math.log(10.0) * 20.0);

            //change the sound level
            control.setValue(dB);


            //PLAY THE PROCESSED SOUND************************************************************************************************************

            //make the current sound the clip
            currentSound = clip;

            //start the clip
            clip.start();

            //loop the clip continuously
            //clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception ex) {
            ex.printStackTrace();
            currentSound = null;
        }
    }

    /**
     * Stop playing sound
     */
    public void stopPlaying() {
        if (clip != null && clip.isOpen()) {
            clip.close();
        }
    }

    /**
     * Loop a sound file (in .wav or .au format) in a background thread.
     */
    public void loop(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) {
                url = file.toURI().toURL();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) {
            throw new RuntimeException("audio " + filename + " not found");
        }
        AudioClip clip = Applet.newAudioClip(url);
        clip.loop();
    }

    /**
     * return sound data as a byte array
     * @param filename
     * @return 
     */
    public byte[] readByte(String filename) {
        byte[] data = null;
        try {
            File inputFile = new File(filename);
            linearStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat linearFormat = linearStream.getFormat();

            SAMPLE_RATE = (int) linearFormat.getSampleRate(); //Calculate the sample rate

            frame_size = linearFormat.getFrameSize();

            BITS_PER_SAMPLE = linearFormat.getSampleSizeInBits();

            //Calculate the length in seconds of the sample
            Sample_Sound_Length = linearStream.getFrameLength() / linearFormat.getFrameRate();

            Num_Of_EquidistantPoint = (int) (Sample_Sound_Length * SAMPLE_RATE) / 2;

            /**
             * time for a frame(sec) = 1/frame rate
             * time for a byte = 1/frame rate*num of bytes for a frame
             * time for a segmentArr = segmentArr size in bytes/frame rate*num of bytes for a segmentArr
             */
            Time_Interval_for_Segment_in_Seconds = 1 * SEGMENT_SIZE / (linearFormat.getFrameRate() * frame_size);

            /**
             * Sample rate  = 44100Hz
             * Bytes per sample = 2
             * Bytes played per a second = 44100*2
             * Time Between Two Bytes = 1/88200
             * Time_Between_Two_Bytes = 1/(SAMPLE_RATE*BYTES_PER_SAMPLE);
             */
            Time_Between_Two_Bytes = 0.5667E-5;

            num_of_channels = linearFormat.getChannels();

            isBigEndian = linearFormat.isBigEndian();

            data = new byte[linearStream.available()];
            linearStream.read(data);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }
        return data;
    }

    /**
     * Get Sample Rate
     * @return 
     */
    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    /**
     * Get Frame Size
     * @return 
     */
    public int getFrameSize() {
        return frame_size;
    }

    /**
     * Get Bits Per Sample
     * @return 
     */
    public int getBitsPerSample() {
        return BITS_PER_SAMPLE;
    }

    /**
     * Get Sample Sound Length
     * @return 
     */
    public float getSampleSoundLength() {
        return Sample_Sound_Length;
    }

    /**
     * Get Number of Equisident Points
     * @return 
     */
    public int getNumofEquisidentPoints() {
        return Num_Of_EquidistantPoint;
    }

    /**
     * Get Time Between Two Bytes In Seconds
     * @return 
     */
    public double getTimeBetweenTwoBytesInSeconds() {
        return Time_Between_Two_Bytes;
    }

    /**
     * Get Time Interval For Segment
     * @return 
     */
    public double getTimeIntervalForSegment() {
        return Time_Interval_for_Segment_in_Seconds;
    }

    /**
     * Get if Mono or Stereo
     * @return 
     */
    public String getMonoStereo() {
        if (num_of_channels == 1) {
            return "Mono";
        }
        if (num_of_channels == 2) {
            return "Stereo";
        } else {
            return "Cannot be Detected";
        }
    }

    /**
     * Get Endian Format
     * @return 
     */
    public String getEndianFormat() {
        if (isBigEndian == true) {
            return "Stored in Big Endian";
        } else {
            return "Stored in Little Endian";
        }
    }

    /**
     * Save the double array as a sound file (using .wav or .au format).
     * @param filename
     * @param input 
     */
    public void save(String filename, double[] input) {

        // assumes 44,100 samples per second
        // use 16-bit audio, stereo, signed PCM, little Endian
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        byte[] data = new byte[2 * input.length];
        for (int i = 0; i < input.length; i++) {
            int temp = (short) (input[i] * MAX_16_BIT);
            data[2 * i + 0] = (byte) temp;
            data[2 * i + 1] = (byte) (temp >> 8);
        }

        // now save the file
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream ais = new AudioInputStream(bais, format, input.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            } else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
            } else {
                throw new RuntimeException("File format not supported: " + filename);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     * Get frequencies in a two dimensional array
     * @param bytesOfFile
     * @param preferedWindow
     * @return 
     */
    public double[][] getFrequencies(byte[] bytesOfFile, int preferedWindow) {

        byte[] segmentArr = new byte[SEGMENT_SIZE];
        int numberOfSegments = (int) (bytesOfFile.length / SEGMENT_SIZE);
        int h = 0;
        double timeValue;
        double approximatedNoteFreq;
        double[][] output = new double[numberOfSegments][2];
        FrequencyMatcher frequencyMatcher = new FrequencyMatcher();


        //segmentation and processing
        for (int j = 0; j < numberOfSegments; j++) {
            timeValue = Time_Interval_for_Segment_in_Seconds * j;

            //Read the relevant segmentArr from full byte array
            for (int i = 0; i < segmentArr.length; i++) {
                segmentArr[i] = bytesOfFile[h + i];
            }

            int N = segmentArr.length;
            double[] data = new double[N / 2];

            /*Convert byte values to short values. Usually to represent value
            of a fraction of time two bytes are used. */
            for (int i = 0; i < N / 2; i++) {
                data[i] = ((short) (((segmentArr[2 * i + 1] & 0xFF) << 8) + (segmentArr[2 * i] & 0xFF))) / ((double) MAX_16_BIT);
            }

            //apply a suitable windowing function to reduce frequecy distortion
            applyWindowing(preferedWindow, data);

            //get decibel values to an array applying a FFT for each segmentArr
            double[] fftDecibelArr = FourierTransformer.FFT(data);

            double min = Double.MAX_VALUE;
            double minHz = 0;
            double max = Double.MIN_VALUE;
            double maxHz = 0;
            int samples = SAMPLE_RATE;

            /*scale value to get frequncies in Hz
            finding the array index of the peak point*/
            double scaleHz = (double) (samples / 2) / (double) fftDecibelArr.length;


            //find frequencies w:r:t mimnimum and maximum decibel values
            for (int x = 0; x < fftDecibelArr.length; x++) {
                double amplitude = fftDecibelArr[x];
                if (min > amplitude) {
                    min = amplitude;
                    minHz = (double) x * scaleHz;
                }

                if (max < amplitude) {
                    max = amplitude;
                    maxHz = (double) x * scaleHz;
                }

            }

            //approximate the found frequency to nearest standard frequency
            approximatedNoteFreq = frequencyMatcher.matchFrequency(maxHz);

            output[j][0] = timeValue;
            output[j][1] = approximatedNoteFreq;

            //increment to the next segmentArr
            h += segmentArr.length;
        }
        return output;
    }

    /**
     * get Power Spectrum Between Given Segments
     * @param startSegment
     * @param endSegment
     * @param preferedWindow
     * @param bytesOfFile
     * @return 
     */
    public XYSeries getPowerSpectrumBetweenSegments(int startSegment, int endSegment, int preferedWindow, byte[] bytesOfFile) {
        XYSeries xySeries = new XYSeries("Power Spectrum");
        byte[] segmentArr = new byte[SEGMENT_SIZE * (endSegment - startSegment)];
        //read bytes from file
        int count = 0;
        for (int h = SEGMENT_SIZE * startSegment; h < SEGMENT_SIZE * endSegment; h++) {
            segmentArr[count] = bytesOfFile[h];
            count++;
        }

        int N = segmentArr.length;
        double[] data = new double[N / 2];

        /*Convert byte values to short values. Usually to represent value
        of a fraction of time two bytes are used. */
        for (int i = 0; i < N / 2; i++) {
            data[i] = ((short) (((segmentArr[2 * i + 1] & 0xFF) << 8) + (segmentArr[2 * i] & 0xFF))) / ((double) MAX_16_BIT);
        }

        //apply a suitable windowing function to reduce frequecy distortion
        applyWindowing(preferedWindow, data);

        //get decibel values to an array applying a FFT for each segmentArr
        double[] fftDecibelArr = FourierTransformer.FFT(data);
        int samples = SAMPLE_RATE;

        /*scale value to get frequncies in Hz
        finding the array index of the peak point*/
        double scaleHz = (double) (samples / 2) / (double) fftDecibelArr.length;

        //add frequencies and decibel values
        for (int x = 0; x < fftDecibelArr.length; x++) {
            double amplitude = fftDecibelArr[x];
            double frequency = (double) x * scaleHz;
            xySeries.add(frequency, amplitude);
        }

        return xySeries;
    }

    /**
     * Get Segement Data in two dimensional array [decibel value][frequency value]
     * @param startSegment
     * @param endSegment
     * @param preferedWindow
     * @param bytesOfFile
     * @return 
     */
    public double[][] returnSegementData(int startSegment, int endSegment, int preferedWindow, byte[] bytesOfFile) {
        byte[] segmentArr = new byte[SEGMENT_SIZE * (endSegment - startSegment)];
        double[][] segmentData;
        //read bytes from file
        int count = 0;
        for (int h = SEGMENT_SIZE * startSegment; h < SEGMENT_SIZE * endSegment; h++) {
            segmentArr[count] = bytesOfFile[h];
            count++;
        }

        int N = segmentArr.length;
        double[] data = new double[N / 2];

        /*Convert byte values to short values. Usually to represent value
        of a fraction of time two bytes are used. */
        for (int i = 0; i < N / 2; i++) {
            data[i] = ((short) (((segmentArr[2 * i + 1] & 0xFF) << 8) + (segmentArr[2 * i] & 0xFF))) / ((double) MAX_16_BIT);
        }

        //apply a suitable windowing function to reduce frequecy distortion
        applyWindowing(preferedWindow, data);

        //get decibel values to an array applying a FFT for each segmentArr
        double[] fftDecibelArr = FourierTransformer.FFT(data);
        int samples = SAMPLE_RATE;

        /*scale value to get frequncies in Hz
        finding the array index of the peak point*/
        double scaleHz = (double) (samples / 2) / (double) fftDecibelArr.length;

        segmentData = new double[2][fftDecibelArr.length];
        //add frequencies and decibel values
        for (int x = 0; x < fftDecibelArr.length; x++) {
            segmentData[0][x] = fftDecibelArr[x];
            segmentData[1][x] = (double) x * scaleHz;
        }

        return segmentData;
    }

    /**
     * perform windowing on given segment
     * @param preferedWindow
     * @param segment 
     */
    private void applyWindowing(int preferedWindow, double[] segment) {
        switch (preferedWindow) {
            case 0:
                applyHanningWindow(segment);
                break;
            case 1:
                applyHammingWindow(segment);
                break;
            case 2:
                applyBlackmanHarrisWindow(segment);
        }
    }

    /**
     * Create a note of given frequency, duration and volume
     * @param hz
     * @param duration
     * @param amplitude
     * @return 
     */
    public double[] note(double hz, double duration, double amplitude) {
        int N = (int) (SAMPLE_RATE * duration);
        double[] a = new double[N + 1];
        for (int i = 0; i <= N; i++) {
            a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / SAMPLE_RATE);
        }
        return a;
    }

    /**
     * Return the number of samples per a channel
     * @return 
     */
    public long getSampleCount() {
        long total = (linearStream.getFrameLength()
                * frame_size * 8) / BITS_PER_SAMPLE;
        return total / num_of_channels;
    }

    /**
     * remove voice part from the audio bytes
     * @param audioBytes
     * @return 
     */
    public double[] removeVoice(byte[] audioBytes) {
        return decodeBytesAndRemoveVoice(audioBytes);
    }

    /**
     * windowing generally improve the sensitivity of FFT spectral-analysis techniques.
     * It would minimize leakage into the adjacent FFT bins
     * (reduced side lobes). Refer
     * http://www.tmworld.com/article/322450-Windowing_Functions_Improve_FFT_Results_Part_I.php
     * for details
     * @param segment 
     */
    private void applyHanningWindow(double[] segment) {

        for (int i = 0; i < segment.length; i++) {
            segment[i] = segment[i] * (0.5 - 0.5 * Math.cos(2 * Math.PI * i / segment.length));
        }
    }

    private void applyHammingWindow(double[] segment) {

        for (int i = 0; i < segment.length; i++) {
            segment[i] = segment[i] * (0.54 - 0.46 * Math.cos(2 * Math.PI * i / segment.length));
        }
    }

    private void applyBlackmanHarrisWindow(double[] segment) {

        for (int i = 0; i < segment.length; i++) {
            segment[i] = segment[i] * (0.355768 - 0.487396 * Math.cos(2 * Math.PI * i / segment.length)
                    + 0.144232 * Math.cos(4 * Math.PI * i / segment.length) - 0.012604 * Math.cos(6 * Math.PI * i / segment.length));
        }
    }

    /**
     * Decode bytes of audioBytes into audioSamples and at the same time
     * remove voice using center pan removal. This is done together to prevent
     * using excessive amount of memory.
     * @param audioBytes
     * @return 
     */
    private double[] decodeBytesAndRemoveVoice(byte[] audioBytes) {

        int sampleSizeInBytes = BITS_PER_SAMPLE / 8;
        int[] sampleBytes = new int[sampleSizeInBytes];
        double[] output = new double[(int) getSampleCount() * sampleSizeInBytes];
        double[] tempLeftRightData = new double[sampleSizeInBytes];
        int k = 0; // index in audioBytes
        int h = 0; // index output values
        try {
            for (int r = 0; r < getSampleCount(); r++) {
                for (int i = 0; i < tempLeftRightData.length; i++) {
                    // collect sample byte in big-endian order
                    if (isBigEndian) {
                        // bytes start with MSB
                        for (int j = 0; j < sampleSizeInBytes; j++) {
                            sampleBytes[j] = audioBytes[k++];
                        }
                    } else {
                        // bytes start with LSB
                        for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                            sampleBytes[j] = audioBytes[k++];
                            if (sampleBytes[j] != 0) {
                                j = j + 0;
                            }
                        }
                    }
                    // get integer value from bytes
                    int ival = 0;
                    for (int j = 0; j < sampleSizeInBytes; j++) {
                        if (j < sampleSizeInBytes - 1) {
                            ival += sampleBytes[j] << 8;
                        } else {
                            ival += sampleBytes[j];
                        }
                    }
                    // decode value
                    //double ratio = Math.pow(2., BITS_PER_SAMPLE - 1);
                    double val = ((double) ival) / ((double) MAX_16_BIT);
                    tempLeftRightData[i] = val;
                }
                //reduce leftdata(even) from rightdata (odd)to remove voice
                output[h] = tempLeftRightData[0] - tempLeftRightData[1];
                h++;
                output[h] = tempLeftRightData[0] - tempLeftRightData[1];
                h++;
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot Remove Vocals of MONO inputs");
        }
        return output;
    }
}
