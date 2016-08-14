package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Point {

	static final double EPSILON = 1e-5;
	public double x, y;
	
	public Point(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public Point clone() {
		return new Point(x, y);
	}
	
	static public Point sum(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y);
	}
	
	static public Point sub(Point p1, Point p2){
		return new Point(p1.x-p2.x, p1.y-p2.y);
	}
	
	static public double abs(Point p){
		return Math.sqrt(abs2(p));
	}
	
	static public double abs2(Point p){
		return p.x*p.x+p.y*p.y;
	}

	static public double dist(Point p1, Point p2){
		return Math.sqrt(dist2(p1,p2));
	}
	
	static public double dist2(Point p1, Point p2){
		return abs2(sub(p1, p2));
	}
	
	static public double scalarProd(Point p1, Point p2){
		return p1.x*p2.x+p1.y*p2.y;
	}
	
	static public boolean arePerpendiculars(Point p1, Point p2){
		return Math.abs(scalarProd(p1, p2))<EPSILON;
	}
	
	static public double vectorProd(Point p1, Point p2){
		return p1.x*p2.y-p2.x*p1.y;
	}
	
	static public boolean areParallels(Point p1, Point p2){
		return paralelogramArea(p1, p2)<EPSILON;
	}
	
	static public double paralelogramArea(Point p1, Point p2, Point p3){
		return paralelogramArea(sub(p1,p2), sub(p3,p2));
	}
	
	static public double paralelogramArea(Point p1, Point p2){
		return Math.abs(vectorProd(p1, p2));
	}
	
	/**
	 * Only works when the z coordinate is zero.
	 * 
	 * @param p1 - first vector
	 * @param p2 - second vector
	 * @return 1 if p1 is clockwise to p2, 0 if p1 is parallel to p2, -1 if p1 is counterclockwise to p2
	 */
	static public int vectorOrientation(Point p1, Point p2){
		double sign = vectorProd(p1, p2);
		if(Math.abs(sign)<EPSILON)
			return 0;
		if(sign>0)
			return 1;
		return -1;
	}
	
	
	/**
	 *
	 * @param p - the pivot of the radial sorting (it has to be below the rest of the point, if more than one applies, select the leftmost of them). 
	 * @return a comparator to do the sorting.
	 */
	static public Comparator<Point> radialComparatorFrom(final Point p){
		return new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				Point v1 = sub(o1,p);
				Point v2 = sub(o2,p);
				int sign= vectorOrientation(v1, v2);
				if(sign==0){
					double d1 = abs2(v1);
					double d2 = abs2(v2);
					if(Math.abs(d1-d2)<EPSILON)
						return 0;
					if(d1>d2){
						return 1;
					}else{
						return -1;
					}
				}
				return -sign;
			}
		};
	}
	
	static public Comparator<Point> xComparator(){
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double diffX=o1.x-o2.x;
				if(Math.abs(diffX)<EPSILON){
					double diffY=o1.y-o2.y;
					if(Math.abs(diffY)<EPSILON)
						return 0;
					if(diffY>0)
						return 1;
					return -1;
				}
				if(diffX>0)
					return 1;
				return -1;
			}
		};
	}
	
	static public Comparator<Point> yComparator(){
		return new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				double diffY=o1.y-o2.y;
				if(Math.abs(diffY)<EPSILON){
					double diffX=o1.x-o2.x;
					if(Math.abs(diffX)<EPSILON)
						return 0;
					if(diffX>0)
						return 1;
					return -1;
				}
				if(diffY>0)
					return 1;
				return -1;
			}
		};
	}
	
	/**
	 * 
	 * @param points - array of points sorted radially counterclockwise from the lowest point (if more than one applies the leftmost of them).
	 * @return A list of points, representing the vertices of the Convex Hull.
	 */
	static public List<Point> convexHullEfficient(Point[] points){
		List<Point> result = new ArrayList<Point>();
		if(points.length<4){
			for (int i = 0; i < points.length; i++) {
				result.add(points[i]);
			}
		}else{
			int k=0;
			Point[] stack = new Point[points.length];
			stack[k++]=points[0];
			stack[k++]=points[1];
			int i=2;
			while(i<points.length){
				Point a = stack[k-1];
				Point b = (k>1?stack[k-2]:null);
				if( k>1 && vectorOrientation(sub(points[i],a), sub(b,a))<=0){
					k--;
				}else{
					stack[k++]=points[i++];
				}
			}
			for(i=0; i<k; i++){
				result.add(stack[i]);
			}
		}
		return result;
	}
	
	static public List<Point> convexHull(Point[] points){
		Point[] ppoint = new Point[points.length];
		Point minleftPoint = null;
		Comparator<Point> yComp = yComparator();
		int k=1;
		for(Point p : points){
			if(minleftPoint==null || yComp.compare(p, minleftPoint)<0){
				if(minleftPoint!=null){
					ppoint[k++]=minleftPoint;
				}
				minleftPoint=p;
			}else{
				ppoint[k++]=p;
			}
		}
		Arrays.sort(ppoint,1,k, radialComparatorFrom(minleftPoint));
		ppoint[0]=minleftPoint;
		
		return convexHullEfficient(ppoint);
	}
	
	static public List<Point> convexHull(Collection<Point> points){
		Point[] ppoint = new Point[points.size()];
		Point minleftPoint = null;
		Comparator<Point> yComp = yComparator();
		int k=1;
		for(Point p : points){
			if(minleftPoint==null || yComp.compare(p, minleftPoint)<0){
				if(minleftPoint!=null){
					ppoint[k++]=minleftPoint;
				}
				minleftPoint=p;
			}else{
				ppoint[k++]=p;
			}
		}
		Arrays.sort(ppoint,1,k, radialComparatorFrom(minleftPoint));
		ppoint[0]=minleftPoint;
		
		return convexHullEfficient(ppoint);
	}
	
	static public Pair<Point, Point> furthestPoints(Point[] points){
		return furthestPointsEfficient(convexHull(points));
	}
	
	static public Pair<Point, Point> furthestPoints(Collection<Point> points){
		return furthestPointsEfficient(convexHull(points));
	}
	
	/**
	 * 
	 * @param points - a collection of points, representing the perimeter of a convex polygon.
	 * @return a pair of furthest points in the polygon.
	 */
	static private Pair<Point, Point> furthestPointsEfficient(List<Point> points){
		int n = points.size();
		if(n<2)
			return null; 
		if(n==2)
			return new Pair<Point, Point>(points.get(0), points.get(1));
		int k=1;
		while(k<n-1 && paralelogramArea(points.get(n-1), points.get(0), points.get(k+1))>paralelogramArea(points.get(n-1), points.get(0), points.get(k)))
			k++;
		Pair<Point, Point> result = new Pair<Point, Point>(points.get(n-1), points.get(k));
		double maxDist2 = dist2(points.get(n-1), points.get(k));
		for(int i=0, j=k; i<k; i++){
			double currDist;
			double dist=paralelogramArea(points.get(i), points.get(i+1), points.get(j));
			double auxDist=paralelogramArea(points.get(i), points.get(i+1), points.get((j+1)%n));
			while(dist<auxDist){
				currDist = dist2(points.get(i), points.get(j));
				if(currDist>maxDist2){
					maxDist2=currDist;
					result.first=points.get(i);
					result.second=points.get(j);
				}
				dist=auxDist;
				j=(j+1)%n;
				auxDist=paralelogramArea(points.get(i), points.get(i+1), points.get((j+1)%n));
			} 
			currDist = dist2(points.get(i), points.get(j));
			if(currDist>maxDist2){
				maxDist2=currDist;
				result.first=points.get(i);
				result.second=points.get(j);
			}
		}
		return result;
	}
	
	static public Pair<Point, Point> closestPoints(Point[] points){
		Point[] aux = Arrays.copyOf(points, points.length);
		return findingClosestPoints(aux);
	}
	
	static public Pair<Point, Point> closestPoints(List<Point> points){
		Point[] aux = points.toArray(new Point[0]);
		return findingClosestPoints(aux);
	}
	
	static private Pair<Point, Point> findingClosestPoints(Point[] points){
		Arrays.sort(points, xComparator());
		return closestPointsEfficient(points);
	}
	
	/**
	 * 
	 * @param points - array of points sorted by its x-coordinate.
	 * @return a closest pair of points
	 */
	static public Pair<Point, Point> closestPointsEfficient(Point[] points){
		int n = points.length;
		if(n<2)
			return null;
		SortedSet<Point> pastPoints = new TreeSet<Point>(yComparator());
		Pair<Point, Point> result = new Pair<Point, Point>(points[0], points[1]);
		double minDist=dist(points[0], points[1]);
		int indexLastPointInTree = 0;
		for(int i=0; i<n; i++){
			double x=points[i].x;
			double y=points[i].y;
			while(indexLastPointInTree<i && points[indexLastPointInTree].x<x-minDist)
				pastPoints.remove(points[indexLastPointInTree++]);
			SortedSet<Point> auxSet = pastPoints.tailSet(new Point(x, y-minDist));
			for (Point point : auxSet) {
				if(point.y-y>minDist)
					break;
				double auxDist = dist(point, points[i]);
				if(auxDist<minDist){
					minDist=auxDist;
					result.first=point;
					result.second=points[i];
				}
			}
			pastPoints.add(points[i]);
		}
		return result;
	}

}