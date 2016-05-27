import static org.lwjgl.opengl.GL11.*; 

public class GLGraphics 
{

	public static void renderArrow(Vec2 startPos, Vec2 endPos)
	{			
		//glBegin(GL_QUADS);
		double dy = endPos.y - startPos.y;
		double dx = endPos.x - startPos.x;	
		double theta = (dx < 0 ? Math.PI + Math.atan(dy/dx) : Math.atan(dy/dx)) ;
		double len = Math.sqrt(dy*dy + dx * dx);
		lineSegment(startPos, 30, len, - Math.PI/2 + theta);
	}
	
	public static void lineSegment(Vec2 start, double width, double height, double theta_rad)
	{
		// basic rectangle from 2 triangles
		// triangle 1	
		double x0 = start.x; double y0 = start.y;
		double x1 = start.x + width; double y1 = y0;
		double x2 = start.x; double y2 = y0+height;

		double x1r = (x1 -start.x)* Math.cos(theta_rad) - (y1-start.y) * Math.sin(theta_rad);
		double x2r = (x2-start.x) * Math.cos(theta_rad) - (y2-start.y) * Math.sin(theta_rad);
		x1r += start.x; x2r += start.x;
		
		double y1r = (x1-start.x) * Math.sin(theta_rad) + (y1-start.y) * Math.cos(theta_rad);
		double y2r = (x2-start.x) * Math.sin(theta_rad) + (y2-start.y) * Math.cos(theta_rad);
		y1r += start.y; y2r += start.y;
		
		// triangle 2
		double xx0 = x1; double yy0 = y0;
		double xx1 = x1; double yy1 = y0+height;
		double xx2 = xx0-width; double yy2 = y0 + height;
		
		double xx0r = (xx0 - start.x) * Math.cos(theta_rad) - (yy0 - start.y) * Math.sin(theta_rad);
		double xx1r = (xx1 - start.x)* Math.cos(theta_rad) - (yy1 - start.y) * Math.sin(theta_rad);
		double xx2r = (xx2 - start.x) * Math.cos(theta_rad) - (yy2 - start.y) * Math.sin(theta_rad);
		xx0r += start.x; xx1r += start.x; xx2r += start.x;
		
		double yy0r = (xx0 - start.x)* Math.sin(theta_rad) + (yy0 - start.y) * Math.cos(theta_rad);
		double yy1r = (xx1 - start.x)* Math.sin(theta_rad) + (yy1 - start.y)* Math.cos(theta_rad);
		double yy2r = (xx2 - start.x)* Math.sin(theta_rad) + (yy2 - start.y)* Math.cos(theta_rad);
		yy0r += start.y; yy1r += start.y; yy2r += start.y;
		
		glEnable(GL_BLEND); // blend to remove ugly piece background
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		glLoadIdentity();

		glBegin(GL_TRIANGLES);	
		glColor4f(0.3f, 0.5f, 0.3f, 0.7f);
		
		glVertex2d(x0, y0); glVertex2d(x1r, y1r); glVertex2d(x2r, y2r);
		glVertex2d(xx0r, yy0r); glVertex2d(xx1r, yy1r); glVertex2d(xx2r, yy2r);
		
		glEnd();
		glPopMatrix();
		
		glDisable(GL_BLEND);
		glColor4f(1f, 1f, 1f, 1f);
	}
}
