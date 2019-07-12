package edu.augustana.osleventsandroid;

public class SearchMultiFieldFilter implements EventFilter {

    private String lowerCaseQuery;
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SearchMultiFieldFilter(String query) {
        this.lowerCaseQuery = query.toLowerCase();
    }

    @Override
    public boolean applyFilter(Event event) {
        if (!enabled) {
            return true;
        }

        if (event.getName().toLowerCase().contains(lowerCaseQuery) ||
                event.getLocation().toLowerCase().contains(lowerCaseQuery) ||
                event.getTags().toLowerCase().contains(lowerCaseQuery) ||
                event.getOrganization().toLowerCase().contains(lowerCaseQuery)) {
            return true;
        }

        return false;
    }
}
