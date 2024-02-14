//imports the necessary libraries
package edu.nyu.cs.recursion_exercise;

import processing.core.PApplet;
import processing.core.PConstants;

/** 
*  displays a Mandelbrot set that the user can zoom into repeatedly...
*
* @author  Foo Barstein with comments by Daniel Brito (db4471)
* @version 1.0
*/

public final class Mandelbrot extends PApplet {
	//stores maximum iterations of Mandelbrot 
	private int max = 64;

	//stores colors of Mandelbrot in a two dimensional array
	private float[][] colors = new float[48][3];

	//stores the x and y coordinates of the center of the current view
	private double viewX = 0.0;
	private double viewY = 0.0;

	//stores the zoom being used to zoom into the Mandelbrot set
	private double zoom = 1.0;

	//stores the x and y coordinates of the mouse when it was pressed
	private int mousePressedX;
	private int mousePressedY;

	//flag value to determine whether a new mandelbrot set is needed, such as when first running the program
	private boolean renderNew = true;

	//flag value that says whether or not the user is clicking for a box to then zoom into
	private boolean drawBox = false;

	/**
	 * Papplet method that sets the size of the window.
	 */
	public void settings() {
		this.size(600,400);
	}

	/**
	 * Papplet method that is called once when the program starts.
	 * generates colors.
	 */
	public void setup() {
		//generates a float array of colors to display the Mandelbrot set.
		for (int i = 0; i < colors.length; i++) {
			int c = 2 * i * 256 / colors.length;
			//if statement for if the calculation results in a value greater than 255, which wouldnt be on the rgb scale
			if (c > 255)
				c = 511 - c;
			float[] color = {c, c, c};
			this.colors[i] = color;
		}
	}
	/**
	 * Another Papplet method called every 1/60th of a second to display what is happening in the program.
	 */
	public void draw() {
		//checks if a new Mandelbrot set needs to be rendered or if a box should be drawn
		if (!renderNew && !this.drawBox) return;
		this.background(0, 0, 0);
		//checks if a box should be drawn, and draws it
		if (this.drawBox) {
			this.noFill();
			this.stroke(255, 0, 0);
			rect(this.mousePressedX, this.mousePressedY, this.mouseX - this.mousePressedX, this.mouseY - this.mousePressedY);
		}
		//iterates through the height window, and calculates the correct display/measurements for zooms
		for (int y = 0; y < this.height; y++) {
			//nested loop to iterate through the width of the window
			//together the loops iterate through the entire window
			for (int x = 0; x < this.width; x++) {
				double r = zoom / Math.min(this.width, this.height);
				double dx = 2.5 * (x * r + this.viewX) - 2.0;
				double dy = 1.25 - 2.5 * (y * r + this.viewY);
				int value = this.mandel(dx, dy);
				float[] color = this.colors[value % this.colors.length];
				this.stroke(color[0], color[1], color[2]);
				this.line(x, y, x, y);
			}
		}
		
		//formatting for the text that is displayed in the window
		this.textAlign(PConstants.CENTER);

		//text to help the user understand the program
		this.text("Click and drag to draw an area to zoom into.", this.width / 2, this.height-20);

		//flag value to say that a new Mandelbrot set is not to be rendered
		this.renderNew = false;
	}
	/**
	 * Another Papplet method called each time the mouse click is released. Stores the values of the location of this event.
	 * @param px the x coordinate of the pixel
	 * @param py the y coordinate of the pixel
	 * @return the number of iterations of the Mandelbrot set
	 */
	private int mandel(double px, double py) {
		//values are soubles due to the specifics of the Mandelbrot set, coordinates, and area calculations.
		//initializing the coordinates to a standard amount
		double zx = 0.0, zy = 0.0;
		//initializing the zoomed coordinates to a standard amount
		double zx2 = 0.0, zy2 = 0.0;
		//initializing the value of the number of iterations of the Mandelbrot set to 0, to then be incremented
		int value = 0;
		//zooms in and checks that the zooming in is valid, stores the variables
		while (value < this.max && zx2 + zy2 < 4.0) {
			//incrementing the values of the coordinates to go along with the repeated zooming in
			zy = 2.0 * zx * zy + py;
			zx = zx2 - zy2 + px;
			//the zoom is squared to zoom in
			zx2 = zx * zx;
			//same for the y coordinate
			zy2 = zy * zy;
			//incrementing the value of the number of iterations of the Mandelbrot set
			value++;
		}
		//returns the value of the max number of iterations of the Mandelbrot set
		return value == this.max ? 0 : value;
	}

	/**
	 * Another Papplet method called each time the mouse click is clicked. Stores the values of the location of this event.
	 */
	public void mousePressed() {
		//sets the coordinates based on the Papplet method mouse clicked
		this.mousePressedX = this.mouseX;
		this.mousePressedY = this.mouseY;

		//flag value to say that a new Mandelbrot set is to be rendered
		this.drawBox = true;
	}

	/**
	 * Another Papplet method called each time the mouse click is released. Stores the values of the location of this event.
	 */
	public void mouseReleased() {
		//sets the coordinates of the size of the box based on the Papplet method mouse released
		int mouseReleasedX = this.mouseX;
		int mouseReleasedY = this.mouseY; 
		//if statement to check the legitimacy of the box being made to zoom
		if (this.mouseButton == PConstants.LEFT) {
			//if statement to make sure there was a box drawn and not just a click in the same place that it was released, making a point
			if (mouseReleasedX != mousePressedX && mouseReleasedY != mousePressedY) {
				//setting of the width and height based on the mouse movements
				int w = this.width;
				int h = this.height;

				//setting of the view based on the mouse movements and the area calculation of the box
				this.viewX += this.zoom * Math.min(mouseReleasedX, mousePressedX) / Math.min(w, h);
				this.viewY += this.zoom * Math.min(mouseReleasedY, mousePressedY) / Math.min(w, h);

				//setting of the zoom based on the mouse movements and the area calculation of the box
				this.zoom *= Math.max((double)Math.abs(mouseReleasedX - mousePressedX) / w, (double)Math.abs(mouseReleasedY - mousePressedY) / h);
			}
		}
		//if statement to add to the max number of iterations of the Mandelbrot set 
		else if (this.mouseButton == PConstants.RIGHT) {
			this.max += max / 4;
		}
		//else statement to set the variables back to a standard amount/position for coordinates
		else {
			this.max = 64;
			this.viewX = this.viewY = 0.0;
			this.zoom = 1.0;
		}

		//flag values because if the if statements are satisfied, the box should not be drawn and a new Mandelbrot set should be rendered
		this.drawBox = false;
		this.renderNew = true;
	}

	/**
	 *Main method of the program. Calls other methods to run display the Mandelbrot set.
	 */
	public static void main(String[] args) {
		PApplet.main("edu.nyu.cs.recursion_exercise.Mandelbrot");
	}

   
	 
}