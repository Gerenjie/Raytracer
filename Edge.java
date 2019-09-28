public class Edge extends Vector {
   double length;
   
   public Edge (double [] pos, double [] Slop, double len) {
      super (pos, Slop);
      length = len;
   }
   public Edge (double xp, double yp, double zp, double xs, double ys, double zs, double len) {
      super (xp, yp, zp, xs, ys, zs);
      length = len;
   }
   public double getLength() {
      return length;
   }
   
}