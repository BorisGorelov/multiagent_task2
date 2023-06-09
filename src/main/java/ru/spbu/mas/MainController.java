package ru.spbu.mas;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.util.HashMap;

public class MainController {
    void initAgents() {
        HashMap<String, String[]> neighbors = new HashMap<>();
        neighbors.put("1_Main", new String[]{"2", "3", "4"});
        neighbors.put("2", new String[]{"1_Main", "4"});
        neighbors.put("3", new String[]{"1_Main", "4", "5"});
        neighbors.put("4", new String[]{"3", "5"});
        neighbors.put("5", new String[]{"1_Main", "4"});

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "8085");
        profile.setParameter(Profile.CONTAINER_NAME, "CurrentContainer");
        profile.setParameter(Profile.GUI, "true");
        Runtime runtime = Runtime.instance();
        ContainerController cc = runtime.createMainContainer(profile);

        try {
            for (String i: neighbors.keySet()) {
                AgentController agent = cc.createNewAgent(i,
                        "ru.spbu.mas.DefaultAgent",
                        new Object[]{neighbors.get(i)});
                agent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}