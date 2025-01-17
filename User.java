import java.util.*;

public class User {
    private final String uuid;
    private final List<Link> links;

    public User() {
        this.uuid = UUID.randomUUID().toString();
        this.links = new ArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        links.add(link);
    }
}
