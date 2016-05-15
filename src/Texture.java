
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import static org.lwjgl.opengl.GL11.*;


public class Texture {
    
	private int Width;
	private int Height;
	private int [] pixelData;
	private BufferedImage image;
	private int texture_id;
	private ByteBuffer buffer;
	
	// public c'tor, loads the texture file
	public Texture(String filename)
	{
		try {
			if (!Load(filename)) {
				System.out.println("..[Texture] failed to load texture " + filename);
			}
		} catch (Exception any) {
			System.out.println("..[Texture] Exception loading texture: " + any.getMessage());
		}
	}
	
	// specifies color key to be used for the texture.
	public void SetColorKey(char Red, char Green, char Blue)
	{
		
	}

	public void AddReference() {};
	public void ReleaseReference() {};
	public void Bind() 
	{
        glBindTexture(GL_TEXTURE_2D, texture_id); 
        
        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer); 
	}	
	public int GetWidth() { return Width; }
	public int GetHeight() { return Height; }	
	public int GetID() { return texture_id; }
	
	// load a texture file
	Boolean Load(String fname)
	{
		// generate new image id 
		try
		{
			image = ImageIO.read(new File(fname));//ImageIO.read(Texture.class.getResource(fname));
			pixelData = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixelData, 0, image.getWidth());
			
			buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB
			
            for(int j = 0; j < image.getHeight(); ++j){
                for(int i = 0; i < image.getWidth(); ++i){
                    int pixel = pixelData[j * image.getWidth() + i];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));             // Blue component
                    buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha component. Only for RGBA
                }
            }
            
            buffer.flip();           
            texture_id = glGenTextures();
		}
		catch(Exception any)
		{
			System.out.println("..[Texture] Load exception: " + any.getMessage());
			return false;
		}
		
		return true;
	}

}
