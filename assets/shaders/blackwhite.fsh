#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;
varying vec2 v_repeat;

uniform sampler2D u_texture;

void main()
{
	vec2 tmp = v_texCoords * v_repeat;
	tmp = fract(tmp);
	gl_FragColor = texture2D(u_texture, tmp);
}