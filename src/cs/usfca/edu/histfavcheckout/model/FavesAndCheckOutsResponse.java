package cs.usfca.edu.histfavcheckout.model;

import java.util.List;

public class FavesAndCheckOutsResponse {
    private int checkouts;
    private List<Favorites> favorites;

    public int getCheckouts() {
        return checkouts;
    }

    public void setCheckouts(int checkouts) {
        this.checkouts = checkouts;
    }

    public List<Favorites> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorites> favorites) {
        this.favorites = favorites;
    }
}
