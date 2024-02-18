package controller;

import model.Model;
import model.Provider;
import model.strategy.*;
import view.SwingView;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Provider> providers;

    public static void initProviders(String query) {
        providers = new ArrayList<>();
        providers.add(new Provider(new JobsUAStrategy(query)));
        providers.add(new Provider(new RabotaUAStrategy(query)));
        providers.add(new Provider(new WorkUAStrategy(query)));
        providers.add(new Provider(new DouStrategy(query)));
        providers.add(new Provider(new OLXStrategy(query)));
    }

    public static void stopAll() {
        for (Provider provider : providers) {
            Strategy strategy = (Strategy) provider.getStrategy();
            strategy.stop();
        }
    }

    public static void main(String[] args) {
        SwingView view = new SwingView();
        Model model = new Model(view);
        Controller controller = new Controller(model);
        view.setController(controller);
        view.start();
    }
}