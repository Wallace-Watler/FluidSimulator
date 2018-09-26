package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.stream.IntStream;

public class TemperatureGrid {

	private static final double INITIAL_TEMPERATURE = 273;
	private static final double TEMP_COLOR_BASE = Math.log(3);
	private static final double TEMP_COLOR_SCALE = 100;
	public static final int GRID_COARSENESS = 3;
	
	public static double[][] grid;
	
	public static void init() {
		grid = new double[Main.WIDTH >> GRID_COARSENESS][Main.HEIGHT >> GRID_COARSENESS];
		IntStream
			.range(0, grid.length)
			.parallel()
			.forEach(
				j -> IntStream
						.range(0, grid[0].length)
						.parallel()
						.forEach(i -> grid[i][j] = INITIAL_TEMPERATURE)
			);
	}
	
	public static Color getTempColor(double temperature) {
		int red, green, blue;
		double spectrum = Math.log(temperature / TEMP_COLOR_SCALE) / TEMP_COLOR_BASE;
		spectrum++;
		if(spectrum < 0) spectrum = 0;
		else if(spectrum > 5) spectrum = 5;
		
		if(spectrum >= 3) {
			red = 255;
			if(spectrum < 4) {
				green = (int) (255 * (4 - spectrum));
				blue = 0;
			}else {
				green = 0;
				blue = (int) (255 * (spectrum - 4));
			}
		}else if(spectrum < 2) {
			red = 0;
			if(spectrum < 1) {
				green = (int) (255 * spectrum);
				blue = 255;
			}else {
				green = 255;
				blue = (int) (255 * (2 - spectrum));
			}
		}else {
			red = (int) (255 * (spectrum - 2));
			green = 255;
			blue = 0;
		}
		return new Color(red, green, blue);
	}
	
	public static void tick() {
		double[][] tempTemp = new double[grid[0].length][grid.length];
		IntStream
			.range(0, grid.length)
			.parallel()
			.forEach(
				j -> IntStream
						.range(0, grid[0].length)
						.parallel()
						.forEach(
								i -> {
									double newTemp = 0;
									for(int n = j - 1; n <= j + 1; n++) {
										if(n >= 0 && n < grid.length) {
											for(int m = i - 1; m <= i + 1; m++) {
												if(m >= 0 && m < grid[0].length) {
													if(n - j != 0 && m - i != 0) {
														newTemp += grid[m][n] / 36;
													}else if(n == j && m == i) {
														newTemp += grid[m][n] * 4 / 9;
													}else {
														newTemp += grid[m][n] / 9;
													}
												}
											}
										}
									}
									if(i == 0 || i == grid[0].length - 1) newTemp *= 6.0 / 5;
									if(j == 0 || j == grid.length - 1) newTemp *= 6.0 / 5;
									tempTemp[i][j] = newTemp;
								}
						)
			);
		IntStream
			.range(0, grid.length)
			.parallel()
			.forEach(
				j -> IntStream
						.range(0, grid[0].length)
						.parallel()
						.forEach(i -> grid[i][j] = tempTemp[i][j])
			);
	}
	
	public static void render(Graphics g) {
		for(int j = 0; j < grid.length; j++) {
			for(int i = 0; i < grid[0].length; i++) {
				Color c = getTempColor(grid[i][j]);
				g.setColor(new Color(c.getRed() / 2, c.getGreen() / 2, c.getBlue() / 2));
				g.fillRect(i << GRID_COARSENESS, Main.HEIGHT - (j << GRID_COARSENESS) - 36, 1 << GRID_COARSENESS, 1 << GRID_COARSENESS);
			}
		}
	}
}
