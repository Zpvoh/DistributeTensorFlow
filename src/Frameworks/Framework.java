package Frameworks;

import Node.ValueNode;
import Node.Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Framework implements Serializable{
    private ArrayList<ValueNode> parameters;
    private ArrayList<Node> serverVariables;
    private ArrayList<Node> sendVariables;

    private ArrayList<double[][]> actualOut;
    private boolean isReadParameter;

    public Framework(){
        //this.input=new ArrayList<>();
        //this.output=new ArrayList<>();
        this.parameters=new ArrayList<>();
        this.serverVariables=new ArrayList<>();
        this.sendVariables=new ArrayList<>();

        //ValueNode inputNode=new ValueNode(inputNum,1);
        //parameters.add(inputNode);
    }

    public boolean addParameter(ValueNode para){
        parameters.add(para);

        return true;
    }

    public boolean addServerVariables(Node operator){
        serverVariables.add(operator);

        return true;
    }

    public boolean addSendVariables(Node info){
        getSendVariables().add(info);

        return true;
    }

    public boolean readParameters(ObjectInputStream inputStream){
        for(int i=0; i<parameters.size(); i++){
            Object o=null;
            try {
                if((o=inputStream.readObject()) instanceof double[][]){
                    parameters.get(i).setValue((double[][]) o);
                }else{
                    isReadParameter=false;
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        isReadParameter=true;
        return true;
    }

    public boolean readParameters(ArrayList<double[][]> values){
        if(values.size()!=parameters.size())
            return false;

        for(int i=0; i<parameters.size(); i++){
            parameters.get(i).setValue(values.get(i));
        }

        return true;
    }

    public boolean computeActualOut() {
        //ArrayList<double[][]> result=new ArrayList<>();
        //ValueNode first=parameters.get(0);
        //first.setValue(in);

        for(int i=0; i<serverVariables.size(); i++){
            if(!serverVariables.get(i).compute()) {
                System.out.println("Failed");
                return false;
            }
        }

        return true;
    }

    public ArrayList<ValueNode> getParameters() {
        return parameters;
    }

    public ArrayList<Node> getServerVariables() {
        return serverVariables;
    }

    public boolean isReadParameter() {
        return isReadParameter;
    }

    public ArrayList<Node> getSendVariables() {
        return sendVariables;
    }

    public static ArrayList<double[][]> getValueOfNodeArray(ArrayList<Node> nodeArr){
        ArrayList<double[][]> result=new ArrayList<>();
        nodeArr.forEach(node -> {
            result.add(node.getValue());
        });

        return result;
    }
}
