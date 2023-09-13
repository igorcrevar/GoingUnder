#pragma optimize(off) // Disable optimization for this shader

attribute vec4 a_position;

uniform mat4 u_projTrans;

// these are passing to fragment shader
//uniform vec4 u_abcd;
//uniform vec2 u_x0y0;
//uniform vec2 u_offset;

void main() {
	gl_Position =  u_projTrans * a_position;
}