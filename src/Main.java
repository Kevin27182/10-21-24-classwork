import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String CSV_REGEX = "[,\n]";

    public static void main(String[] args) {

        String file = "data/cities.txt";
        Path filePath = Path.of(file);
        List<String> lines;

        // Extract lines to a new List
        try (Stream<String> l = Files.lines(filePath)) {
            lines = l.toList();
        }
        // Throw a runtime exception if the file is not found
        catch (IOException e) { throw new RuntimeException(e); }

        // For Each State list the top three cities in terms of population.
        buildTitle("For Each State list the top three cities in terms of population.");
        lines
                .parallelStream()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).map(String::strip).toList())
                .sorted(Comparator.comparing(a -> Integer.parseInt(((List<String>) a).get(2))).reversed())
                .collect(Collectors.groupingBy(a -> a.get(1)))
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> System.out.println(entry.getKey() + ": " + entry.getValue().stream().limit(3).toList()));

        // For Each State, list the lowest city in terms of population.
        buildTitle("For Each State, list the lowest city in terms of population.");
        lines
                .parallelStream()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).map(String::strip).toList())
                .sorted(Comparator.comparing(a -> Integer.parseInt(a.get(2))))
                .collect(Collectors.groupingBy(a -> a.get(1)))
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> System.out.println(entry.getKey() + ": " + entry.getValue().stream().toList().getFirst()));

        // Find all the cities that share names and print out their name, state and population.
        buildTitle("Find all the cities that share names and print out their name, state and population.");
        lines
                .parallelStream()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).map(String::strip).toList())
                .collect(Collectors.groupingBy(List::getFirst))
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.comparingByKey())
                .filter(entry -> entry.getValue().size() > 1)
                .forEachOrdered(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Are there cities in Arkansas?  If so, list all their names and populations?
        buildTitle("Are there cities in Arkansas?  If so, list all their names and populations?");
        lines
                .parallelStream()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).map(String::strip).toList())
                .filter(a -> a.get(1).equalsIgnoreCase("AR"))
                .sorted(Comparator.comparing(List::getFirst))
                .forEachOrdered(a -> System.out.println(a.getFirst() + ": " + a.getLast()));

        // What is the population rank of the largest city in Arkansas (that is, if all the cities in the country were ranked by population, how far down on that list would AR's top city be?)
        buildTitle("What is the population rank of the largest city in Arkansas (that is,", "if all the cities in the country were ranked by population, how far", "down on that list would AR's top city be?)");
        var counter = new AtomicInteger(0);
        lines
                .stream()
                .map(line -> Pattern.compile(CSV_REGEX).splitAsStream(line).map(String::strip).toList())
                .sorted(Comparator.comparing(a -> Integer.parseInt(((List<String>) a).get(2))).reversed())
                .map(a -> new ArrayList<String>() {{ add(String.valueOf(counter.getAndIncrement())); addAll(a); }})
                .filter(a -> a.get(2).equalsIgnoreCase("AR"))
                .limit(1)
                .forEachOrdered(a -> System.out.println("Rank " + a.getFirst() + ": " + a.subList(1, a.size())));
    }

    // Create a fancy title
    private static void buildTitle(String... title) {

        // It's not suspicious regex if you know what you're doing
        String line = Stream.of(title)
                .max(Comparator.comparing(String::length))
                .orElse("")
                .replaceAll(".", "-");

        System.out.println();
        System.out.println(line);
        Stream.of(title).forEach(System.out::println);
        System.out.println(line);
    }
}
