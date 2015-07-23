/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package notesdetectingapplication.InstrumentClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JTextArea;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RequiredImprovementStrategy;
import org.encog.persist.EncogPersistedCollection;

/**
 *
 * @author thilanka
 */
public class NeuralNetwork {

    //Create a basic neural network
    public BasicNetwork network = new BasicNetwork();
    public String FILENAME = "Miyaesi_neural.eg";
    File training_file = new File("Training_data.csv");
    int input_neurons = 5;
    int hidden_neurons = 100;
    int output_neurons = 2;
    String dir;
    public NeuralNetwork(String dir)
    {
    	this.dir=dir;
    	FILENAME=dir+FILENAME;
    	training_file=new File(dir+"Training_data.csv");
    }

    /**
     * Creat a basic neural network accoding to the data
     */
    public void createNN(){
        //Creating the layers for the NN
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,input_neurons));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,hidden_neurons));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,output_neurons));
        network.setLogic(new FeedforwardLogic());

        network.getStructure().finalizeStructure();
        //Randomize the weights
        network.reset();
        //loggingPanel.append("The neural network sucessfully created\n");
    }

    /**
     * Train the newly created neural network with given training data
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void TrainNN() throws FileNotFoundException, IOException{
        //loggingPanel.append("Training started\n");
        // read the training data from file
        ArrayList list = getTrainingDataFromFile(input_neurons,output_neurons);
        double[][] input = (double[][]) list.get(0);
        double[][] ideal = (double[][]) list.get(1);
        // assign them to a neural dataset
        NeuralDataSet trainingSet = new BasicNeuralDataSet(input,ideal);
        // set the training technique
        final Train train = new ResilientPropagation(network, trainingSet);
        train.addStrategy(new RequiredImprovementStrategy(5));
        int epoch = 1;
        do {
            //train the network
            train.iteration();
            //loggingPanel.append("Epoch # " +epoch +" Error : " + train.getError()+"\n");
            epoch++;
        } while(train.getError() > 0.1);
        final EncogPersistedCollection encog = new EncogPersistedCollection(FILENAME); // An Encog PersistedCollection object is created with the specified
        encog.create(); // create a new Encog EG file
        // add the trained network to the file
        encog.add("network", network);
        //loggingPanel.append("Training Finished\n");
    }

    /**
     * Query the trained network with data
     * @param query_data validation data
     * @return
     */
    public String queryNetwork(double [][] query_data) {
        int guitar_count = 0;
        int piano_count = 0;
        int violin_count = 0;
        int flute_count = 0;
        int onset_count =0;
        final EncogPersistedCollection encog =
                new EncogPersistedCollection(FILENAME);
        // Load the saved network to the program
        BasicNetwork testNetwork = (BasicNetwork) encog.find("hundradneurons");
        for (int i=0;i<query_data.length;i++) {
            NeuralData neuralDataInput = new BasicNeuralData(query_data[i]);
            // Get the result
            NeuralData output = testNetwork.compute(neuralDataInput);
            // identify the each instrument
            if (output.getData(0) <= 0.5 && output.getData(1) <= 0.5) {
                //loggingPanel.append("Onset Data @ "+onset_count+" :classified to - Flute\n");
                flute_count++;
            } else if (output.getData(0) <= 0.5 && output.getData(1) >= 0.5) {
                //loggingPanel.append("Onset Data @ "+onset_count+" :classified to - Violin\n");
                violin_count++;
            } else if (output.getData(0) >= 0.5 && output.getData(1) <= 0.5) {
                //loggingPanel.append("Onset Data @ "+onset_count+" :classified to - Guitar\n");
                guitar_count++;
            } else if (output.getData(0) >= 0.5 && output.getData(1) >= 0.5) {
                //loggingPanel.append("Onset Data @ "+onset_count+" :classified to - Piano\n");
                piano_count++;
            }
            onset_count++;
        }
//        loggingPanel.append("Classification finished successfully\n");
//        loggingPanel.append("Flute Classified instances = "+flute_count+"\n");
//        loggingPanel.append("Violin Classified instances = "+violin_count+"\n");
//        loggingPanel.append("Guitar Classified instances = "+guitar_count+"\n");
//        loggingPanel.append("Piano Classified instances = "+piano_count+"\n");
        String instrument = "Unkown";
        if ((flute_count + violin_count) > (piano_count + guitar_count)) {
            if(flute_count > violin_count) {
                instrument = "Flute";
            } else {
                instrument = "Violin";
            }
        } else {
            if(piano_count > guitar_count) {
                instrument = "Piano";
            } else {
                instrument = "Guitar";
            }
        }
        //loggingPanel.append("Dominent instrument belongs to: "+instrument+"\n");
        return instrument;
    }

    /**
     * Read the data from a external cvs file
     * @param input_size input neuron size
     * @param ideal_size output neuron size
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList getTrainingDataFromFile(int input_size,int ideal_size) throws FileNotFoundException, IOException {
        //loggingPanel.append("Reading the training file\n");
        ArrayList<ArrayList> training_data = new ArrayList();
        //String [][] numbers = new String [24][24];

	
	BufferedReader bufRdr  = new BufferedReader(new FileReader(training_file));
	String line = null;
	
	//read each line of text file
	while((line = bufRdr.readLine()) != null )
	{
            ArrayList temp_list = new ArrayList();
            StringTokenizer st = new StringTokenizer(line,",");
            while (st.hasMoreTokens())
            {
		//get next token and store it in the array
		temp_list.add(Double.parseDouble(st.nextToken()));
            }
            training_data.add(temp_list);
	}
        double [][] input = new double[training_data.size()][input_size];
        double [][] ideal = new double[training_data.size()][ideal_size];
        ArrayList list = new ArrayList();
        for (int i=0; i < training_data.size(); i++) {
            for (int j=0; j < (input_size+ideal_size); j++) {
                if (j < input_size) {
                    input[i][j] = Double.parseDouble(training_data.get(i).get(j).toString());
                } else {
                    ideal[i][j-input_size] = Double.parseDouble(training_data.get(i).get(j).toString());
                }
            }

        }
        list.add(input);
        list.add(ideal);
       return list;
    }

    /**
     * Get trained data from an array
     * @return
     */
    public ArrayList getTrainingData() {
        ArrayList list = new ArrayList();
        double trainingData[][] = {
            { 10.638297872340443,
              0.07002342714588843,0.17071641646864866,0.502291264198929,1.0},
            { 2.564102564102563,
                      3.198374923771387,0.04015837018258223,2.8440290437381854,1.0},
            {  8.51063829787234,
                       -0.00815236037116397,-0.00808357193817059,0.9864789949817705,1.0},
            { 20.512820512820493,
                       -1.0370446447630923,0.015760331846752674,2.8396010033351478,1.0},
            {10.256410256410255,
                       -0.6100464062738781,0.010394134858427002,0.9867220643375071,1.0},
            {5.747126436781606,
                       -0.17068949159740987,0.00896120266255417,0.9856862431265887,1.0},
            {55.172413793103516,
                       -0.4428323306780588,0.026398697210293278,0.959363731506823,1.0},
            {2.564102564102564,
                       -2.3351504921820756,0.02563271716625543,2.3758990809738734,1.0},
            {6.382978723404255,
                       0.19771882841972338,-0.008143044973718584,0.9860071533620426,1.0},
            {28.57142857142857,
                       -0.10077355236368571,0,0.9612168606161621,1.0},
            {58.97435897435898,
                       0,0.16773843021629758,4.742948580702077,1.0},
            {17.948717948717988,
                       2.2610148364353657,0.005524801619472078,1.964502088826216,1.0},
            {58.620689655172384,
                       0.20456118027671893,0.08746939944507715,2.3999765666382857,1.0},
            {2.564102564102564,
                       -1.648455431826763,0.0026699668076246076,1.9054471412484946,1.0},
            {24.137931034482747,
                       0.24192955003260092,0.008353792335079397,1.9584324580643568,1.0},
            {95.74468085106392,
                       0.6094284912884214,0,3.278643294135046,1.0},
            {58.97435897435898,
                       0,0.3456964210968072,4.257441608353091,1.0},
            {7.6923076923076925,
                       1.300467849963718,0.016848589567871955,2.337840483443434,1.0},
            {72.34042553191489,
                       -0.12356659576865785,0.08573632439480788,0.9975402630125048,0.0},
            {61.53846153846154,
                       0.011696595332398597,0.1431662862732005,2.468645049322984,0.0},
            {41.02564102564102,
                       -0.019795332058744494,0.029911836378445328,1.4547514038729883,0.0},
            {51.063829787234084,
                       -0.06595031626611271,-6.895559562910287E-4,1.9481423132011297,0.0},
            {51.282051282051235,
                       -0.03851387826316188,0.016085541700535763,2.4411840696645397,0.0},
            {57.446808510638306,
                       -0.07902517149454659,0.014280260608133396,2.4194002957542255,0.0},
            {87.34177215189874,
                       -0.013947692642734585,0.16348497023917385,1.4555780913770877,0.0},
            {48.93617021276588,
                       -0.09104795725886755,0.0027223590110176128,2.0104150070365927,0.0},
            {61.53846153846154,
                       0,0.021788502184069712,2.469271496710862,0.0},
            {75.86206896551724,
                       3.79642146496832E-4,0.011215585765324375,2.430578298146025,0.0}
             };
        double INPUT[][] = new double[trainingData.length][4];
        double IDEAL[][] = new double[trainingData.length][1];
        for (int i=0; i < trainingData.length; i++) {
            for (int j=0; j < 5; j++) {
                if (j < 4) {
                    INPUT[i][j] = trainingData[i][j];
                } else {
                    IDEAL[i][j-4] = trainingData[i][j];
                }
            }

        }
        list.add(INPUT);
        list.add(IDEAL);
       return list;
    }


}
