#ifdef GL_ES
	precision mediump float;
#endif

const float PI = 3.14159265359;
const float TileTextureSize = 512.0;
const float MapTextureSize = 128.0;
const float TileSize = 32.0;
const float TilesPerRow = TileTextureSize / TileSize; // 16

uniform sampler2D u_texture_tile;
uniform sampler2D u_texture_map;

uniform vec2 u_offset;
uniform vec2 u_horizonat_inc;
uniform vec2 u_vertical_inc;
uniform vec2 u_screen_size;
uniform float u_zoom_factor;

void main() {
	vec2 rel_coord = gl_FragCoord.xy - u_screen_size * 0.5; // -sx/2, -sy/2
	
	// absolute world coordinate
	vec2 abs_coord = ((vec2(
		(rel_coord.y * u_vertical_inc.x + rel_coord.x * u_horizonat_inc.x),
		(rel_coord.y * u_vertical_inc.y + rel_coord.x * u_horizonat_inc.y)
	)) * u_zoom_factor + u_offset) / TileSize;

	vec2 tileUV = fract(abs_coord);
	tileUV.y = 1.0 - tileUV.y;
	tileUV = tileUV / TilesPerRow;
	
	vec2 mapUV = fract(floor(abs_coord) / MapTextureSize);
	vec4 mapData = texture2D(u_texture_map, mapUV);
	float tileIndex = mapData.x; 

	tileIndex = tileIndex * 255.0 / TilesPerRow; // scale to 255 and divide by 16
	vec2 mapOffset = vec2(
		fract(tileIndex) * TilesPerRow, 
		floor(tileIndex)
	) * TileSize / TileTextureSize;

	tileUV += mapOffset;

	gl_FragColor = texture2D(u_texture_tile, tileUV);
}