package io.github.temesoft.testpojo.report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service interface for managing test execution reports in the test-pojo framework.
 * <p>
 * This service collects and organizes test results from various POJO validation tests,
 * providing functionality to generate, retrieve, and persist reports. The reports include
 * information about which classes were tested and any messages or findings from each test type.
 * </p>
 * <p>
 * This interface is currently internal to the test-pojo framework and is used primarily by
 * {@link io.github.temesoft.testpojo.TestPojo} to track test execution.
 * </p>
 */
public interface TestPojoReportService {

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
    void addReportEntry(TestType testType, Class<?> clazz, String message);

    /**
     * Prints the generated report to the standard output (console).
     * <p>
     * This is a convenience method that generates the report and outputs it using
     * {@link System#out}.
     * </p>
     */
    void saveReport();

    /**
     * Retrieves the generated report as a string.
     * <p>
     * The report includes all test findings organized by class and test type.
     * The format is human-readable with proper indentation and structure.
     * </p>
     *
     * @return the complete test report as a string, never {@code null}
     */
    String getReport();

    /**
     * Resets the report service, clearing all accumulated test data.
     * <p>
     * This method removes all report entries and class information, allowing for
     * a fresh start. This is useful when running multiple independent test suites.
     * </p>
     */
    void reset();

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
    void saveReport(Path path) throws IOException;

    /**
     * Enumeration of test types supported by the test-pojo framework.
     * <p>
     * Each test type corresponds to a specific aspect of POJO validation:
     * </p>
     * <ul>
     *   <li>{@link #Constructor} - Tests all public constructors can be invoked with random data</li>
     *   <li>{@link #EqualsAndHashCode} - Tests {@code equals()} and {@code hashCode()} contract compliance</li>
     *   <li>{@link #Random} - Tests random instantiation and method invocation with random parameters</li>
     *   <li>{@link #SetterGetter} - Tests getter and setter method pairs</li>
     *   <li>{@link #ToString} - Tests {@code toString()} method consistency</li>
     * </ul>
     */
    enum TestType {
        /**
         * Constructor testing - validates all public constructors.
         */
        Constructor,

        /**
         * Equals and HashCode testing - validates contract compliance.
         */
        EqualsAndHashCode,

        /**
         * Random instantiation testing - validates methods with random data.
         */
        Random,

        /**
         * Setter/Getter testing - validates accessor method pairs.
         */
        SetterGetter,

        /**
         * ToString testing - validates toString() consistency.
         */
        ToString
    }
}