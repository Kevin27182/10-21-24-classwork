import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String CSV_REGEX = "[,\n]";

    public static void main(String[] args) {

        String file = "data/cities.txt";
        Path filePath = Path.of(file);

        // For each state, list the top three cities in terms of population
        try (Stream<String> lines = Files.lines(filePath)) {

            var output = lines
                .parallel()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).toList())
                .sorted(Comparator.comparing(a -> Integer.parseInt(((List<String>) a).get(2).strip())).reversed())
                .collect(Collectors.groupingBy(a -> a.get(1)));

            output.forEach((key, value) -> {
                var cities = value.stream().limit(3).toList();
                System.out.println(key + ": " + cities);
            });
        }
        // Throw a runtime exception if the file is not found
        catch (IOException e) { throw new RuntimeException(e); }



        // For Each State, list the lowest city in terms of population.
        try (Stream<String> lines = Files.lines(filePath)) {

        }
        // Throw a runtime exception if the file is not found
        catch (IOException e) { throw new RuntimeException(e); }

        // Find all the cities that share names and print out their name, state and population.
        // Are there cities in Arkansas?  If so, list all their names and populations?
        // What is the population rank of the largest city in Arkansas (that is, if all the cities in the country were ranked by population, how far down on that list would AR's top city be?)


    }
}
