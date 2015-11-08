package pt.ua.engserv.safeguard.settings;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

import pt.ua.engserv.safeguard.R;

/**
 * Created by luis on 10/19/13.
 */
public class ApplicationStatus implements Cloneable, Serializable {

    protected String label;
    protected Drawable icon;
    protected boolean enabled;

    public ApplicationStatus(String label, Drawable icon, boolean enabled) {

        this.label = label;
        this.icon = icon;
        this.enabled = enabled;
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

    @Override
    public ApplicationStatus clone() {

        return new ApplicationStatus(label, icon, enabled);
    }

}
