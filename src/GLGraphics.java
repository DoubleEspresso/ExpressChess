import static org.lwjgl.opengl.GL11.*; 

public class GLGraphics 
{

	public static void renderArrow(Vec2 startPos, Vec2 endPos)
	{		
		System.out.println("start = " + startPos.x + " end pos = " + endPos.x);
		glPushMatrix();
		glEnable(GL_BLEND); // blend to remove ugly piece background
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);		
		glBegin(GL_QUADS);
		
		glColor4f(1f, 1f, 1f, 0.8f);
		glVertex2d(startPos.x, startPos.y); 
		glVertex2d(startPos.x, endPos.y); 
		glVertex2d(endPos.x, endPos.y); 
		glVertex2d(startPos.x, endPos.y); 
		
		glDisable(GL_BLEND);
		glEnd();
		glPopMatrix();
	}
	
}
