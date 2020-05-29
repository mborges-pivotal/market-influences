package com.borgescloud.markets.news.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsView extends News {

    List<Content> content;
    List<Media> media;
    List<Link> links;
    List<Import> imports;

    public void addContent(Content c) {
        if (content == null) {
            content = new ArrayList<Content>();
        }
        content.add(c);
    }

    public void addMedia(Media m) {
        if (media == null) {
            media = new ArrayList<Media>();
        }
        media.add(m);
    }

    public void addLink(Link l) {
        if (links == null) {
            links = new ArrayList<Link>();
        }
        links.add(l);
    }

    public void addImport(Import i) {
        if (imports == null) {
            imports = new ArrayList<Import>();
        }
        imports.add(i);
    }

    @Data
    @Builder
    public static class Content {
        private String tag;
        private String text;
    }

    @Data
    @SuperBuilder
    public static class Media {
        private String tag;
        private String src;

    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Image extends Media {
        private String width;
        private String height;
        private String alt;
    }


    @Data
    @Builder
    public static class Link {
        private String tag;
        private String href;
        private String text;
    }

    @Data
    @Builder 
    public static class Import {
        private String tag;
        private String href;
        private String rel;
    }

    public static NewsView copy(News n) {
        NewsView nv = new NewsView();
        nv.setId(n.getId());
        nv.setFeedId(n.getFeedId());
        nv.setTitle(n.getTitle());
        nv.setPublishedDate(n.getPublishedDate());
        nv.setDescription(n.getDescription());
        nv.setLink(n.getLink());
        return nv;
    }

}
