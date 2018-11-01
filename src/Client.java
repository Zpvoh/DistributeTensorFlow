import Frameworks.BPFramework;
import Frameworks.FPFramework;
import Node.ValueNode;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Constants.readConfig();

        Scanner scanner=new Scanner(System.in);
        System.out.println("Please input network layer number:");
        int layerNum=scanner.nextInt();
        int[] neuronNum=new int[layerNum];

        for(int i=0; i<layerNum; i++){
            System.out.println("Please input "+i+"th layer neuron number:");
            neuronNum[i]=scanner.nextInt();
        }

        System.out.println("Please input learning rate:");
        double rate=scanner.nextDouble();

        ValueNode in=new ValueNode(neuronNum[0],1);
        ValueNode desireOut=new ValueNode(neuronNum[neuronNum.length-1], 1);
        FPFramework fp=new FPFramework(neuronNum, in);
        BPFramework bp=new BPFramework(neuronNum, rate, desireOut);

        double[][] queryIn=new double[neuronNum[0]][1];
        System.out.println("Please type your input in line:");
        for(int i=0; i<neuronNum[0]; i++){
            queryIn[i][0]=scanner.nextDouble();
        }

        String MasterAddr=Constants.configs.get("MasterAddr");
        int MasterPort=Integer.parseInt(Constants.configs.get("MasterPort"));
        Socket socket=new Socket(MasterAddr, MasterPort);
        OutputStream stream=socket.getOutputStream();
        ObjectOutputStream ostream=new ObjectOutputStream(stream);

        ostream.writeObject(fp);
        ostream.writeObject(bp);
        ostream.writeObject(queryIn);

        ObjectInputStream streamFromMaster=new ObjectInputStream(socket.getInputStream());
        double[][] queryOut=(double[][]) (streamFromMaster.readObject());

        int result=(queryOut[0][0]>queryOut[1][0])?0:1;

        System.out.println("Result is "+result);

        streamFromMaster.close();
        ostream.close();
        stream.flush();
        stream.close();
    }
}
