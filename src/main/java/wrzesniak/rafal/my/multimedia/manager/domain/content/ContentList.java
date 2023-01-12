package wrzesniak.rafal.my.multimedia.manager.domain.content;

import java.util.List;

public interface ContentList<T> {

    String LIST_NAME_REGEX = "^[\\w ]{4,50}$";
    String LIST_NAME_MESSAGE = "List name must be between 4-50 characters and contain letters, spaces and digits only";

    List<T> getContent();
    String getName();

}
