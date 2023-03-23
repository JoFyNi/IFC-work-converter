import java.io.*;

public class IFC {
    double x;
    double y;
    double z;

    public IFC(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void analyze() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("testFile.txt")));
        reader.readLine();
        reader.close();
    }
}
