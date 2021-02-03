package net.fabricmc.example;

public class EasyBedConfig {
	private Float percentage;
	public EasyBedConfig(Float percentage)
	{
		this.percentage = percentage;
	}
	
	public Float getPercentage()
	{
		return percentage;
	}
	
	public static EasyBedConfig defaultConfig()
	{
		return new EasyBedConfig(0.5f);
	}
}
