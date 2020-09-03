package com.example.nasa_observations_bmd;

import java.util.ArrayList;

public class Asset {

    static class Collection {
        static class Item {
            private String href;

            public String getHref() {
                return href;
            }
        }

        private String href;
        private ArrayList<Item> items;
        private String version;

        public String getHref() {
            return href;
        }

        public ArrayList<Item> getItems() {
            return items;
        }

        public String getVersion() {
            return version;
        }
    }

    private Collection collection;

    public Collection getCollection() {
        return collection;
    }
}
