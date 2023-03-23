import java.util.Vector;

class Point {
    final float x;
    final float y;
    float angle; // calculated later on in reference to some other point

    Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
		//return "(" + x + ", " + y + ") angle = " + angle;
        return "(" + x + ", " + y + ")";
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            return p.x == this.x && p.y == this.y;
        } else {
            return false;
        }
    }
}

class GFG {

    public static int orientation(Point p, Point q, Point r) {
        float value = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (value == 0) {
            return 0;
        }
        return (value < 0) ? 1: 2;
    }

    public static void convexHull(Point points[], int n) {
        if (n < 3) return;

        Vector<Point> hull = new Vector<Point>();

        int l = 0;
        for (int i = 0; i < n; i++) {
            if (points[i].x < points[l].x) {
                l = i;
            }
        }
        int p = l,q;
        do {
            hull.add(points[p]);
            q = (p+1) % n;
            for(int i = 0; i < n; i++) {
                if (orientation(points[p], points[i], points[q]) == 2) {
                    q = i;
                }
            }
            p = q;
        } while (p != l);

        for (Point temp : hull) {
            System.out.println("(" + temp.x + ", " + temp.y + ")");
        }
    }

    public static void main(String[] args)
    {

        Point points[] = new Point[7];
        points[0]=new Point(0, 3);
        points[1]=new Point(2, 3);
        points[2]=new Point(1, 1);
        points[3]=new Point(2, 1);
        points[4]=new Point(3, 0);
        points[5]=new Point(0, 0);
        points[6]=new Point(3, 3);

        int n = points.length;
        convexHull(points, n);

    }
}
