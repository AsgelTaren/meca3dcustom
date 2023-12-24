package meca3dcustom.app;

import meca3dcustom.math.Matrix;

public class CustomSimu {

	private Model model;
	private Matrix[] position, speed;
	private double h = 1.0 / 60.0;

	public CustomSimu(Model model) {
		this.model = model;
	}

}