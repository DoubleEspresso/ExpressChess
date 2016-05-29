import java.lang.Math;

public class Vec2 
{

   protected double x;
   protected double y;

   public Vec2() 
   {
      x = y = 0;
   }
   
   public Vec2(Vec2 src)
   {
	   x = src.x; y = src.y;
   }

   public Vec2( double[] arr)
   {
	   if (arr.length < 2)  x = y = 0.0;
	   else
		   { x = arr[0]; y = arr[1]; }		   
   }
   
   public Vec2( double x, double y ) 
   {
      this.x = x;
      this.y = y;
   }

   public double length() 
   {
      return Math.sqrt( x*x + y*y );
   }

   public Vec2 add( Vec2 v1 ) 
   {
	   Vec2 v2 = new Vec2( this.x + v1.x, this.y + v1.y );
       return v2;
   }

   public Vec2 sub( Vec2 v1 ) 
   {
	   Vec2 v2 = new Vec2( this.x - v1.x, this.y - v1.y );
       return v2;
   }

   public Vec2 mult( double scaleFactor ) 
   {
	   Vec2 v2 = new Vec2( this.x*scaleFactor, this.y*scaleFactor );
       return v2;
   }

   public void set(double x, double y)
   {
	   this.x = x; this.y = y;
   }
   
   public void set(Vec2 other)
   {
	   this.x = other.x; this.y = other.y;
   }
   
   public Vec2 normalize() 
   {
      double l = this.length();     
      if (l == 0) return new Vec2();
	  else return new Vec2(this.x/l, this.y/l);
   }   

   public Boolean equals(Vec2 other)
   {
	   return (x == other.x && y == other.y);
   }
   
   public double dot(Vec2 v1 ) 
   {
	   return this.x*v1.x + this.y*v1.y;
   }
   
   public double angle_deg(Vec2 v2)
   {
	   double l1 = this.length();
	   double l2 = v2.length();
	   if (l1 == 0 || l2 == 0) return 90;
	   else return this.dot(v2) / ( l1 * l2 ) * 180.0 / Math.PI;
   }
   
   public double angle_theta(Vec2 v2)
   {
	   double l1 = this.length();
	   double l2 = v2.length();
	   if (l1 == 0 || l2 == 0) return Math.PI / 2.0;
	   else return this.dot(v2) / ( l1 * l2 );  
   }
   
   
}