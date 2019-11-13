import java.util.ArrayList;

class UiSpeedUpButton extends UiElement{
	double speedMultiplier;
	UiSpeedUpButton(RectObj p, boolean show, ArrayList<UiElement> ui, Environment env, double speedMultiplier) {
		super(p, show, ui, env);
		this.speedMultiplier = speedMultiplier; 
	}
	
	public void onClick() {
		super.onClick();
		if(spriteNum == 1) {
			turnOffSpeed();
		} else {
			turnOffAllOtherButtons();
			turnOnSpeed();
		}
	}
	
	public void turnOffSpeed() {
		spriteNum = 0;
		clicked = false;
		env.changeSpeedMultiplierInEnv(1);
	}
	
	public void turnOnSpeed() {
		clicked = true;
		spriteNum = 1;
		env.changeSpeedMultiplierInEnv(speedMultiplier);
	}
	
	public void turnOffAllOtherButtons() {
		for (UiElement obj : ui) {
			if(obj instanceof UiSpeedUpButton && obj != this) {
				((UiSpeedUpButton) obj).turnOffSpeed();
			}
		}
	}
}
