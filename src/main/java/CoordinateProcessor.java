import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Polygon;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.Geometry;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.MultiPoint;
//import org.locationtech.jts.geom.Polygon;
//import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;
public class CoordinateProcessor {
    public List<Point> marchByAngle(Collection<? extends Point> points) {
        List<Point> hull = new ArrayList<>();

        Point startingPoint = GraphUtils.getMinY(points); // starting point guaranteed to be on the hull
        hull.add(startingPoint);

        Point prev = startingPoint;
        float prevAngle = -1;

        while (true) {
            float minAngle = Float.MAX_VALUE;
            double maxDist = 0;
            Point next = null;

            /*
             * iterate over every point and pick the one that creates the largest angle
             */
            for (Point p : points) {
                if (p == prev) continue;

                float angle = GraphUtils.angle(prev, p);
                double dist = GraphUtils.dist(prev, p);
                int compareAngles = Float.compare(angle, minAngle);

                if (compareAngles <= 0 && angle > prevAngle) {
                    if (compareAngles < 0 || dist > maxDist) {
                        /*
                         * found a better Point. It either has a smaller angle, or if it's collinear, then it's further way
                         */
                        minAngle = angle;
                        maxDist = dist;
                        next = p;
                    }
                }
            }

            if (next == startingPoint) break; // came back to the starting point, so we are done

            hull.add(next);

            prevAngle =  GraphUtils.angle(prev, next);
            prev = next;
        }

        return hull;
    }


    /**
     *
     * @param points
     * @return
     */
    public List<Point> march(Collection<? extends Point> points) {
        List<Point> hull = new ArrayList<>();

        Point startingPoint = GraphUtils.getMinY(points); // bottom most, left most point is guaranteed to be on the hull
        hull.add(startingPoint);

        Point prevVertex = startingPoint;

        while (true) {
            Point candidate = null;
            /*
             * iterate over every point and pick the one that creates the smallest counterclockwise angle
             * in reference to the previous vertex
             */
            for (Point point : points) {
                if (point == prevVertex) continue;

                if (candidate == null) {
                    candidate = point;
                    continue;
                }

                int ccw = GraphUtils.ccw(prevVertex, candidate, point);

                if (ccw == 0 && GraphUtils.dist(prevVertex, candidate) < GraphUtils.dist(prevVertex, point)) {
                    candidate = point; // collinear tie is decided by the distance
                } else if (ccw < 0) {
                    /*
                     * if made a clockwise turn, then found a better point that makes a smaller
                     * counterclockwise angle in reference to the previous vertex
                     */
                    candidate = point;
                }
            }

            if (candidate == startingPoint) break; // came back to the starting point, so we are done

            hull.add(candidate);
            prevVertex = candidate;
        }

        return hull;
    }
    //public static Polygon calculateVolume(List<Coordinate> coordinates) {
    //    GeometryFactory geometryFactory = new GeometryFactory();
    //    MultiPoint multiPoint = geometryFactory.createMultiPointFromCoords(
    //            coordinates.toArray(new Coordinate[0])
    //    );
    //    ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
    //    builder.setSites(multiPoint);
    //    Geometry triangles = builder.getTriangles(geometryFactory);
    //    return (Polygon) triangles.convexHull();
    //}

    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(2, 2));
        points.add(new Point(1, 3));
        points.add(new Point(1, 1));
        points.add(new Point(0, 2));
        points.add(new Point(3, 1));
        CoordinateProcessor hull = new CoordinateProcessor();

        System.out.println("Jarvis March:" + hull.march(points));

        //List<Coordinate> coordinates = new ArrayList<>();
        //coordinates.add(new Coordinate(0, 0));
        //coordinates.add(new Coordinate(0, 1));
        //coordinates.add(new Coordinate(1, 1));
        //coordinates.add(new Coordinate(1, 0));
        //coordinates.add(new Coordinate(0.5, 0.5));

        //double volume = CoordinateProcessor.calculateVolume(coordinates);
        //System.out.println("Volume: " + volume);
    }
}
