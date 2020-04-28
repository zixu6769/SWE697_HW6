package mcas;

import static org.junit.Assert.*;
import org.junit.Before;

import mcas.Mcas.State;
import mcas.Mcas.Command;

import org.junit.Test;

public class McasTest {

	Mcas  mcasTest;
	boolean autoPilot;
	boolean flapsDown;
	double angleOfAttack;
	
	State state;
	Command trimResult;
	
	@Before
	public void init() {
		mcasTest = new Mcas();
		
	}
	
	/*************************************************************************
	 *     D1 - ((state == State.INACTIVE) && !autopilotOn && !flapsDown)    *
	 *************************************************************************/
	// TTF = F  =>state remains inactive	
	@Test
	public void testRow4() {
		autoPilot = true;
		flapsDown = false;
		angleOfAttack = 1.0;		
		trimResult = mcasTest.trim(autoPilot, flapsDown, angleOfAttack);	
		state = mcasTest.getState();
		assertTrue(state==State.INACTIVE);
	}
	
	// (FFF = F) => (state becomes ARMED)
	@Test
	public void testRow7() {
		assertTrue(mcasTest.getState() == State.INACTIVE); 	// a
		autoPilot = false; 									// b
		flapsDown = false; 									// c
		angleOfAttack = 1.0; 								// make sure D3 is not triggered
		mcasTest.trim(autoPilot, flapsDown, angleOfAttack); // run the method
		assertTrue(mcasTest.getState() == State.ARMED);		// state should be ARMED
	}
	
	/*************************************************************************
	 *     D2 - ((state == State.ARMED) && (autopilotOn || flapsDown))       *
	 *************************************************************************/
	// TTF = T =>state becomes inactive
	@Test
	public void testRow12() {
		autoPilot = false;
		flapsDown = false;
		angleOfAttack = 1.0;
		trimResult = mcasTest.trim(autoPilot, flapsDown, angleOfAttack);
		autoPilot = true;
		trimResult = mcasTest.trim(autoPilot, flapsDown, angleOfAttack);	
		state = mcasTest.getState();
		assertTrue(state==State.INACTIVE);
	}
	
	// (FTF = F) => (state remains either INACTIVE or ACTIVE)
	@Test
	public void testRow16_case1() {
		assertTrue(mcasTest.getState() == State.INACTIVE); 	// a = INACTIVE
		autoPilot = true; 									// b
		flapsDown = false; 									// c
		angleOfAttack = Math.random()*100; 					// AOA doesn't matter
		mcasTest.trim(autoPilot, flapsDown, angleOfAttack); // run the method
		assertTrue(mcasTest.getState() == State.INACTIVE);	// state should not change
	}
	
	@Test
	public void testRow16_case2() {
		// conditions to trigger D1
		assertTrue(mcasTest.getState() == State.INACTIVE); 	// T
		autoPilot = false; 									// F
		flapsDown = false;									// F
		angleOfAttack = 1.0;								// make sure D3 is not triggered
		mcasTest.trim(autoPilot, flapsDown, angleOfAttack); // run the method
		
		// conditions to trigger D3
		assertTrue(mcasTest.getState() == State.ARMED);		// state becomes ARMED
		angleOfAttack = 20.0;								// AOA is over threshold
		mcasTest.trim(autoPilot, flapsDown, angleOfAttack); // run the method again
		
		// now we can test row16
		assertTrue(mcasTest.getState() == State.ACTIVE); 	// a = ACTIVE
		autoPilot = true; 									// b
		flapsDown = false; 									// c
		angleOfAttack = Math.random()*100; 					// AOA doesn't matter
		mcasTest.trim(autoPilot, flapsDown, angleOfAttack); // run the method
		
		// D4 is triggered, gives the same result as if D2 is triggered. Infeasible?
		assertTrue(mcasTest.getState() == State.INACTIVE);
	}
		
		
	

}
