
/**
 * @author Christian Bargmann <christian.bargmann@haw-hamburg.de>
 * @version 03.07.2017
 * @see
 * @since 03.07.2017 , 22:10:55
 *
 */
public class Startup {

	public static void main(String[] args) {
		// WorldGenerator worldgen = new SimplexNoiseGenerator(10, 0.2f, 0.0025f);
		WorldGenerator worldgen = new SimplexNoiseGenerator(7, 0.6f, 0.0050f);
		MapImage mi = new MapImage();

		for (int i = 0; i < 10; i++) {
			double[][] array = worldgen.createWorld(500, 500);
			mi.visualize(array, "generatedMap" + i);
		}
	}
}