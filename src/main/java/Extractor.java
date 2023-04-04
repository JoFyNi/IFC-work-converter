import java.io.*;

public class Extractor {
    private static final String FILENAME = "src/main/java/sample.ifc"; // Name der IFC-Datei
    private static final File log = new File("log.txt");

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            double totalVolume = 0.0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) { // Nur Objekte verarbeiten (beginnen mit #)
                    double volume = analyzeGeoData(line);
                    totalVolume += volume;
                }
            }
            System.out.println("Gesamtvolumen der Objekte: " + totalVolume);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double extractHeight(String line) {
        String[] tokens = line.split("\\(");
        String heightStr = tokens[1].substring(0, tokens[1].length() - 1);
        if (isValidDouble(heightStr)) {
            return Double.parseDouble(heightStr);
        }
        return 0.0;
    }

    private static double extractWidth(String line) {
        String[] tokens = line.split(",");
        String widthStr = tokens[2].substring(1);
        if (isValidDouble(widthStr)) {
            return Double.parseDouble(widthStr);
        }
        return 0.0;
    }

    private static double[] extractCoordinates(String corner) {
        String[] tokens = corner.split(",");
        double[] coords = new double[3];
        for (int i = 0; i < 3; i++) {
            coords[i] = Double.parseDouble(tokens[i].substring(1));
        }
        return coords;
    }

    private static double analyzeGeoData(String line) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(log.getAbsolutePath()));
            double volume = 0.0;
            if (line.contains("IFCWALL")) {
                // Wenn es sich um eine IFC-Wand handelt, extrahiere die Höhe, Breite und Tiefe
                String[] tokens = line.split(",");
                boolean isProductDefinition = false; // Flag, um zu markieren, ob eine Produktdefinition verarbeitet wird
                String currentProduct = ""; // Name des aktuellen Produkts
                String currentProductType = ""; // Typ des aktuellen Produkts
                String currentProductShape = ""; // Form des aktuellen Produkts
                String currentProductMaterial = ""; // Material des aktuellen Produkts
                double currentProductHeight = 0.0; // Höhe des aktuellen Produkts
                double currentProductWidth = 0.0; // Breite des aktuellen Produkts
                double currentProductDepth = 0.0; // Tiefe des aktuellen Produkts
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("FILE_DESCRIPTION") || line.startsWith("FILE_SCHEMA")) {
                        continue; // Diese Zeilen sind für die Analyse der Geo-Daten nicht relevant und können ignoriert werden
                    } else if (line.startsWith("#1")) {
                        isProductDefinition = true; // Wir verarbeiten jetzt eine Produktdefinition
                    } else if (line.startsWith("#")) {
                        isProductDefinition = false; // Wir verarbeiten jetzt keine Produktdefinition mehr
                    } else if (isProductDefinition) {
                        if (line.startsWith("IFCWALL")) {
                            currentProduct = line.split("\\(")[1].split("\\)")[0]; // Extrahieren Sie den Namen des Produkts aus der Zeile
                            currentProductType = "IFCWALL"; // Der Typ des Produkts ist eine Wand
                        } else if (line.startsWith("#2")) {
                            // Wir interessieren uns nur für die Beziehung zwischen der Wand und dem Material
                            if (line.contains(currentProduct) && line.contains("IFCMATERIAL")) {
                                currentProductMaterial = line.split("\\(")[2].split("\\)")[0]; // Extrahieren Sie den Namen des Materials aus der Zeile
                            }
                        } else if (line.startsWith("#3")) {
                            // Wir interessieren uns nur für die Beziehung zwischen der Wand und der Eigenschaft
                            if (line.contains(currentProduct) && line.contains("IFCEXTRUDEDAREASOLID")) {
                                currentProductShape = line.split("\\(")[2].split("\\)")[0]; // Extrahieren Sie den Namen der Form aus der Zeile
                            }
                        }
                    } else if (line.startsWith("#" + currentProductShape)) {
                        // Wir verarbeiten jetzt die Zeilen, die die Abmessungen und Formen der Extrusion enthalten
                        if (line.contains("IFCAXIS2PLACEMENT3D")) {
                            // Extrahieren Sie die Koordinaten der Ecken
                            double[] coords2 = new double[0];
                            double[] coords1 = new double[0];
                            if (tokens.length >= 13) {
                                String corner1 = tokens[9] + "," + tokens[10] + "," + tokens[11];
                                String corner2 = tokens[12] + "," + tokens[13] + "," + tokens[14];
                                coords1 = extractCoordinates(corner1);
                                coords2 = extractCoordinates(corner2);
                            }// Berechne die Abmessungen der Extrusion
                            double height = extractHeight(tokens[8]);
                            double depth = Math.sqrt(Math.pow(coords2[0] - coords1[0], 2) + Math.pow(coords2[1] - coords1[1], 2));
                            double width = extractWidth(tokens[10]);

                            // Berechne das Volumen als Höhe * Breite * Tiefe
                            volume = height * width * depth;
                            return volume;
                        }
                    }
                }
                logWriter.write(currentProductType + " " +
                        currentProductShape + " " +
                        currentProductMaterial + " " +
                        currentProductHeight + " " +
                        currentProductWidth + " " +
                        currentProductDepth + "\n");
                logWriter.flush();
            }
            logWriter.close();
            return volume;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hilfsmethode zur Validierung eines Strings als Double
    private static boolean isValidDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
