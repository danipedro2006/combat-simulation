import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TerrainViewer extends Application {

    private static final String TIF_PATH = "C:\\Users\\danie\\Downloads\\srtm_41_03\\srtm_41_03.tif";
    private static final int SCALE = 2;      // Pixel spacing scale
    private static final int HEIGHT_SCALE = 10; // Exaggerate vertical

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load TIFF as grayscale
        BufferedImage image = ImageIO.read(new File(TIF_PATH));
        int width = image.getWidth();
        int height = image.getHeight();

        float[][] heightMap = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y) & 0xFF; // Assuming grayscale
                heightMap[y][x] = rgb;
            }
        }

        // Build terrain mesh
        TriangleMesh mesh = new TriangleMesh();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mesh.getPoints().addAll(
                    x * SCALE, 
                    -heightMap[y][x] * HEIGHT_SCALE, 
                    y * SCALE
                );
            }
        }

        // Texture coordinates (dummy)
        mesh.getTexCoords().addAll(0, 0);

        // Generate faces (2 triangles per square)
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                int p0 = y * width + x;
                int p1 = p0 + 1;
                int p2 = p0 + width;
                int p3 = p2 + 1;

                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0); // Triangle 1
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0); // Triangle 2
            }
        }

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.LIGHTGREEN));
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setCullFace(CullFace.BACK);

        Group root = new Group(meshView);
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.SKYBLUE);

        // Add camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setTranslateY(-200);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        scene.setCamera(camera);

        // Light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(300);
        light.setTranslateY(-500);
        light.setTranslateZ(-300);
        root.getChildren().add(light);

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX 3D Terrain");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
