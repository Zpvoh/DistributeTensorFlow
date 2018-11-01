import Frameworks.BPFramework;
import Frameworks.FPFramework;
import com.csvreader.CsvReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Master {
    private static ArrayList<double[][]> inList = new ArrayList<>();
    private static ArrayList<double[][]> outList = new ArrayList<>();

    public static void main(String[] args){
        Constants.readConfig();
        readTrainingSet();
        //generateSin();
        System.out.println("Training set has read.");

        ServerSocket serverSocket = null;
        try {
            int MasterPort=Integer.parseInt(Constants.configs.get("MasterPort"));
            serverSocket = new ServerSocket(MasterPort);
            while (true) {
                System.out.println("Master waiting for Client...");
                Socket socket = serverSocket.accept();
                System.out.println("Client is here!");

                InputStream stream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(stream);

                FPFramework fpFramework = (FPFramework) (objectInputStream.readObject());
                BPFramework bpFramework = (BPFramework) (objectInputStream.readObject());
                double[][] queryIn = (double[][]) (objectInputStream.readObject());
                System.out.println("Master reading finished.");

                polishQueryIn(queryIn);
                String PSAddr=Constants.configs.get("PSAddr");
                int PSPort=Integer.parseInt(Constants.configs.get("PSPort"));
                String WorkerAddr=Constants.configs.get("WorkerAddr");
                int WorkerPort=Integer.parseInt(Constants.configs.get("WorkerPort"));
                interactWithPS(PSAddr, PSPort, bpFramework, outList);
                double[][] queryOut = interactWithWorker(WorkerAddr, WorkerPort, fpFramework, queryIn, inList, outList);

                ObjectOutputStream streamToClient = new ObjectOutputStream(socket.getOutputStream());
                streamToClient.writeObject(queryOut);
                System.out.println("Send queryOut to Client.");

                streamToClient.flush();
                streamToClient.close();
                stream.close();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("There is some problems");
        }

    }

    public static void readTrainingSet() {
        try {
            CsvReader csvReader = new CsvReader("student_data.csv");
            csvReader.readRecord();
            while (csvReader.readRecord()) {
                double[][] outArr = new double[2][1];
                outArr[0][0] = Double.parseDouble(csvReader.get(0)) == 0 ? 1 : 0;
                outArr[1][0] = Double.parseDouble(csvReader.get(0)) == 1 ? 1 : 0;
                outList.add(outArr);

                double[][] inArr = new double[3][1];
                inArr[0][0] = ((Double.parseDouble(csvReader.get(1)) - 200) / 600);
                inArr[1][0] = ((Double.parseDouble(csvReader.get(2)) - 1) / 3);
                inArr[2][0] = ((Double.parseDouble(csvReader.get(3)) - 1) / 3);
                inList.add(inArr);

            }
        } catch (FileNotFoundException e) {
            System.out.println("There is no such file");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
    }

    public static void polishQueryIn(double[][] inArr) {
        for (int i = 0; i < inArr.length; i++) {
            inArr[0][0] = ((inArr[0][0] - 200) / 600);
            inArr[1][0] = ((inArr[1][0] - 1) / 3);
            inArr[2][0] = ((inArr[2][0] - 1) / 3);
        }
    }

    public static void generateSin(){
        for(int i=0; i<400; i++){
            double x=(2*Math.random()-1)*Math.PI;
            double[][] in=new double[][]{{x}};
            double[][] out=new double[][]{{Math.sin(x)}};
            inList.add(in);
            outList.add(out);
        }
    }

    public static int interactWithPS(String host, int port, BPFramework bpFramework, ArrayList<double[][]> outList) throws IOException {
        Socket socketWithPS = new Socket(host, port);
        ObjectOutputStream outputStreamWithPS = new ObjectOutputStream(socketWithPS.getOutputStream());
        outputStreamWithPS.writeObject(bpFramework);
        outputStreamWithPS.writeObject(outList);

        InputStream inputStreamWithPS = socketWithPS.getInputStream();
        int response = inputStreamWithPS.read();
        if (response == Constants.READY) {
            System.out.println("PS got query.");
        } else {
            System.out.println("Getting query failed.");
        }

        outputStreamWithPS.close();
        inputStreamWithPS.close();
        socketWithPS.close();

        return response;
    }

    public static double[][] interactWithWorker(String host, int port, FPFramework fpFramework, double[][] queryIn, ArrayList<double[][]> inList, ArrayList<double[][]> outList) throws IOException, ClassNotFoundException {
        Socket socketWithWorker = new Socket(host, port);
        ObjectOutputStream outputStreamWithWorker = new ObjectOutputStream(socketWithWorker.getOutputStream());
        outputStreamWithWorker.writeObject(fpFramework);
        outputStreamWithWorker.writeObject(inList);
        outputStreamWithWorker.writeObject(outList);
        outputStreamWithWorker.writeObject(queryIn);

        ObjectInputStream inputStreamWithWorker = new ObjectInputStream(socketWithWorker.getInputStream());
        double[][] result = (double[][]) (inputStreamWithWorker.readObject());
        System.out.println("Worker has computed the required output.");

        outputStreamWithWorker.close();
        inputStreamWithWorker.close();
        socketWithWorker.close();

        return result;
    }
}
