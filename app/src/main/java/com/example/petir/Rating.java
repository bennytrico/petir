package com.example.petir;

public class Rating {
    private Integer count_order;
    private Double average_rating, rating_montir;

    public Rating() {

    }
    public Rating(Double rating_montir, Integer count_order, Double average_rating) {
        this.rating_montir = rating_montir;
        this.count_order = count_order;
        this.average_rating = average_rating;
    }

    public Double getRating_montir() {
        return rating_montir;
    }

    public void setRating_montir(Double rating_montir) {
        this.rating_montir = rating_montir;
    }

    public Double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Double average_rating) {
        this.average_rating = average_rating;
    }

    public Integer getCount_order() {
        return count_order;
    }

    public void setCount_order(Integer count_order) {
        this.count_order = count_order;
    }
}
