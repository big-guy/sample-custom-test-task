package org.gradle.sample;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.events.OperationDescriptor;
import org.gradle.tooling.events.OperationType;
import org.gradle.tooling.events.test.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Configure the connector and create the connection
        GradleConnector connector = GradleConnector.newConnector();

        connector.useInstallation(new File(args[0]));
        connector.forProjectDirectory(new File(args[1]));

        ProjectConnection connection = connector.connect();
        try {
            // Configure the build
            BuildLauncher launcher = connection.newBuild();
            launcher.forTasks("testPassingDebug", "testFailingDebug");
            launcher.setStandardOutput(System.out);
            launcher.setStandardError(System.err);
            launcher.addProgressListener(progressEvent -> {
                // Test has just finished
                if (progressEvent instanceof TestFinishEvent testFinishEvent) {
                    switch(testFinishEvent.getResult()) {
                        case TestFailureResult x -> System.out.println(toEventPath(testFinishEvent.getDescriptor()) + " failed");
                        case TestSkippedResult x -> System.out.println(toEventPath(testFinishEvent.getDescriptor()) + " skipped");
                        case TestSuccessResult x -> System.out.println(toEventPath(testFinishEvent.getDescriptor()) + " succeeded");
                        default -> System.out.println(toEventPath(testFinishEvent.getDescriptor()) + " finished with unknown result");
                    }
                } else if (progressEvent instanceof TestMetadataEvent testMetadataEvent) {
                    // NOTE: metadata is associated with a test operation through its parent:
                    // progressEvent.getDescriptor().getParent() instanceof TestOperationDescriptor
                    System.out.println(toEventPath(testMetadataEvent.getDescriptor()) + " " + testMetadataEvent.getValues());
                }
            }, OperationType.TEST, OperationType.TEST_METADATA);

            // Run the build
            launcher.run();
        } finally {
            // Clean up
            connection.close();
        }
    }

    private static String toEventPath(OperationDescriptor descriptor) {
        List<String> names = new ArrayList<>();
        OperationDescriptor current = descriptor;
        while (current != null) {
            names.add(current.getDisplayName());
            current = current.getParent();
        }
        Collections.reverse(names);
        return String.join(" > ", names);
    }
}
