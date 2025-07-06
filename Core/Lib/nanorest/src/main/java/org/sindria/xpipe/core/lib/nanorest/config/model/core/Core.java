package org.sindria.xpipe.core.lib.nanorest.config.model.core;

public class Core {

    public Product product;

    public StoreView storeView;

    public Notifications notifications;

    public Github github;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public StoreView getStoreView() {
        return storeView;
    }

    public void setStoreView(StoreView storeView) {
        this.storeView = storeView;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public Github getGithub() {
        return github;
    }

    public void setGithub(Github github) {
        this.github = github;
    }



}
