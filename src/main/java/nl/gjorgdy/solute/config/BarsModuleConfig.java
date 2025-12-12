package nl.gjorgdy.solute.config;

public class BarsModuleConfig extends ToggleModuleConfig {

	public double targetVelocity;
	public double velocityModifier;

	public BarsModuleConfig(boolean enabled, double targetVelocity, double velocityModifier) {
		super(enabled);
		this.targetVelocity = targetVelocity;
		this.velocityModifier = velocityModifier;
	}

}
