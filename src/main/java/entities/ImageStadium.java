package entities;

import java.util.Objects;

public class ImageStadium {
    private String ref,name,url,type;

    public ImageStadium() {
    }

    public ImageStadium(String ref, String name, String url, String type) {
        this.ref = ref;
        this.name = name;
        this.url = url;
        this.type = type;
    }

    public ImageStadium(String name, String url, String type) {
        this.name = name;
        this.url = url;
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageStadium that = (ImageStadium) o;
        return Objects.equals(ref, that.ref);
    }

    @Override
    public String toString() {
        return "ImageStadium{" +
                "ref='" + ref + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
