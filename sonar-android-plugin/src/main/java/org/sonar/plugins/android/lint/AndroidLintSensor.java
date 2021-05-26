/*
 * SonarQube Android Lint Plugin
 * Copyright (C) 2013-2016 SonarSource SA and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.android.lint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.plugins.android.AndroidPlugin;

import java.io.File;

public class AndroidLintSensor implements Sensor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidLintSensor.class);

  private final SensorContext context;

  public AndroidLintSensor(SensorContext context) {
    this.context = context;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor
            .onlyOnLanguages("java", "xml")
            .name("AndroidLint")
            .onlyOnFileType(InputFile.Type.MAIN)
            .onlyWhenConfiguration(config -> config.hasKey(AndroidPlugin.LINT_REPORT_PROPERTY));
  }

  @Override
  public void execute(SensorContext context) {
    File lintReport = getFile(
            context.config()
                    .get(AndroidPlugin.LINT_REPORT_PROPERTY)
                    .orElse(AndroidPlugin.LINT_REPORT_PROPERTY_DEFAULT)
    );
    new AndroidLintProcessor(context).process(lintReport);
  }

  private File getFile(String path) {
    try {
      File file = new File(path);
      if (!file.isAbsolute()) {
        file = new File(this.context.fileSystem().baseDir(), path).getCanonicalFile();
      }
      return file;
    } catch (Exception e) {
      LOGGER.warn("Lint report not found, please set {} property to a correct value.", AndroidPlugin.LINT_REPORT_PROPERTY);
      LOGGER.warn("Unable to resolve path : "+path, e);
    }
    return null;
  }
}
