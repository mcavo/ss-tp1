package model;

public class Pair<A,B>{
	
	public A first;
	public B second;
	
	public Pair(){
		first=null;
		second=null;
	}
	
	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}
}