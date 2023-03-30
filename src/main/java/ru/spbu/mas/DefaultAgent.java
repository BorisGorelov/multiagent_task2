package ru.spbu.mas;


import jade.core.Agent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class DefaultAgent extends Agent {
    private String name;
    private List<String> links;
    public List<String> getLinks() {
        return links;
    }
    private Double val;
    public Double getVal() {
        return val;
    }
    private Map<String, Double> idToNum;
    private Map<Integer, Double> init = new HashMap<Integer, Double>() {{
        put(1, 7.0);
        put(2, 9.0);
        put(3, 19.5);
        put(4, 5.0);
        put(5, 4.56);
    }};

    @Override
    protected void setup() {
        name = getAID().getLocalName();
        int id = getId();
        Object[] arguments = getArguments();
        links = Arrays.asList((String[]) arguments[0]);
        val = init.get(id);
        idToNum = new HashMap<>();

        addBehaviour(new FindAverage(this, 100));
        System.out.println("Agent " + id + " with number " + val);
    }

    public int getId() {
        return Integer.parseInt(this.name.substring(0, 1));
    }

    public void addAgentNum(String sender, Double senderNumber) {
        idToNum.put(sender, senderNumber);
    }

    // local voting
    public void updateNumber() {
        double alpha = 1 / (1 + (float) this.links.size());
        double sum = idToNum.values()
                .stream()
                .mapToDouble(value -> alpha * (value - val))
                .sum();
        val = val + sum;
    }
}