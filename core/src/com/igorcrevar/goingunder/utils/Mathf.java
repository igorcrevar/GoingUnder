package com.igorcrevar.goingunder.utils;

public class Mathf {
	public static float lerp(float a, float b, float time){
		return lerp(a, b, time, false);
	}
	
	public static float lerp(float a, float b, float time, boolean inverse) {
		if (time < 0.0f) time = 0.0f;
		if (time > 1.0f) time = 1.0f;
		return !inverse ? a + (b - a) * time : b + (a - b) * time;
	}
	
	public static float lerpBI(float a, float b, float time) {
		if (time < 0.0f) time = 0.0f;
		if (time > 1.0f) time = 1.0f;
		if (time <= 0.5f) {
			return a + (b - a) * time * 2.0f;
		}
		
		return b + (a - b) * (time - 0.5f) * 2.0f;
	}
}
