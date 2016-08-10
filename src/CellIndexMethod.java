import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Particle;
import model.Point;

public class CellIndexMethod {

	Set<Particle>[][] matrix;
	private double cellLength;
	private Map<Particle, Set<Particle>> neighbours;
	private boolean periodicBounds;
	private double rc;

	public CellIndexMethod(List<Particle> particles, double l, int m, double rc, boolean periodicBounds) {
		cellLength = l / m;
		this.periodicBounds = periodicBounds;
		this.rc = rc;
		if (!validProperties(particles, l, m, rc)) {
			throw new IllegalArgumentException();
		}
		fillMatrix(particles, m);
		fillNeighbours(particles);
	}

	private void fillNeighbours(List<Particle> particles) {
		neighbours = new HashMap<Particle, Set<Particle>>();
		for (Particle p : particles) {
			neighbours.put(p, new HashSet<Particle>());
		}
		int m = matrix.length;
		for (int i = 1; i < m - 1; i++) {
			for (int j = 0; j < m - 1; j++) {
				Set<Particle> particlesInCell = matrix[i][j];
				for (Particle p : particlesInCell) {
					int[] dx = { 0, 0, 1, 1, 1 };
					int[] dy = { 0, 1, 1, 0, -1 };
					for (int k = 0; k < 5; k++) {
						for (Particle q : matrix[i + dy[k]][j + dx[k]]) {
							addNeighbour(p, q);
						}
					}
				}
			}
		}
		// i = 0
		for (int j = 0; j < m - 1; j++) {
			Set<Particle> particlesInCell = matrix[0][j];
			for (Particle p : particlesInCell) {
				int[] dx = { 0, 0, 1, 1, 1 };
				int[] dy = { 0, 1, 1, 0, -1 };
				for (int k = 0; k < 4; k++) {
					for (Particle q : matrix[dy[k]][j + dx[k]]) {
						addNeighbour(p, q);
					}
				}
				if (periodicBounds) {
					for (Particle q : matrix[dy[4]][(j + dx[4])%m]) {
						addNeighbour(p, q);
					}
				}
			}
		}
		// i = m-1
		// j = m-1
		// i = m-1 & j = m-1
	}

	private void addNeighbour(Particle p, Particle q) {
		double dist2 = Point.dist2(p.getPoint(), q.getPoint());
		double r = rc + p.getRadius() + q.getRadius();
		if (!p.equals(q) && dist2 < r * r) {
			neighbours.get(p).add(q);
			neighbours.get(q).add(p);
		}
	}

	private void fillMatrix(List<Particle> particles, int m) {
		matrix = new Set[m][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				matrix[i][j] = new HashSet<Particle>();
			}
		}
		for (Particle particle : particles) {
			Point p = particle.getPoint();
			matrix[(int) (p.x / cellLength)][(int) (p.y / cellLength)].add(particle);
		}
	}

	private boolean validProperties(List<Particle> particles, double l, int m, double rc) {
		double max1 = 0;
		double max2 = 0;
		for (Particle p : particles) {
			if (p.getRadius() >= max1) {
				max2 = max1;
				max1 = p.getRadius();
			} else {
				if (p.getRadius() >= max2) {
					max2 = p.getRadius();
				}
			}
		}
		return (cellLength > rc + max1 + max2);
	}

	public Map<Particle, Set<Particle>> getNeighbours() {
		return neighbours;
	}

}
