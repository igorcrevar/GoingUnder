#ifdef GL_ES
	precision mediump float;
#endif

const float PI = 3.14159265359;

uniform vec3  u_color_top;
uniform vec3  u_color_bottom;
uniform vec2  u_screen;
uniform float u_offset;
uniform float u_repeat_factor;

void main() {
	vec2 pos = gl_FragCoord.xy / u_screen;
	pos.y += 0.00618125 * sin(pos.x * 32.92 + cos(pos.y * 59.78) * 0.9);
	
	float normalizedY = 1.0 - pos.y; // on top is 0, on bottom is 1
	normalizedY = fract(u_offset + normalizedY * u_repeat_factor); // max(u_repeat_factor, 0.5));
	float time = sin(PI * normalizedY); // 0-1 then 1-0
	vec3 color = mix(u_color_top, u_color_bottom, time);
	gl_FragColor = vec4(color, 1.0);
}