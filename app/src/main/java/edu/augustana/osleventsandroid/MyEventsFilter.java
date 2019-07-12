package edu.augustana.osleventsandroid;

public class MyEventsFilter implements EventFilter {

    private String user;
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MyEventsFilter(String user) {
        this.user = user;
    }

    @Override
    public boolean applyFilter(Event event) {
        if (!enabled) {
            return true;
        }
        return event.getFavoritedBy().containsKey(user);
    }
}
