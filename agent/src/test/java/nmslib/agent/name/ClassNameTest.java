/*
 *    Copyright 2021 Whilein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nmslib.agent.name;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author whilein
 */
final class ClassNameTest {

    private Name name;
    private Name strictName;
    private Name matchSubjectExpectTrue;
    private Name matchSubjectExpectFalse;
    private Name startsWithSubjectExpectTrue;
    private Name startsWithSubjectExpectFalse;

    @BeforeEach
    public void setup() {
        name = ClassName.of("com", "example", "nmslib", "#0");
        strictName = ClassName.of("com", "example", "nmslib", "V1_0");
        matchSubjectExpectTrue = ClassName.of("com", "example", "nmslib", "V1_0");
        matchSubjectExpectFalse = ClassName.of("net", "example", "nmslib", "V1_0");
        startsWithSubjectExpectTrue = ClassName.of("com", "example");
        startsWithSubjectExpectFalse = ClassName.of("net", "example");
    }

    @Test
    public void convertToString() {
        assertEquals("com.example.nmslib.#0", name.convertToString());
    }

    @Test
    public void convertToInternalString() {
        assertEquals("com/example/nmslib/#0", name.convertToInternalString());
    }

    @Test
    public void format() {
        assertEquals(strictName, name.format("V1_0"));
    }

    @Test
    public void startsWith() {
        assertTrue(name.startsWith(startsWithSubjectExpectTrue));
        assertFalse(name.startsWith(startsWithSubjectExpectFalse));
    }

    @Test
    public void matches() {
        assertEquals(1, strictName.matches(matchSubjectExpectTrue));
        assertEquals(0.75, name.matches(matchSubjectExpectTrue));
        assertEquals(0, name.matches(matchSubjectExpectFalse));
    }

}