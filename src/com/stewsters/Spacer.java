package com.stewsters;

import processing.core.PApplet;
import processing.core.PGraphics;
import fisica.FCircle;
import fisica.FPoly;
import fisica.FWorld;
import fisica.Fisica;
import geomerative.RG;
import geomerative.RPoint;
import geomerative.RShape;

public class Spacer extends PApplet {

	FWorld world;
	String filename = "../assets/shipAlign.svg";
	FSVG ship;

	public void setup() {
		size(600, 600);
		smooth();

		frameRate(60);

		Fisica.init(this);
		Fisica.setScale(10);

		RG.init(this);

		RG.setPolygonizer(RG.ADAPTATIVE);

		world = new FWorld();
		world.setEdges(this, color(0));
		world.setGravity(0, 0);

		ship = createWingedStar(width / 2, height / 2);
	}

	public void draw() {
		background(255);

		world.draw(this);
		world.step();
	}

	public void mousePressed() {
		if (world.getBody(mouseX, mouseY) == null) {
			// createWingedStar(mouseX, mouseY);
			fireRocket(mouseX, mouseY, random(TWO_PI), 300);
		}
	}

	public void keyPressed() {

		if (key == ' ') {
			fireRocket(ship.getX(), ship.getY(), ship.getRotation(), 300);
		}
		float thrust = 2000.f;
		switch (key) {
		case ('w'):
		case ('W'):
			ship.addForce(thrust * cos(ship.getRotation()),
					thrust * sin(ship.getRotation()));
			break;
		case ('d'):
		case ('D'):
			ship.adjustAngularVelocity(20 / ship.getMass());
			break;
		case ('s'):
		case ('S'):
			ship.addForce(-thrust * cos(ship.getRotation()),
					-thrust * sin(ship.getRotation()));
			break;
		case ('a'):
		case ('A'):
			ship.adjustAngularVelocity(-20 / ship.getMass());
			break;
		}
		// switch(key) {
		// case('w'):case('W'):result ^=NORTH;break;
		// case('d'):case('D'):result ^=EAST;break;
		// case('s'):case('S'):result ^=SOUTH;break;
		// case('a'):case('A'):result ^=WEST;break;
		// }

		// saveFrame("screenshot.png");
	}

	FSVG createWingedStar(float x, float y) {
		float angle = random(TWO_PI);
		float magnitude = 200;

		FSVG obj = new FSVG(filename);
		obj.setPosition(x, y);
		obj.setRotation(angle + PI / 2);
		obj.setVelocity(magnitude * cos(angle), magnitude * sin(angle));
		obj.setDamping(0);
		obj.setRestitution((float) 0.9);
		obj.setGrabbable(false);
		world.add(obj);
		return obj;
	}

	void fireRocket(float startX, float startY, float rotation, float speed) {
		FCircle rocket = new FCircle(5);
		rocket.setPosition(startX, startY);
		rocket.setVelocity(speed * cos(rotation), speed * sin(rotation));
		rocket.setFriction(0);
		rocket.setRestitution(1.f);
		rocket.setDensity(10.f);
		world.add(rocket);
	}

	class FSVG extends FPoly {
		RShape m_shape;

		float w = 100;
		float h = 100;

		FSVG(String filename) {
			super();

			RShape fullSvg = RG.loadShape(filename);
			m_shape = fullSvg.getChild("layer1");
			RShape outline = fullSvg.getChild("layer1");

			if (m_shape == null || outline == null) {
				println("ERROR: Couldn't find the shapes called 'object' and 'outline' in the SVG file.");
				return;
			}

			// Make the shapes fit in a rectangle of size (w, h)
			// that is centered in 0
			m_shape.transform(-w / 2, -h / 2, w / 2, h / 2);
			outline.transform(-w / 2, -h / 2, w / 2, h / 2);

			RPoint[] points = outline.getPoints();

			if (points == null)
				return;

			for (int i = 0; i < points.length; i++) {
				this.vertex(points[i].x, points[i].y);
			}

			this.setNoFill();
			this.setNoStroke();
		}

		public void draw(PGraphics applet) {
			preDraw(applet);
			m_shape.draw(applet);
			postDraw(applet);
		}
	}

}
