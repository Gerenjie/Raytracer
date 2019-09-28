import java.awt.*;

public class Polygon extends Material {
   
   public double [] slope = new double [3];
   public double d;
   public Edge [] edges;
   public double [] [] vertices;
   double [] mins = new double [3];
   double [] maxes = new double [3];
   double [] centroid = new double [3];


    public Polygon (double [][] points, Color c, double ref, double tran, double refr) {
      Vector v1 = new Vector (points[0], Vector.minus(points[0], points[1]));
      Vector v2 = new Vector (points[0], Vector.minus(points[0], points[2]));
      double [] slope1 = v1.getSlope();
      double [] slope2 = v2.getSlope();
      slope[0] = slope1[1]*slope2[2]-slope1[2]*slope2[1];
      slope[1] = -slope1[0]*slope2[2]-slope1[2]*slope2[0];
      slope[2] = slope1[0]*slope2[1]-slope1[1]*slope2[0];
      slope = Vector.unit(slope);
      d = -(slope[0]*points[0][0]+slope[1]*points[0][1]+slope[2]*points[0][2]);
      color = c;
      reflectivity = 1-ref;
      transparency = tran;
      refractivity = refr;
      vertices = points;
      edges = new Edge [points.length];
      for (int a = 0; a < edges.length; a++) {
         if (points[a][0] < mins[0]) {
            mins[0] = points[a][0];
         }
         if (points[a][0]>maxes[0]) {
            maxes[0] = points[a][0];
         }
         if (points[a][1]<mins[1]) {
            mins[1] = points[a][1];
         }
         if (points[a][1]>maxes[1]) {
            maxes[1] = points[a][1];
         }
         if (points[a][2]<mins[2]) {
            mins[2] = points[a][2];
         }
         if (points[a][2]>maxes[2]) {
            maxes[2] = points[a][2];
         }
         double [] edgeVector = Vector.minus(points[(a+1)%points.length], points[a]);
         edges[a] = new Edge (points[a], edgeVector, Vector.magnitude(edgeVector));
      }
      for (double [] point : points) {
         centroid = Vector.plus(centroid, point);
      }
      centroid = Vector.scale(centroid, 1.0/points.length);

      
      
      
   }
   
   public double [] getCentroid () {
      return centroid;
   }
   
   public double [] getNormal (double [] intersectionPoint) {
      return Vector.unit(slope);
   }
   
   public double [] getSlope() {
      return slope;
   }
   
   public double getD () {
      return d;
   }
   
   public Edge [] getEdges() {
      return edges;
   }
   
   public double [] [] getPoints () {
      return vertices;
   }
   
   public double [] getMins () {
      return mins;
   }
   public double [] getMaxes () {
      return maxes;
   }
   
   
   
   
   
}
   