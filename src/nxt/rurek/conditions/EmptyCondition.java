package nxt.rurek.conditions;

import nxt.rurek.position.Situation;

public class EmptyCondition extends Condition{
	@Override
	public boolean check(Situation s) {
		return true;
	}
	
}
