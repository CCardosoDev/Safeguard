package pt.ua.engserv.safeguard.settings;

import java.io.Serializable;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApplicationStatus implements Cloneable, Serializable, Comparable<ApplicationStatus> {

    private String label;
    private Drawable icon;
    private boolean enabled;
    private Intent launcherIntent;

    public ApplicationStatus(String label, Drawable icon, boolean enabled, Intent launcherIntent) {

        this.label = label;
        this.icon = icon;
        this.enabled = enabled;
        this.launcherIntent = launcherIntent;
    }

    public String getLabel() {

        return label;
    }

    public Drawable getIcon() {

        return icon;
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public Intent getLauncherIntent() {
		return launcherIntent;
	}

	@Override
    public ApplicationStatus clone() {

        return new ApplicationStatus(label, icon, enabled, launcherIntent);
    }

	@Override
	public int compareTo(ApplicationStatus another) {
		
		return label.compareTo(another.getLabel());
	}

}
