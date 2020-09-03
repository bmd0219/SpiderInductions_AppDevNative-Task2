package com.example.nasa_observations_bmd;

import java.util.ArrayList;

public class SearchResult {
    static class Collection {

        static class Metadata {
            private int total_hits;

            public int getTotal_hits() {
                return total_hits;
            }
        }

        static class Item {

            static class Data {
                private String center;
                private String description;
                private String description_508;
                private ArrayList<String> keywords;
                private String location;
                private String media_type;
                private String nasa_id;
                private String photographer;
                private String secondary_creator;
                private String title;
                private String year_start;
                private String year_end;

                public String getCenter() {
                    return center;
                }

                public String getDescription() {
                    return description;
                }

                public String getDescription_508() {
                    return description_508;
                }

                public ArrayList<String> getKeywords() {
                    return keywords;
                }

                public String getLocation() {
                    return location;
                }

                public String getMedia_type() {
                    return media_type;
                }

                public String getNasa_id() {
                    return nasa_id;
                }

                public String getPhotographer() {
                    return photographer;
                }

                public String getSecondary_creator() {
                    return secondary_creator;
                }

                public String getTitle() {
                    return title;
                }

                public String getYear_start() {
                    return year_start;
                }

                public String getYear_end() {
                    return year_end;
                }
            }

            static class Link {
                private String href;
                private String render;
                private String rel;

                public String getHref() {
                    return href;
                }

                public String getRender() {
                    return render;
                }

                public String getRel() {
                    return rel;
                }
            }

            private String href;
            private ArrayList<Data> data;
            private ArrayList<Link> links;

            public String getHref() {
                return href;
            }

            public ArrayList<Data> getData() {
                return data;
            }

            public ArrayList<Link> getLinks() {
                return links;
            }
        }

        static class Link {
            private String prompt;
            private String href;
            private String rel;

            public String getPrompt() {
                return prompt;
            }

            public String getHref() {
                return href;
            }

            public String getRel() {
                return rel;
            }
        }

        private Metadata metadata;
        private String href;
        private ArrayList<Item> items;
        private String version;
        private ArrayList<Link> links;

        public Metadata getMetadata() {
            return metadata;
        }

        public String getHref() {
            return href;
        }

        public ArrayList<Item> getItems() {
            return items;
        }

        public String getVersion() {
            return version;
        }

        public ArrayList<Link> getLinks() {
            return links;
        }
    }

    private Collection collection;

    public Collection getCollection() {
        return collection;
    }
}
