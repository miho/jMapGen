package jMapGen.com.nodename.Delaunay;

import jMapGen.com.nodename.geom.LineSegment;

import java.util.Vector;
	/**
	 * The line segment connecting the two Sites is part of the Delaunay triangulation;
	 * the line segment connecting the two Vertices is part of the Voronoi diagram
	 * @author ashaw
	 * 
	 */
	public final class Edge
	{
		private static Vector<Edge> _pool = new Vector<Edge>();

		/**
		 * This is the only way to create a new Edge 
		 * @param site0
		 * @param site1
		 * @return 
		 * 
		 */
		static Edge createBisectingEdge(Site site0, Site site1)
		{
			double dx, dy, absdx, absdy;
			double a, b, c;
		
			dx = site1.x - site0.x;
			dy = site1.y - site0.y;
			absdx = dx > 0 ? dx : -dx;
			absdy = dy > 0 ? dy : -dy;
			c = site0.x * dx + site0.y * dy + (dx * dx + dy * dy) * 0.5;
			if (absdx > absdy)
			{
				a = 1.0; b = dy/dx; c /= dx;
			}
			else
			{
				b = 1.0; a = dx/dy; c /= dy;
			}
			
			Edge edge = Edge.create();
		
			edge.leftSite = site0;
			edge.rightSite = site1;
			site0.addEdge(edge);
			site1.addEdge(edge);
			
			edge._leftVertex = null;
			edge._rightVertex = null;
			
			edge.a = a; edge.b = b; edge.c = c;
			//trace("createBisectingEdge: a ", edge.a, "b", edge.b, "c", edge.c);
			
			return edge;
		}

		private Edge create()
		{
			Edge edge;
			if (_pool.size() > 0)
			{
				edge = _pool.firstElement();
				edge.init();
			}
			else
			{
				edge = new Edge();
			}
			return edge;
		}

		public LineSegment delaunayLine()
		{
			// draw a line connecting the input Sites for which the edge is a bisector:
			return new LineSegment(leftSite.coord, rightSite.coord);
		}

                public LineSegment voronoiEdge()
                {
                  if (!visible) return new LineSegment(null, null);
                  return new LineSegment(_clippedVertices[LR.LEFT],
                                         _clippedVertices[LR.RIGHT]);
                }

		private static int _nedges = 0;
		
		private static final Edge DELETED = new Edge();
		
		// the equation of the edge: ax + by = c
		private double a, b, c;
		
		// the two Voronoi vertices that the edge connects
		//		(if one of them is null, the edge extends to infinity)
		 Vertex _leftVertex;
		Vertex getleftVertex()
		{
			return _leftVertex;
		}
		
		Vertex _rightVertex;
		Vertex getRightVertex()
		{
			return _rightVertex;
		}
		Vertex vertex(LR leftRight)
		{
			return (leftRight == LR.LEFT) ? _leftVertex : _rightVertex;
		}
		void setVertex(LR leftRight, Vertex v)
		{
			if (leftRight == LR.LEFT)
			{
				_leftVertex = v;
			}
			else
			{
				_rightVertex = v;
			}
		}
		
		internal function isPartOfConvexHull():Boolean
		{
			return (_leftVertex == null || _rightVertex == null);
		}
		
		public function sitesDistance():Number
		{
			return Point.distance(leftSite.coord, rightSite.coord);
		}
		
		public static function compareSitesDistances_MAX(edge0:Edge, edge1:Edge):Number
		{
			var length0:Number = edge0.sitesDistance();
			var length1:Number = edge1.sitesDistance();
			if (length0 < length1)
			{
				return 1;
			}
			if (length0 > length1)
			{
				return -1;
			}
			return 0;
		}
		
		public static function compareSitesDistances(edge0:Edge, edge1:Edge):Number
		{
			return - compareSitesDistances_MAX(edge0, edge1);
		}
		
		// Once clipVertices() is called, this Dictionary will hold two Points
		// representing the clipped coordinates of the left and right ends...
		private var _clippedVertices:Dictionary;
		internal function get clippedEnds():Dictionary
		{
			return _clippedVertices;
		}
		// unless the entire Edge is outside the bounds.
		// In that case visible will be false:
		internal function get visible():Boolean
		{
			return _clippedVertices != null;
		}
		
		// the two input Sites for which this Edge is a bisector:
		private var _sites:Dictionary;
		internal function set leftSite(s:Site):void
		{
			_sites[LR.LEFT] = s;
		}
		internal function get leftSite():Site
		{
			return _sites[LR.LEFT];
		}
		internal function set rightSite(s:Site):void
		{
			_sites[LR.RIGHT] = s;
		}
		internal function get rightSite():Site
		{
			return _sites[LR.RIGHT] as Site;
		}
		internal function site(leftRight:LR):Site
		{
			return _sites[leftRight] as Site;
		}
		
		private var _edgeIndex:int;
		
		public function dispose():void
		{
			if (_delaunayLineBmp)
			{
				_delaunayLineBmp.dispose();
				_delaunayLineBmp = null;
			}
			_leftVertex = null;
			_rightVertex = null;
			if (_clippedVertices)
			{
				_clippedVertices[LR.LEFT] = null;
				_clippedVertices[LR.RIGHT] = null;
				_clippedVertices = null;
			}
			_sites[LR.LEFT] = null;
			_sites[LR.RIGHT] = null;
			_sites = null;
			
			_pool.push(this);
		}

		public function Edge(lock:Class)
		{
			if (lock != PrivateConstructorEnforcer)
			{
				throw new Error("Edge: constructor is private");
			}
			
			_edgeIndex = _nedges++;
			init();
		}
		
		private function init():void
		{	
			_sites = new Dictionary(true);
		}
		
		public function toString():String
		{
			return "Edge " + _edgeIndex + "; sites " + _sites[LR.LEFT] + ", " + _sites[LR.RIGHT]
					+ "; endVertices " + (_leftVertex ? _leftVertex.vertexIndex : "null") + ", "
					 + (_rightVertex ? _rightVertex.vertexIndex : "null") + "::";
		}

		/**
		 * Set _clippedVertices to contain the two ends of the portion of the Voronoi edge that is visible
		 * within the bounds.  If no part of the Edge falls within the bounds, leave _clippedVertices null. 
		 * @param bounds
		 * 
		 */
		internal function clipVertices(bounds:Rectangle):void
		{
			var xmin:Number = bounds.x;
			var ymin:Number = bounds.y;
			var xmax:Number = bounds.right;
			var ymax:Number = bounds.bottom;
			
			var vertex0:Vertex, vertex1:Vertex;
			var x0:Number, x1:Number, y0:Number, y1:Number;
			
			if (a == 1.0 && b >= 0.0)
			{
				vertex0 = _rightVertex;
				vertex1 = _leftVertex;
			}
			else 
			{
				vertex0 = _leftVertex;
				vertex1 = _rightVertex;
			}
		
			if (a == 1.0)
			{
				y0 = ymin;
				if (vertex0 != null && vertex0.y > ymin)
				{
					 y0 = vertex0.y;
				}
				if (y0 > ymax)
				{
					return;
				}
				x0 = c - b * y0;
				
				y1 = ymax;
				if (vertex1 != null && vertex1.y < ymax)
				{
					y1 = vertex1.y;
				}
				if (y1 < ymin)
				{
					return;
				}
				x1 = c - b * y1;
				
				if ((x0 > xmax && x1 > xmax) || (x0 < xmin && x1 < xmin))
				{
					return;
				}
				
				if (x0 > xmax)
				{
					x0 = xmax; y0 = (c - x0)/b;
				}
				else if (x0 < xmin)
				{
					x0 = xmin; y0 = (c - x0)/b;
				}
				
				if (x1 > xmax)
				{
					x1 = xmax; y1 = (c - x1)/b;
				}
				else if (x1 < xmin)
				{
					x1 = xmin; y1 = (c - x1)/b;
				}
			}
			else
			{
				x0 = xmin;
				if (vertex0 != null && vertex0.x > xmin)
				{
					x0 = vertex0.x;
				}
				if (x0 > xmax)
				{
					return;
				}
				y0 = c - a * x0;
				
				x1 = xmax;
				if (vertex1 != null && vertex1.x < xmax)
				{
					x1 = vertex1.x;
				}
				if (x1 < xmin)
				{
					return;
				}
				y1 = c - a * x1;
				
				if ((y0 > ymax && y1 > ymax) || (y0 < ymin && y1 < ymin))
				{
					return;
				}
				
				if (y0 > ymax)
				{
					y0 = ymax; x0 = (c - y0)/a;
				}
				else if (y0 < ymin)
				{
					y0 = ymin; x0 = (c - y0)/a;
				}
				
				if (y1 > ymax)
				{
					y1 = ymax; x1 = (c - y1)/a;
				}
				else if (y1 < ymin)
				{
					y1 = ymin; x1 = (c - y1)/a;
				}
			}

			_clippedVertices = new Dictionary(true);
			if (vertex0 == _leftVertex)
			{
				_clippedVertices[LR.LEFT] = new Point(x0, y0);
				_clippedVertices[LR.RIGHT] = new Point(x1, y1);
			}
			else
			{
				_clippedVertices[LR.RIGHT] = new Point(x0, y0);
				_clippedVertices[LR.LEFT] = new Point(x1, y1);
			}
		}

	}

class PrivateConstructorEnforcer {}