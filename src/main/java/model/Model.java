package model;

import view.View;
import vo.Vacancy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Model {
    private final View view;
    private Provider[] providers;
    private List<Vacancy> vacancies;

    public Model(View view) {
        this.view = view;
    }

    public void parse() {
        vacancies = new ArrayList<>();
        try (ExecutorService service = Executors.newWorkStealingPool()) {
            List<Callable<List<Vacancy>>> strategies = Arrays.stream(providers).map(Provider::getStrategy).collect(Collectors.toList());
            List<Future<List<Vacancy>>> futures = service.invokeAll(strategies);
            for (Future<List<Vacancy>> future : futures) {
                vacancies.addAll(future.get());
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("error in parse method");
        }
        view.update(vacancies);
    }


    public List<Vacancy> getVacancies() {
        return vacancies;
    }

    public void update() {
        view.update(vacancies);
    }

    public void setProviders(Provider... providers) {
        this.providers = providers;
    }
}
