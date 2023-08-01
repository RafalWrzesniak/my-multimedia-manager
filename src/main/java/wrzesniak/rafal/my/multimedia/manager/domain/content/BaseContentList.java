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
public class BaseContentList<T> {

    private static final String LIST_NAME_REGEX = "^[\\s\\p{L}\\w ]{4,50}$";
    private static final String LIST_NAME_MESSAGE = "List name must be between 4-50 characters and contain letters, spaces and digits only";

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Pattern(regexp = LIST_NAME_REGEX, message = LIST_NAME_MESSAGE)
    private String name;

    @ManyToMany
    private List<T> contentList;

    @Enumerated(EnumType.STRING)
    private ContentListType contentListType;

    private boolean isAllContentList;

    public BaseContentList(String listName, ContentListType contentListType) {
        this.name = listName;
        this.contentListType = contentListType;
        this.contentList = new ArrayList<>();
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

    public boolean contains(T content) {
        return contentList.contains(content);
    }

}
