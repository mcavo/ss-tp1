package run;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cellindexmethod.BruteForceMethod;
import cellindexmethod.CellIndexMethod;
import model.Particle;
import model.Point;
import utils.XYZFilesGenerator;

public class Run {
	
	public static void main(String[] args) {
		
		double l = 20; double r = 0.25, rc = 1; boolean periodicBounds = false; int n = 1000, seed = 6;
		List<Particle> particles = generateRandomExample(l, r, periodicBounds, n, seed);
		System.out.println("Cell Index Method:");
		double accumulate = 0;
		int count = 0;
		for(int m = 1 ; m<=13 ; m++) {
			accumulate = 0;
			for(count=0 ; count<50 ; count++) {
				CellIndexMethod cim = new CellIndexMethod(new ArrayList<Particle>(particles), l, m, rc, periodicBounds);
				accumulate += cim.getTime();
			}
			System.out.println("M = " + m + ": " + (accumulate/count) + "ms");
		}
		
		System.out.println("\n\nBrute Force Method:");
		accumulate = 0;
		for(count=0 ; count<50 ; count++) {
			BruteForceMethod bfm = new BruteForceMethod(new ArrayList<Particle>(particles), l, rc, periodicBounds);
			accumulate += bfm.getTime();
		}
		System.out.println("Time: " + (accumulate/count) + "ms");
		//XYZFilesGenerator.showNeighbours("output/example1", cim);
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
			particles.add(new Particle(i+1, x, y, r));
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

}
