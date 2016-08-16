package model;

public class Particle {
	
	private int id;
	private Point point;
	private double radius;
	
	public Particle(int id, double x, double y, double r) {
		this.id = id;
		this.point = new Point(x, y);
		this.radius = r;
	}

	public int getId() {
		return id;
	}

	public Point getPoint() {
		return point;
	}
	
	public double getRadius() {
		return radius;
	}

}
