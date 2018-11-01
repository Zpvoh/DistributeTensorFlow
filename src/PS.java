import Frameworks.BPFramework;
import Frameworks.Framework;
import Node.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PS {
    //private static int[] neuronNum;
    private static ArrayList<double[][]> outList;
    private static BPFramework bpFramework;
    private static boolean[] isClosing = {false};
    private static ServerSocket serverWithWorker;


    public static void main(String[] args) {
        Constants.readConfig();

        try {
            int PSPort=Integer.parseInt(Constants.configs.get("PSPort"));
            int PS2WorkerPort=Integer.parseInt(Constants.configs.get("PS2WorkerPort"));
            ServerSocket serverWithMaster = new ServerSocket(PSPort);
            serverWithWorker=new ServerSocket(PS2WorkerPort);

            while (true) {
                isClosing[0] = false;
                System.out.println("PS waiting for Master...");

                Socket socketWithMaster = serverWithMaster.accept();
                ObjectInputStream inputStreamWithMaster = new ObjectInputStream(socketWithMaster.getInputStream());

                bpFramework = (BPFramework) (inputStreamWithMaster.readObject());
                outList = (ArrayList<double[][]>) (inputStreamWithMaster.readObject());
                System.out.println("PS reading finished.");

                OutputStream outputStreamWithMaster = socketWithMaster.getOutputStream();
                outputStreamWithMaster.write(Constants.READY);
                outputStreamWithMaster.flush();
                System.out.println("PS responding finished.");
                socketWithMaster.close();

                bpFramework.initBiasAndWeights(-1, 0, 0, 1);
                prepareServerForWorker();
            }


        } catch (IOException | ClassNotFoundException e) {
            System.out.println("IO has some problem");
        }
    }

    public static void prepareServerForWorker() throws IOException {
        System.out.println("PS waiting for worker...");
        while (!isClosing[0]) {
            Socket socketWithWorker = serverWithWorker.accept();
            ThreadForWorker threadForWorker = new ThreadForWorker(socketWithWorker, bpFramework, outList, isClosing);
            threadForWorker.run();
        }

        System.out.println("PS finishes his job.");
        //serverWithWorker.close();
    }
}

class ThreadForWorker implements Runnable {
    private Socket socketWithWorker;
    private final BPFramework bpFramework;
    private ArrayList<double[][]> outList;
    private boolean[] isClosing;

    public ThreadForWorker(Socket socketWithWorker, BPFramework bpFramework, ArrayList<double[][]> outList, boolean[] isClosing) {
        this.socketWithWorker = socketWithWorker;
        this.bpFramework = bpFramework;
        this.outList = outList;
        this.isClosing = isClosing;
    }

    @Override
    public void run() {
        //synchronized (bpFramework){
        try {
            ObjectInputStream streamFromWorker = new ObjectInputStream(socketWithWorker.getInputStream());
            Integer close = (Integer) (streamFromWorker.readObject());
            if (close.equals(Constants.CLOSE_PS)) {
                isClosing[0] = true;
                return;
            }

            Integer query = (Integer) (streamFromWorker.readObject());
            if (query.equals(Constants.WORKER_REQUIRE_PARAMETER)) {
                ObjectOutputStream streamToWorker = new ObjectOutputStream(socketWithWorker.getOutputStream());
                ArrayList<Node> sendVariables = bpFramework.getSendVariables();
                streamToWorker.writeObject(Framework.getValueOfNodeArray(sendVariables));
                streamToWorker.flush();
                streamToWorker.close();
            } else if (query.equals(Constants.WORKER_SEND_PARAMETER)) {
                ArrayList<Integer> indies = (ArrayList<Integer>) (streamFromWorker.readObject());
                ArrayList<ArrayList<double[][]>> values = (ArrayList) (streamFromWorker.readObject());

                for (int i = 0; i < indies.size(); i++) {
                    bpFramework.getDesireOut().setValue(outList.get(indies.get(i)));
                    bpFramework.readParameters(values.get(i));
                    bpFramework.computeActualOut();
                }
            }

            streamFromWorker.close();
            socketWithWorker.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("IO has some problem");
        }
        }
    //}
}
