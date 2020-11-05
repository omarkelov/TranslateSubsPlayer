package ru.nsu.fit.markelov;

import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.markelov.subtitles.TagFilter;

import java.util.HashSet;

public class TagFilterTest {
    @Test
    public void test() {
        Assert.assertEquals("<i>Lorem</i> ipsum <i>dolor</i> <u>sit</u> amet, consectetur <u>adipiscing</u> elit",
            TagFilter.filter("{\\a}<i>Lorem</i> ipsum <b><a href=''>{i}dolor{/i}</a></b> <u>sit</u> amet, <div>consectetur</div> {u}adipiscing{/u} {div}elit{/div}",
            new HashSet<>(){{add("i");add("u");}}));
    }
}
