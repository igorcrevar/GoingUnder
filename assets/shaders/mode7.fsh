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
uniform vec2 u_screen_size;
uniform vec2 u_origin;
uniform float u_scale;
uniform mat4 u_height;
uniform int u_map_index;

void main() {
	vec2 rel_to_origin = gl_FragCoord.xy - u_origin;

	vec2 rotated = vec2(
		rel_to_origin.x * u_abcd[0] + rel_to_origin.y * u_abcd[2],
		rel_to_origin.x * u_abcd[1] + rel_to_origin.y * u_abcd[3]
	);

	float y = 1.0;
	float z = u_screen_size.y - rel_to_origin.y; // bottom should be closer (larger)
	vec2 projected = vec2(rotated.x / z * u_scale, y / z * rotated.y * u_scale);

	vec2 coord = (projected + u_offset) / TileSize;

	vec2 tile_uv = fract(coord);
	tile_uv.y = 1.0 - tile_uv.y;    // flip v in uv texture space
	tile_uv = tile_uv / TilesPerRow;
	
	vec2 map_uv = fract(floor(coord) / MapTextureSize);
	vec4 map_data = texture2D(u_texture_map, map_uv);
	float tile_index = map_data[u_map_index]; 

	tile_index = tile_index * 255.0 / TilesPerRow; // scale to 255 and divide by 16

	tile_uv += vec2(
		fract(tile_index) * TilesPerRow, 
		floor(tile_index)
	) * TileSize / TileTextureSize;

	gl_FragColor = texture2D(u_texture_tile, tile_uv);
}