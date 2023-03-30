package ru.spbu.mas;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.UnreadableException;

public class FindAverage extends TickerBehaviour {
    private final double eps = 1e-6;
    private final int steps = 30;

    public FindAverage(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        Map<String, Double> receivedData = receiveData();
        processReceivedData(receivedData, (DefaultAgent) myAgent);
        Double previousState = ((DefaultAgent) this.myAgent).getVal();
        ((DefaultAgent) this.myAgent).updateNumber();

        double abs = Math.abs(previousState - ((DefaultAgent) this.myAgent).getVal());
        if (abs < eps && abs > 0 || getTickCount() > steps) {
            this.stop((DefaultAgent) myAgent);
            return;
        }
        sendData();
    }

    private void sendData() {
        List<String> linkedAgents = ((DefaultAgent) myAgent).getLinks();
        if (linkedAgents == null) {
            return;
        }

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        linkedAgents.forEach(agent -> {
            message.addReceiver(new AID(agent, AID.ISLOCALNAME));
        });

        Map<String, Double> contentToSend = new HashMap<String, Double>() {{
            // emulate corruption of data
            double randomAdd = 2 * Math.random() - 1;
            put(myAgent.getLocalName(), ((DefaultAgent) myAgent).getVal() + randomAdd);
        }};

        try {
            message.setContentObject((Serializable) contentToSend);
        } catch (Exception e) {
            System.out.println("Unable to generate message: " + contentToSend);
            e.printStackTrace();
        }

        myAgent.send(message);
    }

    private Map<String, Double> receiveData() {
        Map<String, Double> receivedData = new HashMap<>();

        ACLMessage msgRes = myAgent.receive();
        while (msgRes != null) {
            try {
                Object receivedContent = msgRes.getContentObject();
                if (receivedContent instanceof Map) {
                    receivedData.putAll((Map) receivedContent);
                }
            } catch (UnreadableException e) {
                this.stop((DefaultAgent) this.myAgent);
            } catch (Exception e) {
                System.out.println("Invalid message content in received message" + msgRes);
            }
            msgRes = myAgent.receive();
        }
        return receivedData;
    }

    private void processReceivedData(Map<String, Double> content, DefaultAgent agent) {
        if (content.isEmpty()) {
            return;
        }
        content.forEach(agent::addAgentNum);
    }

    private void stop(DefaultAgent currentAgent) {
        if (currentAgent.getName().contains("Main")) {
            System.out.println("\nComputed average value:  " + currentAgent.getVal());
        }
        currentAgent.doDelete();
        this.stop();
    }
}