package io.github.temesoft.testpojo.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.github.temesoft.testpojo.report.TestPojoReportService.TestType.Constructor;
import static io.github.temesoft.testpojo.report.TestPojoReportService.TestType.EqualsAndHashCode;
import static io.github.temesoft.testpojo.report.TestPojoReportService.TestType.Random;
import static io.github.temesoft.testpojo.report.TestPojoReportService.TestType.SetterGetter;
import static io.github.temesoft.testpojo.report.TestPojoReportService.TestType.ToString;

/**
 * Default implementation of {@link TestPojoReportService} that collects and manages
 * test execution reports.
 * <p>
 * This implementation uses thread-safe collections to store test findings, making it
 * safe for use in concurrent test execution scenarios. All report data is stored in
 * static fields, meaning reports accumulate across all instances unless explicitly
 * cleared using {@link #reset()}.
 * </p>
 * <p>
 * The report format organizes findings by class, with each test type's results
 * indented underneath. This provides a clear, hierarchical view of test results.
 * </p>
 *
 * <h3>Report Format Example:</h3>
 * <pre>
 * Class: com.example.User
 *     Test type: SetterGetter
 *         Field 'name' tested successfully
 *         Field 'email' tested successfully
 *     Test type: EqualsAndHashCode
 *         equals() contract verified
 *         hashCode() consistency verified
 * Class: com.example.Product
 *     Test type: Constructor
 *         Constructor with 3 parameters tested successfully
 * </pre>
 */
public class TestPojoReportServiceImpl implements TestPojoReportService {

    private static final String NL = "\n";
    private static final String TAB = "\t";

    private static final Map<Class<?>, List<String>> CLASS_TEST_RANDOM_OF_MESSAGES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<String>> CLASS_TEST_CONSTRUCTOR_OF_MESSAGES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<String>> CLASS_TEST_EQUALS_HASHCODE_OF_MESSAGES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<String>> CLASS_TEST_SETTER_GETTER_OF_MESSAGES = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<String>> CLASS_TEST_TO_STRING_OF_MESSAGES = new ConcurrentHashMap<>();
    private static final Set<Class<?>> CLASSES = new ConcurrentSkipListSet<>(Comparator.comparing(Class::getName));

    /**
     * Adds a report entry for a specific test type and class.
     * <p>
     * This method is called during test execution to record findings, errors, or other
     * notable information about a particular test.
     * </p>
     *
     * @param testType the type of test that generated this report entry, must not be {@code null}
     * @param clazz    the class that was being tested, must not be {@code null}
     * @param message  the report message or finding to record, must not be {@code null}
     */
    @Override
    public void addReportEntry(final TestType testType, final Class<?> clazz, final String message) {
        CLASSES.add(clazz);
        switch (testType) {
            case Constructor:
                CLASS_TEST_CONSTRUCTOR_OF_MESSAGES
                        .computeIfAbsent(clazz, k -> new ArrayList<>())
                        .add(message);
                break;
            case EqualsAndHashCode:
                CLASS_TEST_EQUALS_HASHCODE_OF_MESSAGES
                        .computeIfAbsent(clazz, k -> new ArrayList<>())
                        .add(message);
                break;
            case Random:
                CLASS_TEST_RANDOM_OF_MESSAGES
                        .computeIfAbsent(clazz, k -> new ArrayList<>())
                        .add(message);
                break;
            case SetterGetter:
                CLASS_TEST_SETTER_GETTER_OF_MESSAGES
                        .computeIfAbsent(clazz, k -> new ArrayList<>())
                        .add(message);
                break;
            case ToString:
                CLASS_TEST_TO_STRING_OF_MESSAGES
                        .computeIfAbsent(clazz, k -> new ArrayList<>())
                        .add(message);
                break;
        }
    }

    /**
     * Prints the generated report to the standard output (console).
     * <p>
     * This is a convenience method that generates the report and outputs it using
     * {@link System#out}.
     * </p>
     */
    @Override
    public void saveReport() {
        System.out.println(generateReport());
    }

    /**
     * Retrieves the generated report as a string.
     * <p>
     * The report includes all test findings organized by class and test type.
     * The format is human-readable with proper indentation and structure.
     * </p>
     *
     * @return the complete test report as a string, never {@code null}
     */
    @Override
    public String getReport() {
        return generateReport();
    }

    /**
     * Resets the report service, clearing all accumulated test data.
     * <p>
     * This method removes all report entries and class information, allowing for
     * a fresh start. This is useful when running multiple independent test suites.
     * </p>
     */
    @Override
    public void reset() {
        CLASS_TEST_RANDOM_OF_MESSAGES.clear();
        CLASS_TEST_CONSTRUCTOR_OF_MESSAGES.clear();
        CLASS_TEST_EQUALS_HASHCODE_OF_MESSAGES.clear();
        CLASS_TEST_SETTER_GETTER_OF_MESSAGES.clear();
        CLASS_TEST_TO_STRING_OF_MESSAGES.clear();
        CLASSES.clear();
    }

    /**
     * Saves the generated report to a file at the specified path.
     * <p>
     * The report is written using UTF-8 encoding. If a file already exists at the
     * specified path, it will be overwritten.
     * </p>
     *
     * @param path the file path where the report should be saved, must not be {@code null}
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override
    public void saveReport(final Path path) throws IOException {
        Files.write(
                path,
                getReport().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Generates the complete test report as a formatted string.
     * <p>
     * The report is organized by class name (alphabetically) and includes all
     * findings from each test type that was executed for that class.
     * </p>
     *
     * @return the formatted report string, never {@code null} but may be empty
     * if no tests have been executed
     */
    private String generateReport() {
        final StringBuilder result = new StringBuilder();
        CLASSES.forEach(aClass -> {
            result.append("Class: ").append(aClass.getName()).append(NL);
            result.append(printFindings(CLASS_TEST_SETTER_GETTER_OF_MESSAGES, aClass, SetterGetter));
            result.append(printFindings(CLASS_TEST_EQUALS_HASHCODE_OF_MESSAGES, aClass, EqualsAndHashCode));
            result.append(printFindings(CLASS_TEST_TO_STRING_OF_MESSAGES, aClass, ToString));
            result.append(printFindings(CLASS_TEST_CONSTRUCTOR_OF_MESSAGES, aClass, Constructor));
            result.append(printFindings(CLASS_TEST_RANDOM_OF_MESSAGES, aClass, Random));
        });
        return result.toString();
    }

    /**
     * Formats the findings for a specific test type and class.
     * <p>
     * Each test type section includes a header with the test type name, followed by
     * all messages associated with that test type for the given class, properly indented.
     * </p>
     *
     * @param mapOfMessages the map containing messages for the test type
     * @param aClass        the class being reported on
     * @param testType      the test type being reported
     * @return a formatted StringBuilder containing the findings section, never {@code null}
     */
    private StringBuilder printFindings(final Map<Class<?>, List<String>> mapOfMessages,
                                        final Class<?> aClass,
                                        final TestType testType) {
        final StringBuilder result = new StringBuilder();
        if (!mapOfMessages.isEmpty()) {
            result.append(TAB).append("Test type: ").append(testType).append(NL);
            final List<String> messages = mapOfMessages.get(aClass);
            if (messages != null) {
                messages.forEach(message -> result.append(TAB).append(TAB).append(message).append(NL));
            }
        }
        return result;
    }
}
