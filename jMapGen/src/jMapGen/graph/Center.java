package jMapGen.graph;

import java.awt.Point;
import java.util.List;

  
  public class Center 
  {
    public int index;
  
    public Point point;  // location
    public Boolean water;  // lake or ocean
    public Boolean ocean;  // ocean
    public Boolean coast;  // land polygon touching an ocean
    public Boolean border;  // at the edge of the map
    public String biome;  // biome type (see article)
    public double elevation;  // 0.0-1.0
    public double moisture;  // 0.0-1.0

    public List<Center> neighbors;
    public List<Edge> borders;
    public List<Corner> corners;
  }