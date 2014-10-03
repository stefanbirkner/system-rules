/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>.
 */

package org.junit.contrib.java.lang.system;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@code ProvideTimeZoneTest} tests {@link ProvideTimeZone}.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
@RunWith(Parameterized.class)
public class ProvideTimeZoneTest {
    @Rule
    public final ProvideTimeZone rule;
    private final TimeZone timeZone;

    @Parameters(name = "{0}")
    public static Collection<String[]> data() {
        final String[] ids = TimeZone.getAvailableIDs();
        final List<String[]> timeZones = new ArrayList<String[]>(ids.length);
        for (final String id : ids)
            timeZones.add(array(id));
        return timeZones;
    }

    public ProvideTimeZoneTest(final String id) {
        timeZone = TimeZone.getTimeZone(id);
        rule = new ProvideTimeZone(timeZone);
    }

    @Test
    public void shouldProvideTimeZoneA() {
        assertThat(TimeZone.getDefault(), is(equalTo(timeZone)));
    }

    private static <T> T[] array(final T... elements) {
        return elements;
    }
}
