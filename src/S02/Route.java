import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<City> route = new ArrayList<>();

    public List<City> getRoute() {
        return route;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!route.isEmpty()) {
            for (City city : route) {
                stringBuilder.append(city.getID()).append(" ");
            }

            stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));
        }

        return "route : " + stringBuilder;
    }
}