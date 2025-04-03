import java.awt.*;
import java.util.*;

public class ZBuffer extends Frame {
    private static final long serialVersionUID = 1L;

    public ZBuffer() {
        super("ZBuffer Algorithm");
        CvZBuf canvas = new CvZBuf();
        add(canvas);
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ZBuffer();
    }
}

// Main Canvas for 3D rendering with Z-Buffer
class CvZBuf extends Canvas3D {
    private int maxX, maxY, centerX, centerY;
    private float[][] buf;
    private Obj3D obj;
    private Point2D imgCenter;

    int iX(float x) { return Math.round(centerX + x - imgCenter.x); }
    int iY(float y) { return Math.round(centerY - y + imgCenter.y); }

    Obj3D getObj() { return obj; }
    void setObj(Obj3D obj) { this.obj = obj; }

    public void paint(Graphics g) {
        if (obj == null) return;
        Vector<Polygon3D> polyList = obj.getPolyList();
        if (polyList == null || polyList.size() == 0) return;

        Dimension dim = getSize();
        maxX = dim.width - 1;
        maxY = dim.height - 1;
        centerX = maxX / 2;
        centerY = maxY / 2;

        buf = new float[dim.width][dim.height];
        for (float[] row : buf) Arrays.fill(row, 1e30F);

        obj.eyeAndScreen(dim);
        imgCenter = obj.getImgCenter();
        obj.planeCoeff();
        Point3D[] e = obj.getE();
        Point2D[] vScr = obj.getVScr();

        for (Polygon3D pol : polyList) {
            if (pol.getNrs().length < 3 || pol.getH() >= 0) continue;

            int cCode = obj.colorCode(pol.getA(), pol.getB(), pol.getC());
            g.setColor(new Color(cCode, cCode, 0));

            pol.triangulate(obj);
            for (Tria tri : pol.getT()) {
                int iA = tri.iA, iB = tri.iB, iC = tri.iC;
                Point2D a = vScr[iA], b = vScr[iB], c = vScr[iC];
                double zAi = 1 / e[tri.iA].z, zBi = 1 / e[tri.iB].z, zCi = 1 / e[tri.iC].z;

                double u1 = b.x - a.x, v1 = c.x - a.x,
                       u2 = b.y - a.y, v2 = c.y - a.y,
                       cc = u1 * v2 - u2 * v1;
                if (cc <= 0) continue;

                double dzdx = -(u2 * (zBi - zAi) - (zCi - zAi) * v2) / cc;
                double dzdy = ((zCi - zAi) * u1 - (zBi - zAi) * v1) / cc;

                double xD = (a.x + b.x + c.x) / 3;
                double yD = (a.y + b.y + c.y) / 3;
                double zDi = (zAi + zBi + zCi) / 3;

                int yBottom = (int) Math.ceil(Math.min(a.y, Math.min(b.y, c.y)));
                int yTop = (int) Math.floor(Math.max(a.y, Math.max(b.y, c.y)));

                for (int y = yBottom; y <= yTop; y++) {
                    double xL = Math.min(a.x, Math.min(b.x, c.x));
                    double xR = Math.max(a.x, Math.max(b.x, c.x));
                    int iy = iY((float) y);
                    int iXL = iX((float) (xL + 0.5));
                    int iXR = iX((float) (xR - 0.5));
                    double zi = 1.01 * zDi + (y - yD) * dzdy + (xL - xD) * dzdx;

                    boolean leftmostValid = false;
                    int xLeftmost = 0;
                    for (int ix = iXL; ix <= iXR; ix++) {
                        if (zi < buf[ix][iy]) {
                            if (!leftmostValid) {
                                xLeftmost = ix;
                                leftmostValid = true;
                            }
                            buf[ix][iy] = (float) zi;
                        } else if (leftmostValid) {
                            g.drawLine(xLeftmost, iy, ix - 1, iy);
                            leftmostValid = false;
                        }
                        zi += dzdx;
                    }
                    if (leftmostValid) g.drawLine(xLeftmost, iy, iXR, iy);
                }
            }
        }
    }
}

// Supporting Classes

class Canvas3D extends Canvas {
    private static final long serialVersionUID = 1L;
}

class Point2D {
    float x, y;

    Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class Point3D {
    float x, y, z;

    Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Tria {
    int iA, iB, iC;

    Tria(int iA, int iB, int iC) {
        this.iA = iA;
        this.iB = iB;
        this.iC = iC;
    }
}

class Polygon3D {
    private int[] nrs;
    private double a, b, c, h;
    private Tria[] t;

    public int[] getNrs() { return nrs; }
    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getH() { return h; }
    public void triangulate(Obj3D obj) { t = new Tria[1]; }
    public Tria[] getT() { return t; }
}

class Obj3D {
    private Vector<Polygon3D> polyList = new Vector<>();
    private Point3D[] e;
    private Point2D[] vScr;
    private Point2D imgCenter;

    public Vector<Polygon3D> getPolyList() { return polyList; }
    public Point3D[] getE() { return e; }
    public Point2D[] getVScr() { return vScr; }
    public Point2D getImgCenter() { return imgCenter; }
    
    public void eyeAndScreen(Dimension dim) {}
    public void planeCoeff() {}
    public int colorCode(double a, double b, double c) { return 200; }
}
