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
	
	@Test
	public void testRow4() {
		autoPilot = true;
		flapsDown = false;
		angleOfAttack = 1.0;
		
		trimResult = mcasTest.trim(autoPilot, flapsDown, angleOfAttack);
		
		state = mcasTest.getState();
		assertTrue(state==State.INACTIVE);

	}
	
	//((state == State.ARMED) && (autopilotOn || flapsDown)) TTF = T =>state becomes inactive
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
		
		
	

}
