package view;


import controller.Controller;
import vo.Vacancy;

import java.util.List;

public interface View {
    void update(List<Vacancy> vacancies);

    void setController(Controller controller);
}
