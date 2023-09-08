#pragma optimize(off) // Disable optimization for this shader

attribute vec4 a_position;

uniform mat4 u_projTrans;

// these are passing to fragment shader
uniform vec2 u_offset;
uniform vec2 u_horizonat_inc;
uniform vec2 u_vertical_inc;
uniform vec2 u_screen_size;
uniform float u_zoom_factor;

void main() {
	gl_Position =  u_projTrans * a_position;
}