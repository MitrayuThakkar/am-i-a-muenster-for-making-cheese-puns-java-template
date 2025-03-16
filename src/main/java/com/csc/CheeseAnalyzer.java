package com.csc;

import java.io.*;

public class CheeseAnalyzer {

    /**
     * A small data class to hold the results of our cheese analysis.
     */
    public static class Results {
        public int pasteurizedCount;
        public int rawCount;
        public int organicHighMoistureCount;
        public String mostCommonMilkType;

        public Results() {}

        public Results(int pasteurizedCount, int rawCount, int organicHighMoistureCount, String mostCommonMilkType) {
            this.pasteurizedCount = pasteurizedCount;
            this.rawCount = rawCount;
            this.organicHighMoistureCount = organicHighMoistureCount;
            this.mostCommonMilkType = mostCommonMilkType;
        }
    }

    /**
     * Analyzes a cheese CSV file and returns the summarized results.
     *
     * Expected CSV columns (comma separated):
     *   Column 0: id (ignored)
     *   Column 1: name (ignored)
     *   Column 2: milk type (e.g., cow, goat, ewe, buffalo)
     *   Column 3: milk treatment (e.g., pasteurized or raw)
     *   Column 4: moisture percent (as a number)
     *   Column 5: organic flag (1 for organic, 0 otherwise)
     *
     * The first row is assumed to be a header.
     */
    public Results analyzeFile(String inputFilePath) throws IOException {
        int pasteurizedCount = 0;
        int rawCount = 0;
        int organicHighMoistureCount = 0;

        int cowCount = 0;
        int goatCount = 0;
        int eweCount = 0;
        int buffaloCount = 0;

        final int COL_MILK_TYPE = 2;
        final int COL_MILK_TREATMENT = 3;
        final int COL_MOISTURE_PERCENT = 4;
        final int COL_ORGANIC = 5;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    continue;
                }
                String milkType = parts[COL_MILK_TYPE].trim().toLowerCase();
                String milkTreatment = parts[COL_MILK_TREATMENT].trim().toLowerCase();
                String moistureStr = parts[COL_MOISTURE_PERCENT].trim();
                String organicStr = parts[COL_ORGANIC].trim();

                if (milkType.isEmpty() || milkTreatment.isEmpty() || moistureStr.isEmpty() || organicStr.isEmpty()) {
                    continue;
                }

                double moisture;
                int organicVal;
                try {
                    moisture = Double.parseDouble(moistureStr);
                    organicVal = Integer.parseInt(organicStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (milkTreatment.contains("pasteurized")) {
                    pasteurizedCount++;
                } else if (milkTreatment.contains("raw")) {
                    rawCount++;
                }

                if (organicVal == 1 && moisture > 41.0) {
                    organicHighMoistureCount++;
                }

                if (milkType.contains("cow")) {
                    cowCount++;
                } else if (milkType.contains("goat")) {
                    goatCount++;
                } else if (milkType.contains("ewe")) {
                    eweCount++;
                } else if (milkType.contains("buffalo")) {
                    buffaloCount++;
                }
            }
        }

        // Determine the most common milk type
        String mostCommonMilkType = "cow";
        int maxCount = cowCount;
        if (goatCount > maxCount) {
            mostCommonMilkType = "goat";
            maxCount = goatCount;
        }
        if (eweCount > maxCount) {
            mostCommonMilkType = "ewe";
            maxCount = eweCount;
        }
        if (buffaloCount > maxCount) {
            mostCommonMilkType = "buffalo";
        }

        return new Results(pasteurizedCount, rawCount, organicHighMoistureCount, mostCommonMilkType);
    }

    /**
     * Writes the analysis results to the given output file.
     */
    public void writeResults(String outputFilePath, Results results) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            writer.println("Number of pasteurized cheeses: " + results.pasteurizedCount);
            writer.println("Number of raw cheeses: " + results.rawCount);
            writer.println("Number of organic cheeses (moisture > 41%): " + results.organicHighMoistureCount);
            writer.println("Most common milk type: " + results.mostCommonMilkType);
        }
    }

    /**
     * Main method to run the analysis from the command line.
     * Usage: java com.csc.CheeseAnalyzer <inputFile.csv> <outputFile.txt>
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java com.csc.CheeseAnalyzer <inputFile.csv> <outputFile.txt>");
            return;
        }
        CheeseAnalyzer analyzer = new CheeseAnalyzer();
        try {
            Results results = analyzer.analyzeFile(args[0]);
            analyzer.writeResults(args[1], results);
            System.out.println("Analysis complete! Results written to " + args[1]);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
