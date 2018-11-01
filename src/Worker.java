import Frameworks.FPFramework;
import Frameworks.Framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker {
    //private static int[] neuronNum;
    private static FPFramework fpFramework;
    //private static ValueNode in;

    public static void main(String[] args) {
        Constants.readConfig();

        try {
            int WorkerPort=Integer.parseInt(Constants.configs.get("WorkerPort"));
            ServerSocket serverWithMaster = new ServerSocket(WorkerPort);

            while (true) {
                System.out.println("Worker waiting for Master...");
                Socket socketWithMaster = serverWithMaster.accept();
                System.out.println("Master is here!");
                ObjectInputStream inputStreamWithMaster = new ObjectInputStream(socketWithMaster.getInputStream());

                fpFramework = (FPFramework) (inputStreamWithMaster.readObject());
                ArrayList<double[][]> inList = (ArrayList<double[][]>) (inputStreamWithMaster.readObject());
                ArrayList<double[][]> outList = (ArrayList<double[][]>) (inputStreamWithMaster.readObject());
                double[][] queryIn=(double[][])(inputStreamWithMaster.readObject());

                System.out.println("Worker reading finished.");

                System.out.println("Start computing!");

                String PS2WorkerAddr=Constants.configs.get("PS2WorkerAddr");
                int PS2WorkerPort=Integer.parseInt(Constants.configs.get("PS2WorkerPort"));
                for (int k = 0; k < Constants.TRAIN_TIMES; k++) {
                    batchCompute(inList, PS2WorkerAddr, PS2WorkerPort);
                   /* double correct=validate(inList, outList);
                    System.out.println(k + " times finished. correct " + correct);*/
                }

                double[][] queryOut=computeOut(queryIn);

                ObjectOutputStream outputStreamWithMaster = new ObjectOutputStream(socketWithMaster.getOutputStream());
                outputStreamWithMaster.writeObject(queryOut);
                System.out.println("Worker responding finished.");

                closePS(PS2WorkerAddr,  PS2WorkerPort);
                System.out.println("Worker is resting now.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("There is some problem.");
        }
    }

    public static void fetchParameter(String host, int port) throws IOException, ClassNotFoundException {
        Socket socketWithPS=new Socket(host, port);

        ObjectOutputStream streamToPS=new ObjectOutputStream(socketWithPS.getOutputStream());
        streamToPS.writeObject(Constants.KEEP_ALIVE);
        streamToPS.writeObject(Constants.WORKER_REQUIRE_PARAMETER);
        streamToPS.flush();

        ObjectInputStream streamFromPS=new ObjectInputStream(socketWithPS.getInputStream());
        ArrayList<double[][]> parameters=(ArrayList)(streamFromPS.readObject());
        fpFramework.readParameters(parameters);
        socketWithPS.close();
    }

    public static void sendParameter(String host, int port, ArrayList<Integer> dataIndies, ArrayList<ArrayList<double[][]>> sendValues) throws IOException {
        Socket socketWithPS=new Socket(host, port);

        ObjectOutputStream streamToPS=new ObjectOutputStream(socketWithPS.getOutputStream());

        streamToPS.writeObject(Constants.KEEP_ALIVE);
        streamToPS.writeObject(Constants.WORKER_SEND_PARAMETER);
        streamToPS.writeObject(dataIndies);
        streamToPS.writeObject(sendValues);

        streamToPS.flush();
        socketWithPS.close();
    }

    public static void closePS(String host, int port) throws IOException {
            Socket closePSSocket = new Socket(host, port);
            ObjectOutputStream closeToPS = new ObjectOutputStream(closePSSocket.getOutputStream());
            closeToPS.writeObject(Constants.CLOSE_PS);
            closePSSocket.close();
    }

    public static void batchCompute(ArrayList<double[][]> inList, String host, int port) throws IOException, ClassNotFoundException {
        ArrayList<ArrayList<double[][]>> sendValues = new ArrayList<>();
        ArrayList<Integer> indies = new ArrayList<>();

        fetchParameter(host, port);

        for (int i = 0; i < Constants.BATCH_SIZE; i++) {
            int s = (int) (Math.random() * (inList.size() - Constants.NUM_VALIDATE));
            indies.add(s);
            fpFramework.getIn().setValue(inList.get(s));
            fpFramework.computeActualOut();
            ArrayList<double[][]> sendValue = Framework.getValueOfNodeArray(fpFramework.getSendVariables());
            sendValues.add(sendValue);
        }

        sendParameter(host, port, indies, sendValues);
    }

    /*public static double validate(ArrayList<double[][]> inList, ArrayList<double[][]> outList){
        double correct = 0;
        for (int i = inList.size() - Constants.NUM_VALIDATE; i < inList.size(); i++) {
            boolean actual = fpFramework.getActualOut().getValue()[0][0] > fpFramework.getActualOut().getValue()[1][0];
            boolean desire = outList.get(i)[0][0] > outList.get(i)[1][0];
            correct += (actual == desire) ? 1 : 0;
        }

        correct/=Constants.NUM_VALIDATE;
        return correct;
    }*/

    public static double[][] computeOut(double[][] in){
        fpFramework.getIn().setValue(in);
        fpFramework.computeActualOut();

        return fpFramework.getActualOut().getValue();
    }


}
