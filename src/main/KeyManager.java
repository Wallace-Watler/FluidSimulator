package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.stream.IntStream;

public class KeyManager implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			IntStream
				.range(0, TemperatureGrid.grid.length)
				.parallel()
				.forEach(
					j -> IntStream
							.range(0, TemperatureGrid.grid[0].length)
							.parallel()
							.forEach(i -> TemperatureGrid.grid[i][j] *= 0.9)
				);
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			IntStream
				.range(0, TemperatureGrid.grid.length)
				.parallel()
				.forEach(
					j -> IntStream
							.range(0, TemperatureGrid.grid[0].length)
							.parallel()
							.forEach(i -> {
									TemperatureGrid.grid[i][j] *= 1.1;
									if(TemperatureGrid.grid[i][j] > 10000) TemperatureGrid.grid[i][j] = 10000;
								}
							)
				);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_T) Main.toggleHeatDisplay();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
