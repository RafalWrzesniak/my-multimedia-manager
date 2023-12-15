package wrzesniak.rafal.my.multimedia.manager.domain.movie.objects;

public record SeriesInfo(int seasonsCount,
                         int allEpisodesCount) {

    public SeriesInfo(int seasonsCount, int allEpisodesCount) {
        this.seasonsCount = seasonsCount == 0 ? 1 : seasonsCount;
        this.allEpisodesCount = allEpisodesCount;
    }
}
