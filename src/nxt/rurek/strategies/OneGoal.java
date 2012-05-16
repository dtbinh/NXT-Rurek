package nxt.rurek.strategies;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import nxt.rurek.Direction;
import nxt.rurek.Environment;
import nxt.rurek.conditions.BackForBall;
import nxt.rurek.conditions.DontSeeBall;
import nxt.rurek.conditions.EmptyCondition;
import nxt.rurek.conditions.HasBall;
import nxt.rurek.conditions.NeedComute;
import nxt.rurek.conditions.SemiHasBall;
import nxt.rurek.conditions.isCharging;
import nxt.rurek.conditions.isTooClose;
import nxt.rurek.geometry.Functions;
import nxt.rurek.geometry.Point;
import nxt.rurek.movement.PositionController;
import nxt.rurek.position.Situation;


public class OneGoal extends Strategy {
	
	private final int debug = 1;
	
	
	/** Tries to defend goal without suicide */
	private void defenceMode (Situation s) {
		Point target = Functions.getFountain();
		isTooClose isTooCloseCond = new isTooClose(Functions.getLeftCorner());
		
		Point me = new Point (s.getPp().getPose());
		rotateTo(me.getAngle(target), s);
		me = new Point (s.getPp().getPose());
		
		while (!isTooCloseCond.check(s)) {
			s.getDp().travel(me.getDistance(target), true);
			while(s.getDp().isMoving()) {
				if (isTooCloseCond.check(s)) {
					break;
				}
			}
			goTo(Functions.getFountain(), s, new EmptyCondition());
			rotateTo(90, s);
		}
	}
	
	private boolean aroundFromLeft(Situation s) {
		Direction bd = s.getBl().getAny();
		if (!bd.isInRange()) return false;
		return bd.toPoint(s.getPp().getPose()).getX() < Environment.getEnvironment().getWidth() / 2;
	}
	
	
	
	@Override
	public void playWith(PositionController move) {
		Situation s = new Situation(move.getBallListener(), move.getDifferentialPilot(), move.getNavigator().getPoseProvider());
		Point destination = new Point (Environment.getEnvironment().getWidth()/2, Environment.getEnvironment().getHeight() + 10);
		Direction bd; /* place to store ball direction */
		HasBall hasBallCond = new HasBall();
		SemiHasBall semiHasBallCond = new SemiHasBall();
		DontSeeBall dontSeeBallCond = new DontSeeBall();	
		BackForBall backForBallCond = new BackForBall();
		NeedComute needComuteCond = new NeedComute();
		isTooClose isTooCloseCond = new isTooClose(Functions.getTarget());
		isCharging isChargingCond = new isCharging(destination);
		int try_cnt;
		
		while (dontSeeBallCond.check(s));
		
		Point b, me;
		Point trg = Functions.getTarget();
		
		while (!Button.ENTER.isDown()) {
			
			/*LCD.clear(4);
			LCD.drawInt((int)bd.getAngle(), 0, 4);
			LCD.drawInt((int)bd.getDistance(), 4, 4);
			LCD.drawInt((int)b.getX(), 8, 4);
			LCD.drawInt((int)b.getY(), 12, 4);
			break;*/
			
			bd = s.getBl().getLast();
			try_cnt = 0;
			while (!bd.isInRange()) {
				if (try_cnt > 2) {
					goTo(Functions.getFountain(), s, dontSeeBallCond);
				}
				rotate(getRotateDir(s.getPp().getPose())*360, s, dontSeeBallCond);
				try_cnt++; 
				bd = s.getBl().getLast();
			}
			me = Functions.fromPose(s.getPp().getPose());
			b = bd.toPoint(s.getPp().getPose());
			
			forceUpdateBall(s);
			
			/*if (isChargingCond.check(s) && hasBallCond.check(s)) {
				LCD.drawInt(0, 15, 3);
				if (debug > 0) LCD.drawString("   " + 1 +  "   ", 0, 3);
		    	double angle = getChargeAngle(s.getPp().getPose(), trg);
		    	rotateWithBallTo(angle, s);
		    	me = Functions.fromPose(s.getPp().getPose());
				s.getDp().travel(me.getDistance(trg), true);
				while(s.getDp().isMoving()) {
					if (!semiHasBallCond.check(s)) {
						s.getDp().stop();
						break;
					} else if (!isChargingCond.check(s)) {
						s.getDp().stop();
						angle = getChargeAngle(s.getPp().getPose(), trg);
				    	rotateWithBallTo(angle, s);
				    	s.getDp().travel(me.getDistance(trg), true);
					} else if (isTooCloseCond.check(s)) {
						defenceMode(s);
					}
				}
			} else */ if (hasBallCond.check(s)) {
				/* obracam się w stronę bramki */
				double angle = getChargeAngle(s.getPp().getPose(), trg)-90;
		    	rotateWithBallTo(convertRotation(angle, s.getPp().getPose().getHeading()), s);
		    	break;
			}
			/*else if (backForBallCond.check(s)) {
				LCD.drawInt(1, 14, 3);
			    
				boolean fromLeft = aroundFromLeft(s);
				double wantedAngle;
				
				while (backForBallCond.check(s)) {
					bd = s.getBl().getLast();
					if (!bd.isInRange()) break;
					me = new Point(s.getPp().getPose());
					b = bd.toPoint(s.getPp().getPose());
					wantedAngle = me.getAngle(b) + (fromLeft ? -90 : 90);
					rotateTo(wantedAngle, s);
					double angle;
					if (fromLeft) {
						if (me.getAngle(b) >= 270) angle = 360 - me.getAngle(b);
						else angle = 90 - me.getAngle(b); 
					} else {
						if (me.getAngle(b) >= 270) angle = 180 + ( 90 - (360 - me.getAngle(b)));
						else angle = me.getAngle(b) - 90;
					}
					s.getDp().arc(25 * (fromLeft ? 1 : -1) , angle);
					rotateTo(90,s);
				}
			} */ else /* if (needComuteCond.check(s)) */ {
				bd = s.getBl().getLast();
				
				LCD.drawInt(2, 14, 3);
				b = bd.toPoint(s.getPp().getPose());
				me = new Point(s.getPp().getPose()); 
				rotateTo(bd.getAngle(), s);
				
				s.getDp().travel(bd.getDistance()+5, true);
				while (s.getDp().isMoving()) {
					forceUpdateBall(s);
					bd = s.getBl().getLast();
					if (!bd.isInRange()) break;
					b = bd.toPoint(s.getPp().getPose());
					me = new Point(s.getPp().getPose());
					int xxx = (int) Math.abs(me.getAngle(b) - s.getPp().getPose().getHeading());
					if (min(xxx, 360-xxx) > 30) {
						s.getDp().stop();
						break;
					}
				}
				LCD.drawInt(1, 6, 3);
			}
			
			
		}
		
		
		
		
	}
}
