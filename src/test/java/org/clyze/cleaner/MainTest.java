package org.clyze.cleaner;

import org.junit.Test;

public class MainTest {
    @Test public void test() {
        Main.main(new String[] {"src/test/resources"});
        assert true;
    }
}
