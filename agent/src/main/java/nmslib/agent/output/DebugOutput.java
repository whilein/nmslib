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

package nmslib.agent.output;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author whilein
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DebugOutput implements Output {

    File classes;
    PrintStream log;

    public static Output create() throws IOException {
        val dir = new File("debug");
        dir.mkdir();

        val classesDir = new File(dir, "classes");
        classesDir.mkdir();

        val logFile = new File(dir, "output.log");

        return new DebugOutput(classesDir, new PrintStream(logFile));
    }

    @Override
    public void logClass(final byte[] bytecode, final String name) {
        val file = new File(classes, name.replace('/', '.') + ".class");

        try (val fos = new FileOutputStream(file)) {
            fos.write(bytecode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(final String text) {
        log.println(text);
        log.flush();
    }
}
