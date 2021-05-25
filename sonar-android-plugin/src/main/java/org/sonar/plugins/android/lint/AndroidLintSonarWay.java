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
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.ValidationMessages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class AndroidLintSonarWay implements BuiltInQualityProfilesDefinition {
  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidLintSonarWay.class);
  public static final String PROFILE_XML_PATH = "/org/sonar/plugins/android/lint/android_lint_sonar_way.xml";

  private final AndroidLintProfileImporter profileImporter;

  public AndroidLintSonarWay(final AndroidLintProfileImporter profileImporter) {
    this.profileImporter = profileImporter;
  }

  @Override
  public void define(Context context) {
    LOGGER.info("Creating Objective-C Profile");

    NewBuiltInQualityProfile nbiqp = context.createBuiltInQualityProfile("Android Lint", "java");
    nbiqp.setDefault(true);

    try(Reader config = new InputStreamReader(getClass().getResourceAsStream(PROFILE_XML_PATH))) {
      RulesProfile ocLintRulesProfile = this.profileImporter.importProfile(config, ValidationMessages.create());
      for (ActiveRule rule : ocLintRulesProfile.getActiveRules()) {
        nbiqp.activateRule(rule.getRepositoryKey(), rule.getRuleKey());
      }
    } catch (IOException ex){
      LOGGER.error("Error Creating AndroidLint Profile",ex);
    }
    nbiqp.done();
  }
}
