package run;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import cellindexmethod.CellIndexMethod;
import model.Particle;
import model.Point;
import utils.Timer;
import utils.XYZFilesGenerator;

public class Run {

	public static void main(String[] args) {
		Map<String, String> optsMap = getOptions(args);
		runCIM(optsMap);
		// runBFM(optsMap);
	}

	private static void runCIM(Map<String, String> optsMap) {
		String lString, mString, rString, rcString, periodicBoundsString, generateXYZString, nString, seedString;
		
		double l, r, rc;
		boolean periodicBounds, generateXYZ;
		String dirPath;
		int m, n, seed;
		
		
		lString = optsMap.get("-l");
		rString = optsMap.get("-r");
		mString = optsMap.get("-m");
		rcString= optsMap.get("-rc");
		periodicBoundsString = optsMap.get("-pb");
		dirPath = optsMap.get("-p");
		nString = optsMap.get("-n");
		seedString = optsMap.get("-s");
		generateXYZString = optsMap.get("-xyz");
		if (lString == null || rString == null || mString == null || rcString  == null || periodicBoundsString  == null || nString  == null || dirPath == null) {
			throw new IllegalArgumentException("Missing parameter: -p path -l length -m cellsCount -r radius -rc radiusc -pb periodicBouds -n particles [-s seed] [-xyz generateXYZFiles]");
		}
		
		try {
			
			l = Double.parseDouble(lString);
			m = Integer.parseInt(mString);
			r = Double.parseDouble(rString);
			rc = Double.parseDouble(rcString);
			periodicBounds = Boolean.parseBoolean(periodicBoundsString);
			n = Integer.parseInt(nString);
			if (seedString != null) {
				seed = Integer.parseInt(seedString);
			} else {
				seed = (int) System.currentTimeMillis();
			}
			generateXYZ = false;
			if (generateXYZString != null) {
				generateXYZ = Boolean.parseBoolean(generateXYZString);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong type parameter: -p path -l double -m M -rc double -pb boolean -n int -r double [-s int] [-xyz boolean]");
		}
		
		try {
			Timer timer = new Timer();
			List<Particle> particles = generateRandomExample(l, r, periodicBounds, n, seed);
			timer.start();
			CellIndexMethod cim = new CellIndexMethod(particles, l, m, rc, periodicBounds);
			timer.stop();
			
			if (generateXYZ) {
				XYZFilesGenerator.showNeighbours(dirPath, cim);
			}
			
			writeFile(dirPath, cim, timer);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	private static Map<String, String> getOptions(String[] args) {
		Map<String, String> optsMap = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].length() < 2) {
					throw new IllegalArgumentException("Not a valid argument: " + args[i]);
				} else {
					if (args.length - 1 == i)
						throw new IllegalArgumentException("Expected arg after: " + args[i]);
					// -opt
					optsMap.put(args[i], args[i + 1]);
					i++;
				}
				break;
			default:
				throw new IllegalArgumentException("Expected arg before: " + args[i]);
			}
		}

		return optsMap;

	}

	public static List<Particle> generateRandomExample(double l, double r, boolean periodicBounds, int n, int seed) {
		List<Particle> particles = new ArrayList<>();
		Random generator = new Random(seed);
		for (int i = 0; i < n; i++) {
			double x = 0, y = 0;
			do {
				x = generator.nextDouble() * l;
				y = generator.nextDouble() * l;
			} while (!validxy(new Point(x, y), l, r, periodicBounds, particles));
			particles.add(new Particle(i + 1, x, y, r));
		}
		return particles;
	}

	private static boolean validxy(Point point, double l, double r, boolean periodicBounds, List<Particle> particles) {
		for (Particle particle : particles) {
			Point p = particle.getPoint();
			if (periodicBounds) {
				if (point.x < p.x && point.x + l / 2 < p.x) {
					point.x += l;
				}
				if (p.x < point.x && p.x + l / 2 < point.x) {
					point.x -= l;
				}
				if (point.y < p.y && point.y + l / 2 < p.y) {
					point.y += l;
				}
				if (p.y < point.y && p.y + l / 2 < point.y) {
					point.y -= l;
				}
			}
			if (Point.dist2(point, p) < Math.pow(2 * r, 2)) {
				return false;
			}
		}
		return true;
	}
	
	private static void writeFile(String path, CellIndexMethod cim, Timer timer) {
		Path file = Paths.get(path + "output.txt");
		List<String> lines = new ArrayList<>();
		
		lines.add("Time: " + timer.getTime() + " ms");
		lines.add("Output:");
		for(Entry<Particle, Set<Particle>> entry : cim.getNeighbours().entrySet()) {
			StringBuffer s = new StringBuffer(entry.getKey().getId() + ": [ ");
			
			for(Particle p : entry.getValue()) {
				s.append(p.getId() + " ");
			}
			s.append("]");
			lines.add(s.toString());
		}
		
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
