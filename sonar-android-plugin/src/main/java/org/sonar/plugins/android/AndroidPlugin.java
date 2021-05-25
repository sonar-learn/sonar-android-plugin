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
package org.sonar.plugins.android;

import org.sonar.api.Plugin;
import org.sonar.api.Property;
import org.sonar.plugins.android.lint.AndroidLintProfileExporter;
import org.sonar.plugins.android.lint.AndroidLintProfileImporter;
import org.sonar.plugins.android.lint.AndroidLintRulesDefinition;
import org.sonar.plugins.android.lint.AndroidLintSensor;
import org.sonar.plugins.android.lint.AndroidLintSonarWay;

import java.util.Arrays;

@Property(
  key = AndroidPlugin.LINT_REPORT_PROPERTY,
  defaultValue = AndroidPlugin.LINT_REPORT_PROPERTY_DEFAULT,
  name = "Lint Report file",
  description = "Path (absolute or relative) to the lint-results.xml file.",
  project = true,
  module = true,
  global = false)
public class AndroidPlugin implements Plugin {

  public static final String LINT_REPORT_PROPERTY = "sonar.android.lint.report";
  public static final String LINT_REPORT_PROPERTY_DEFAULT = "build/outputs/lint-results.xml";

  @Override
  public void define(Context context) {
    context.addExtensions(
            Arrays.asList(
                    AndroidLintProfileExporter.class,
                    AndroidLintProfileImporter.class,
                    AndroidLintRulesDefinition.class,
                    AndroidLintSensor.class,
                    AndroidLintSonarWay.class
            )
    );
  }
}
