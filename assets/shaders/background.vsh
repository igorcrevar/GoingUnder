#pragma optimize(off) // Disable optimization for this shader

attribute vec4 a_position;

uniform mat4  u_projTrans;

void main() {
	gl_Position =  u_projTrans * a_position;
}