package nxt.rurek;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;


public class MoveController {
	NXTRegulatedMotor left = Motor.A;
	NXTRegulatedMotor right = Motor.B;
	private static int max_speed = 100;
	
	
	public void goToPosition(Direction direction) {
		int left_speed = max_speed;
		int right_speed = max_speed;
		if(!direction.isInRange()) {
			left_speed = -1 * max_speed;
		}
		else {
			if(direction.getAngle() < 0){
				right_speed = max_speed;
				left_speed *= ((180 + direction.getAngle()) / 180);
			}
			else {
				right_speed = max_speed;
				left_speed *= ((180 - direction.getAngle()) / 180);
			}
		}
		left.setSpeed(left_speed);
		right.setSpeed(right_speed);
	}
	
}