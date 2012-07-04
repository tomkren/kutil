package net.phys2d.raw.test;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.SlideJoint;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.Body;

/**
 * A test to demonstrate the SlidingJoint - wierd results! :)
 * 
 * @author guRuQu
 */
public class SlideTest extends AbstractDemo {
	
	/**
	 * Create a new test
	 */
	public SlideTest(){
		super("Slider Test");
	}
	
	/**
	 * @see net.phys2d.raw.test.AbstractDemo#init(net.phys2d.raw.World)
	 */
	protected void init(World world) {
		Body knot = new StaticBody(new Circle(10));
		knot.setPosition(250,100);
		world.add(knot);
		
		int N=5;
		Body bodies[] = new Body[N];
		for(int i=0;i<N;i++){
			Body ball = new Body(new Circle(5),10);
			ball.setDamping(0.01f);
			ball.setPosition(280+i*30,100);
			world.add(ball);
			bodies[i]=ball;
		}
		
		for(int i=1;i<N;i++){
			SlideJoint sj = new SlideJoint(bodies[i-1],bodies[i],new Vector2f(),new Vector2f(),40,80,1f);
			world.add(sj);
		}
		SlideJoint sj = new SlideJoint(knot,bodies[0],new Vector2f(),new Vector2f(),10,80,1f);
		world.add(sj);
	}
	

	/**
	 * Entry point to the test
	 * 
	 * @param args The arguments passed to the test
	 */
	public static void main(String[] args) {
		(new SlideTest()).start();
	}

}
