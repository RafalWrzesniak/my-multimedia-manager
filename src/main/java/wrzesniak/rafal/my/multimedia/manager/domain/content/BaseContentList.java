package wrzesniak.rafal.my.multimedia.manager.domain.content;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@With
@Data
@Builder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseContentList<T> implements ContentList<T> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Pattern(regexp = LIST_NAME_REGEX, message = LIST_NAME_MESSAGE)
    private String name;

    @ManyToMany
    private List<T> contentList;

    public BaseContentList(String listName) {
        this.name = listName;
        this.contentList = new ArrayList<>();
    }

    @Override
    public List<T> getContent() {
        return new ArrayList<>(contentList);
    }

    public boolean addContent(T content) {
        if(contentList.contains(content)) {
            return false;
        }
        return contentList.add(content);
    }

    public void removeContent(T content) {
        contentList.remove(content);
    }
}
