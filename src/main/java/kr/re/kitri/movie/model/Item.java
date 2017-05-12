package kr.re.kitri.movie.model;

import java.time.LocalDate;

/**
 * Created by danawacomputer on 2017-05-12.
 */
public class Item {
    private int itemId;
    private String title;
    private String link;
    private String image;
    private String subtitle;
    private LocalDate pubDate;
    private String director;
    private String actor;
    private double userRating;
    private int searchId;

    public Item() {}

    public Item(int itemId, String title, String link, String image, String subtitle, LocalDate pubDate, String director, String actor, double userRating, int searchId) {
        this.itemId = itemId;
        this.title = title;
        this.link = link;
        this.image = image;
        this.subtitle = subtitle;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.userRating = userRating;
        this.searchId = searchId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public LocalDate getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDate pubDate) {
        this.pubDate = pubDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", pubDate=" + pubDate +
                ", director='" + director + '\'' +
                ", actor='" + actor + '\'' +
                ", userRating=" + userRating +
                ", searchId=" + searchId +
                '}';
    }
}
