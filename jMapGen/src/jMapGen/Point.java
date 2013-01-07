package jMapGen;

import java.awt.geom.Point2D;
import java.util.Vector;

public class Point extends Point2D
{
	public double x, y;
	
	public Point()
	{
		x = 0;
		y = 0;
	}
	
	public Point(double X, double Y)
	{
		x = X;
		y = Y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double arg0, double arg1) 
	{
		x = arg0;
		y = arg1;
	}
	
	public double getLength()
	{
		return this.distance(0, 0);
	}
	
	public static double distance(Point p0, Point p1)
	{
		return new Point(p0.x, p0.y).distance(p1);
	}
	
}