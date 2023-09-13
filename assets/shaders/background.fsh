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

// a - horizontal direction x, b - horizontal direction y
// c - vertical direction x, d - vertical direction y
uniform vec4 u_abcd;
uniform vec2 u_offset;
uniform vec2 u_origin;
uniform float u_scale;

void main() {
	vec2 rel_coord = gl_FragCoord.xy - u_origin; // -sx/2, -sy/2
	
	vec2 rotated = vec2(
		rel_coord.x * u_abcd[0] + rel_coord.y * u_abcd[2],
		rel_coord.x * u_abcd[1] + rel_coord.y * u_abcd[3]
	);

	// absolute world coordinate
	vec2 abs_coord = (rotated * u_scale + u_offset) / TileSize;

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