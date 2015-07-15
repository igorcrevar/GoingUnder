attribute vec4 a_position;

uniform mat4 u_projectionViewMatrix;
uniform vec4 u_color_factor;
// This will be passed into the fragment shader.
varying vec4 v_color;  
 
void main()
{          
	v_color = u_color_factor;                   
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
   	gl_Position = u_projectionViewMatrix * a_position;
}