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
		lineSegment(startPos, 20, len, - Math.PI/2 + theta);
	}
	
	public static void lineSegment(Vec2 start, double width, double height, double theta_rad)
	{
		// basic rectangle from 2 triangles
		// triangle 1	
		double x0 = start.x; double y0 = start.y;
		double x1 = start.x + width; double y1 = y0;
		double x2 = start.x; double y2 = y0+height;
//
//		double x1r = (x1 -start.x)* Math.cos(theta_rad) - (y1-start.y) * Math.sin(theta_rad);
//		double x2r = (x2-start.x) * Math.cos(theta_rad) - (y2-start.y) * Math.sin(theta_rad);
//		x1r += start.x; x2r += start.x;
//		
//		double y1r = (x1-start.x) * Math.sin(theta_rad) + (y1-start.y) * Math.cos(theta_rad);
//		double y2r = (x2-start.x) * Math.sin(theta_rad) + (y2-start.y) * Math.cos(theta_rad);
//		y1r += start.y; y2r += start.y;
//		
//		// triangle 2
//		double xx0 = x1; double yy0 = y0;
//		double xx1 = x1; double yy1 = y0+height;
//		double xx2 = xx0-width; double yy2 = y0 + height;
//		
//		double xx0r = (xx0 - start.x) * Math.cos(theta_rad) - (yy0 - start.y) * Math.sin(theta_rad);
//		double xx1r = (xx1 - start.x)* Math.cos(theta_rad) - (yy1 - start.y) * Math.sin(theta_rad);
//		double xx2r = (xx2 - start.x) * Math.cos(theta_rad) - (yy2 - start.y) * Math.sin(theta_rad);
//		xx0r += start.x; xx1r += start.x; xx2r += start.x;
//		
//		double yy0r = (xx0 - start.x)* Math.sin(theta_rad) + (yy0 - start.y) * Math.cos(theta_rad);
//		double yy1r = (xx1 - start.x)* Math.sin(theta_rad) + (yy1 - start.y)* Math.cos(theta_rad);
//		double yy2r = (xx2 - start.x)* Math.sin(theta_rad) + (yy2 - start.y)* Math.cos(theta_rad);
//		yy0r += start.y; yy1r += start.y; yy2r += start.y;
		
		glEnable(GL_BLEND); // blend to remove ugly piece background
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		glLoadIdentity();
		glTranslatef((float)start.x,(float)start.y,0f);		
		glRotatef((float)(theta_rad*180/Math.PI), 0f,0f,1f);
		glTranslatef(-(float)start.x,(float)-start.y,0f);
		
		glBegin(GL_TRIANGLES);	
		glColor4f(0.2f, 0.2f, 0.7f, 0.7f);
		
		//glVertex2d(x0, y0); glVertex2d(x1r, y1r); glVertex2d(x2r, y2r);
		//glVertex2d(xx0r, yy0r); glVertex2d(xx1r, yy1r); glVertex2d(xx2r, yy2r);
		
		glVertex2d(x0, y0); glVertex2d(x1, y1); glVertex2d(x2, y2);
		glVertex2d(x1, y0); glVertex2d(x1, y2); glVertex2d(x1-width, y2);
			
		glEnd();
		glPopMatrix();
		
		glPushMatrix();
		glTranslatef((float)start.x,(float)start.y,0f);		
		glRotatef((float)(theta_rad*180/Math.PI), 0f,0f,1f);
		glTranslatef(-(float)start.x,(float)-start.y,0f);
		glBegin(GL_TRIANGLES);
		glVertex2d(x1+15, y2); glVertex2d((x1+x2)/2f, y2+40); glVertex2d(x2-15, y2);
		glEnd();
		render_rounded_corner(new Vec2((x1+x2)/2f, y1+0.15), 10f, 6);
		glPopMatrix();
		

		glDisable(GL_BLEND);
		glColor4f(1f, 1f, 1f, 1f);
		
	}
	
	private static void render_rounded_corner(Vec2 center, float radius, int type)
	{
		int nb_points = 200; // default
		Vec2[] points = rounded_corner( center,  radius,  nb_points, type);
		
		// default color will be white (alpha = 0) ??
		glBegin(GL_TRIANGLE_FAN);
		for (int j = 0; j < nb_points; ++j) {

			glVertex3f((float)points[j].x, (float)points[j].y, 0f);

		}
		glEnd();
	}
	
	private static Vec2[] rounded_corner(Vec2 center, float radius, int nb_points, int type)
	{

		float start_theta = 0; float end_theta = 90; //defaults
		
		switch (type) {
		case 1: {
			start_theta = 0;
			end_theta = 90; // 1st quadrant of a circle;
			break;
		}
		case 2: {
			start_theta = 90;
			end_theta = 180; // 2nd quadrant of a circle;
			break;
		}
		case 3: {
			start_theta = 180;
			end_theta = 270; // 3rd quadrant of a circle;
			break;
		}
		case 4: {
			start_theta = 270;
			end_theta = 360; // 4th quadrant of a circle;
			break;
		}
		case 5: {
			start_theta = 0; end_theta = 180; break;
		}
		case 6: {
			start_theta = 180; end_theta = 360; break;
		}
		}
		
		float dtheta = (end_theta - start_theta) / nb_points;
		Vec2[] points = new Vec2[nb_points+1];
		points[0] = new Vec2(center.x, center.y);
		
		
		// compute the points, and return a list of them to openGL
		for (int j = 1; j < nb_points+1; ++j) {
			double theta = (start_theta + j * dtheta) * Math.PI / 180.0;
			points[j] = new Vec2(0,0);
			points[j].x = (float) (center.x + radius * Math.cos(theta));
			points[j].y = (float) (center.y + radius * Math.sin(theta));
		}
		
		return points;
	}
}
