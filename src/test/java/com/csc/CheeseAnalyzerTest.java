package com.csc;
import java.io.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheeseAnalyzerTest {

    @Test
    public void testAnalyzeFile_SmallSample(@TempDir Path tempDir) throws IOException {
        File tempCsv = tempDir.resolve("test_cheese.csv").toFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(tempCsv))) {
            // Write header and sample rows.
            pw.println("id,name,milk_type,milk_treatment,moisture,organic");
            pw.println("1,Cheddar,cow,pasteurized,42.0,1");
            pw.println("2,Brie,cow,raw,39.0,0");
            pw.println("3,Feta,goat,raw,42.5,1");
            pw.println("4,Swiss,goat,pasteurized,40.0,0");
            pw.println("5,Blue Cheese,ewe,raw,43.0,1");
            pw.println("6,Monterey,cow,pasteurized,44.0,1");
        }

        CheeseAnalyzer analyzer = new CheeseAnalyzer();
        CheeseAnalyzer.Results results = analyzer.analyzeFile(tempCsv.getAbsolutePath());

        // Expected results based on the CSV above:
        // pasteurizedCount: rows 1, 4, 6 => 3
        // rawCount: rows 2, 3, 5 => 3
        // organicHighMoistureCount: rows 1, 3, 5, 6 => 4
        // Most common milk type: "cow" (rows 1,2,6 = 3) vs "goat" (rows 3,4 = 2) vs "ewe" (row 5 = 1)
        assertEquals(3, results.pasteurizedCount, "Pasteurized count should be 3");
        assertEquals(3, results.rawCount, "Raw count should be 3");
        assertEquals(4, results.organicHighMoistureCount, "Organic with high moisture count should be 4");
        assertEquals("cow", results.mostCommonMilkType, "Most common milk type should be cow");
    }
}


