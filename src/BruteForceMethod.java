import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Particle;
import model.Point;

public class BruteForceMethod {

	private Map<Particle, Set<Particle>> neighbours;
	private boolean periodicBounds;
	private double rc;
	private double l;

	public BruteForceMethod(List<Particle> particles, double l, double rc,
			boolean periodicBounds) {
		this.rc = rc;
		this.l = l;
		this.periodicBounds = periodicBounds;
		neighbours = new HashMap<Particle, Set<Particle>>();
		fillNeighbours(particles);
	}

	private void fillNeighbours(List<Particle> particles) {
		for (int i = 0; i < particles.size(); i++) {
			for (int j = i + 1; j < particles.size(); j++) {
				MaybeAddNeighbour(particles.get(i), particles.get(j));
			}
		}
	}

	private void MaybeAddNeighbour(Particle p, Particle q) {
		if (p.equals(q)) {
			return;
		}
		Point pp = p.getPoint().clone();
		Point qq = q.getPoint().clone();
		double r = rc + p.getRadius() + q.getRadius();
		if (periodicBounds) {

			/*
			 * pp.x + l/2 < qq.x because that means that adding l to pp.x makes
			 * pp closer to qq.
			 */

			if (pp.x < qq.x && pp.x + l / 2 < qq.x) {
				pp.x += l;
			}
			if (qq.x < pp.x && qq.x + l / 2 < pp.x) {
				qq.x += l;
			}
			if (pp.y < qq.y && pp.y + l / 2 < qq.y) {
				pp.y += l;
			}
			if (qq.y < pp.y && qq.y + l / 2 < pp.y) {
				qq.y += l;
			}
		}

		double dist2 = Point.dist2(pp, qq);

		if (dist2 < r * r) {
			neighbours.get(p).add(q);
			neighbours.get(q).add(p);
		}
	}

	public Map<Particle, Set<Particle>> getNeighbours() {
		return neighbours;
	}
}
