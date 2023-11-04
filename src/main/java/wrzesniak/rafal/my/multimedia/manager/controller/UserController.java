package wrzesniak.rafal.my.multimedia.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamoService;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.ListDto;

import java.util.Comparator;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final ContentListDynamoService contentListDynamoService;

    @GetMapping("/lists")
    public List<ListDto> getGeneralListsInfo() {
        log.info("Starts fetching basic list info");
        List<ContentListDynamo> allContentLists = contentListDynamoService.getAllContentLists();
        return allContentLists.stream()
                .map(ListDto::new)
                .sorted(Comparator.comparing(ListDto::getName))
                .toList();
    }

}
