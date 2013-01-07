package jMapGen.graph;

import java.util.List;

public class Corner 
{
	public int index;

	public java.awt.Point point;  // location
	public Boolean ocean;  // ocean
	public Boolean water;  // lake or ocean
	public Boolean coast;  // touches ocean and land polygons
	public Boolean border;  // at the edge of the map
	public double elevation;  // 0.0-1.0
	public double moisture;  // 0.0-1.0

	public List<Center> touches;
	public List<Edge> protrudes;
	public List<Corner> adjacent;

	public int river;  // 0 if no river, or volume of water in river
	public Corner downslope;  // pointer to adjacent corner most downhill
	public Corner watershed;  // pointer to coastal corner, or null
	public int watershed_size;
}