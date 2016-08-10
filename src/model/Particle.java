package model;

public class Particle {
	
	private Point point;
	private double radius;
	
	public Particle(double x, double y, double r) {
		this.point = new Point(x, y);
		this.radius = r;
	}

	public Point getPoint() {
		return point;
	}
	
	public double getRadius() {
		return radius;
	}

}
